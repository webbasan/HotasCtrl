package de.mundito.app;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerHttpPort;
import de.mundito.args.ArgHandlerRegistry;
import de.mundito.args.Parameter;
import de.mundito.configuration.Configuration;
import de.mundito.hid.Hotas;
import de.mundito.hid.SetupHandler;
import de.mundito.hid.SetupHandlerRegistry;
import de.mundito.net.NetServer;
import de.mundito.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;


/**
 * User: webbasan Date: 01.05.15 Time: 23:25
 */
public final class HotasCtrl {

    public static final String APPLICATION_NAME = "HotasCtrl";
    public static final String APPLICATION_VERSION = "1.1";

    public static void main(String... args) {
        Configuration configuration = new Configuration(ArgHandlerRegistry.readArgs(args));
        if (!configuration.isValid()) {
            System.err.println(configuration.getInvalidMessage());
            printUsage();
            return;
        }

        HotasCtrl app = new HotasCtrl(configuration);
        try {
            app.init();
            app.doIt();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        finally {
            app.shutdown();
        }
    }


    private final Configuration configuration;

    private Hotas hotas;

    private NetServer netServer;
    private Thread netServerThread;

    public HotasCtrl(Configuration configuration) {
        this.configuration = configuration;

        this.netServer = null;
        this.netServerThread = null;
    }

    public void init() {
        this.hotas = this.configuration.createHotas();
        this.hotas.init();
    }

    public void doIt() {
        for (ArgHandler argHandler : this.configuration.getArgHandlers()) {
            SetupHandler setupHandler = SetupHandlerRegistry.getHandler(argHandler.getParameter());
            if (setupHandler != null) {
                setupHandler.setup(this.hotas, argHandler);
            }
        }
        this.hotas.update();

        // do something different while hotas does what it has to do?
        if (this.configuration.shouldEnableHttp()) {
            // open socket: expect to receive HTTP GET requests -> mapped to configuration commands
            // TODO: maybe "http-address" -> 'expert mode'?
            InetAddress serverAddress = InetAddress.getLoopbackAddress();
            int listenerPort = this.configuration.getHttpPort();
            startHttp(serverAddress, listenerPort);
        }

        if (this.configuration.shouldEnableConsole()) {
            // if in foreground: open input stream, waiting to get new configuration commands
            handleConsoleInput();
        }
        else if (this.configuration.shouldEnableDaemonMode()) {
            synchronized (this) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    // something woke us up...
                    Util.log(APPLICATION_NAME + ": awake.");
                }
            }
        }
        // check and wait until NetServer thread dies...
        waitForShutdown();
    }

    public void shutdown() {
        // FIXME: check - unclean shutdown in named pipe setup?
        if (this.netServer != null) {
            this.netServer.shutdown();
            waitForShutdown();
        }
        if (this.hotas != null) {
            this.hotas.shutdown();
            this.hotas = null;
        }
    }

    private void handleConsoleInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                String rawInput = reader.readLine();
                if (rawInput == null) {
//                    break; // EOF // TODO: causing bail-out when using named pipes... -> use switch for fg/bg mode...
                    continue; // EOF
                }
                else if (rawInput.equals("")) {
                    continue;
                }

                String input = rawInput.toLowerCase();
                if (input.startsWith("quit") || input.startsWith("exit") || input.startsWith("shutdown")) {
                    shutdown();
                    break; // bail out
                }
                else if (input.startsWith("help")) {
                    printUsage();
                }
                else {
                    // TODO: would be more convenient to allow multiple commands in one line...
                    // TODO: doesn't receive proper quoting via named pipes
                    boolean stateChanged = false;
                    try {
                        ArgHandler argHandler = ArgHandlerRegistry.readArg(rawInput.split(" ", 2));
                        if (argHandler != null && argHandler.isValid()) {
                            SetupHandler setupHandler = SetupHandlerRegistry.getHandler(argHandler.getParameter());
                            if (setupHandler != null) {
                                setupHandler.setup(this.hotas, argHandler);
                                stateChanged = true;
                            }
                            //---  handle args outside HOTAS scope  ---
                            if (argHandler instanceof ArgHandlerHttpPort) {
                                ArgHandlerHttpPort argHttpPort = (ArgHandlerHttpPort)argHandler;
                                int listenerPort = argHttpPort.getPort();
                                if (this.netServer != null
                                        && (listenerPort == -1 || listenerPort != this.netServer.getPort())) {
                                    // already running - either shutdown or re-configure request
                                    this.netServer.shutdown();
                                    waitForShutdown();
                                }
                                if (listenerPort != -1) {
                                    InetAddress serverAddress = InetAddress.getLoopbackAddress();
                                    startHttp(serverAddress, listenerPort);
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        System.out.println("Unable to handle input '" + rawInput + "': " + e);
                    }
                    finally {
                        if (stateChanged) {
                            this.hotas.update();
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error while reading input: " + e);
        }
    }

    private void startHttp(final InetAddress serverAddress, final int listenerPort) {
        this.netServer = new NetServer(this.hotas, serverAddress, listenerPort);
        this.netServerThread = new Thread(this.netServer);
        this.netServerThread.start();
    }

    private void waitForShutdown() {
        if (this.netServerThread != null) {
            Util.log("Waiting for HTTP thread to shutdown...");
            while (this.netServerThread.isAlive()) {
                try {
                    this.netServerThread.join();
                }
                catch (InterruptedException e) {
                    // nothing to do.
                }
            }
            this.netServerThread = null;
            this.netServer = null;
            Util.log("HTTP thread exited.");
        }
        if (this.configuration.shouldEnableDaemonMode()) {
            Util.log(APPLICATION_NAME + ": shutdown.");
        }
    }

    private static void printUsage() {
        StringBuilder usageDescription = new StringBuilder("Usage: HotasCtrl ");

        for (Parameter parameter : Parameter.values()) {
            usageDescription.append(getParameterUsage(parameter));
        }

        System.err.println(usageDescription);
    }

    private static String getParameterUsage(Parameter parameter) {
        return " [" + parameter.name().toLowerCase() + " " + parameter.getDescription() + "]";
    }

}
