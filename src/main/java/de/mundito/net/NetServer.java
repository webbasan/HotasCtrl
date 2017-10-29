package de.mundito.net;

import de.mundito.app.HotasCtrl;
import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerRegistry;
import de.mundito.hid.Hotas;
import de.mundito.hid.SetupHandler;
import de.mundito.hid.SetupHandlerRegistry;
import de.mundito.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Simple network listener to receive configuration commands via HTTP GET requests.
 *
 * User: webbasan Date: 16.07.15 Time: 20:13
 */
public class NetServer
        implements Runnable
{
    private final Hotas hotas;

    private final InetAddress bindAddress;
    private final int servicePort;
    private final int backlog;

    private boolean keepListening;

    public NetServer(final Hotas hotas, final InetAddress bindAddress, final int servicePort) {
        this.hotas = hotas;

        this.bindAddress = bindAddress;
        this.servicePort = servicePort;
        this.backlog = 50;

        this.keepListening = false;
    }


    public void run() {
        log("startup.");
        this.keepListening = true;
        try {
            doIt();
        }
        catch (IOException ioerr) {
            log("IO Error: " + ioerr.getMessage());
        }
    }

    public void shutdown() {
        this.keepListening = false;
    }

    public InetAddress getAddress() {
        return this.bindAddress;
    }

    public int getPort() {
        return this.servicePort;
    }

    private void doIt()
            throws IOException {
        ServerSocket listenerSocket = new ServerSocket(this.servicePort, this.backlog, this.bindAddress);
        listenerSocket.setSoTimeout(30 * 1000); // wake up every 30 seconds

        while (this.keepListening) {
            try {
                log("awaiting connections.");
                Socket socket = listenerSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                boolean keepAlive = false;

                for (String requestLine = reader.readLine();
                     requestLine != null && !requestLine.equals("");
                     requestLine = reader.readLine()) {
                    log("received request '" + requestLine + "'");

                    if (requestLine.startsWith("GET")) {
                        // No REST API: GET based requests allows for undemanding clients and using path part for simplified parsing
                        // GET /light/all/on HTTP/1.0
                        // GET /led/a/red HTTP/1.0
                        // GET /text/line1\nline2\nline3 HTTP/1.0
                        // GET /line1/string HTTP/1.0
                        // GET /clock/local_24h HTTP/1.0

                        keepAlive = handleGetRequest(reader, writer, requestLine);
                    }
                    else if (requestLine.equals("HELLO")) {
                        String msg = "Hello World!";
                        log(" >> " + msg);
                        writer.println(msg);
                        writer.flush();
                    }
                    else if (requestLine.equals("QUIT")) {
                        this.keepListening = false;
                        writer.println("Good Bye!");
                        writer.flush();
                        log("received shutdown request for server.");
                    }
                    if (!keepAlive) {
                        // shutdown current connection
                        writer.flush();
                        reader.close();
                        writer.close();
                        socket.close();
                        log("connection closed.");
                        break; // exit request reader loop.
                    }
                }
            }
            catch (SocketTimeoutException timeout) {
                // check condition to continue
            }
        }

        listenerSocket.close();
        log("shutdown.");
    }

    private boolean handleGetRequest(final BufferedReader reader, final PrintWriter writer, final String requestLine)
            throws IOException {
        // read request header lines until empty line is found
        for (String requestHeader = reader.readLine();
             requestHeader != null && !requestHeader.equals("");
             requestHeader = reader.readLine()) {
            log("   " + requestHeader);

            // TODO: potentially interesting Request-Headers:
            //              Host: <appropriate server-name?>
            //              Keep-Alive: 300
            //              Connection: keep-alive
            // lesser interesting (logging; maybe when handling state info requests):
            //              User-Agent: ...
            //              Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
            //              Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7
        }

        // expecting: GET /arg/param1/param2 HTTP/1.0
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length > 1 && requestParts[1].length() > 1) {
            String command = requestParts[1];
            if (requestParts[1].startsWith("/")) {
                command = requestParts[1].substring(1);
            }

            Result result = handleCommand(java.net.URLDecoder.decode(command, "UTF-8"));
            sendResponse(writer, result);
        }
        // notify to close socket (ignoring keep-alive for now)
        return false;
    }

    private Result handleCommand(final String command) {
        Result result = null;
        boolean stateChanged = false;
        try {
            ArgHandler argHandler = ArgHandlerRegistry.getArgHandler(command.split("/"));

            if (argHandler != null) {
                if (argHandler.isValid()) {
                    SetupHandler setupHandler = SetupHandlerRegistry.getHandler(argHandler.getParameter());
                    if (setupHandler != null) {
                        setupHandler.setup(this.hotas, argHandler);
                        log("setup for '" + argHandler.getParameter() + "' done.");
                        stateChanged = true;
                    }
                    else {
                        // no setupHandler -- don't handle arguments out of HOTAS scope
                        log("Ignoring '" + argHandler.getParameter() + "': out of scope.");
                        result = Result.INVALID_ARGUMENTS;
                    }
                }
                else {
                    // invalid arguments: => 400 Bad Request
                    log("Invalid arguments for '" + argHandler.getParameter() + "'!");
                    result = Result.INVALID_ARGUMENTS;
                }
            }
            else {
                // no argHandler: => 404 Not Found
                log("Unknown command '" + command + "'!");
                result = Result.UNKNOWN_COMMAND;
            }
        }
        catch (Exception e) {
            log("Unable to handle command '" + command + "': " + e);
            // generic error (catch all to keep application running) => 500 Internal Server Error
            result = Result.INTERNAL_ERROR;
        }
        finally {
            if (stateChanged) {
                this.hotas.update();
                log("HOTAS updated.");
                // TODO: may be feedback when HOTAS is offline => 503 Service Unavailable
            }
            if (result == null) {
                result = Result.OK;
            }
        }
        log("result: '" + result.longMessage + "'");
        return result;
    }

    private void sendResponse(final PrintWriter writer, final Result result) {
        writer.printf("HTTP/1.0 %d %s\r\n", result.responseCode, result.responseMessage);
        writer.printf("Server: %s/%s\r\n", HotasCtrl.APPLICATION_NAME, HotasCtrl.APPLICATION_VERSION);
        writer.printf("Date: %s\r\n", getServerDate());
        writer.print("Content-Type: text/plain; charset=UTF-8\r\n");
        writer.print("Connection: close\r\n");
        writer.print("\r\n");
        writer.printf("%s\r\n", result.longMessage);

        writer.flush();
    }

    private void log(final String msg) {
        Util.log("Server: " + msg);
    }

    private static String getServerDate() {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }

}
