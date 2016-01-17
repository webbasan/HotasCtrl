package de.mundito.configuration;

import de.mundito.args.ArgHandler;
import de.mundito.args.ArgHandlerHttpPort;
import de.mundito.args.Parameter;
import de.mundito.hid.Hotas;
import de.mundito.hid.HotasX52Daemon;
import de.mundito.hid.HotasX52Simple;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * User: webbasan Date: 01.05.15 Time: 23:26
 */
public class Configuration {

    private final List<ArgHandler> argHandlers;
    private final Map<Parameter, ArgHandler> handlerByParamter;


    public Configuration(final List<ArgHandler> argHandlers) {
        this.argHandlers = argHandlers;
        this.handlerByParamter = new HashMap<>(argHandlers.size());
        for (ArgHandler argHandler : argHandlers) {
            this.handlerByParamter.put(argHandler.getParameter(), argHandler);
        }
    }

    public List<ArgHandler> getArgHandlers() {
        return Collections.unmodifiableList(this.argHandlers);
    }

    public ArgHandler getArgHandler(final Parameter parameter) {
        return this.handlerByParamter.get(parameter);
    }
    
    public boolean isValid() {
        if (this.argHandlers.isEmpty()) {
            return false;
        }
        for (ArgHandler argHandler : this.argHandlers) {
            if (!argHandler.isValid()) {
                return false;
            }
        }
        return true;
    }

    public String getInvalidMessage() {
        StringBuilder result = new StringBuilder();
        for (ArgHandler argHandler : this.argHandlers) {
            if (!argHandler.isValid()) {
                if (result.length() != 0) {
                    result.append('\n');
                }
                result.append(argHandler.getInvalidValueMessage());
            }
        }
        return result.toString();
    }

    public Hotas createHotas() {
        // choose suitable Hotas implementation based on command arguments
        if (shouldEnableDaemonMode()) {
            return new HotasX52Daemon();
        }
        else {
            return new HotasX52Simple();
        }
    }

    public boolean shouldEnableDaemonMode() {
        boolean enableDaemonMode = false;
        for (ArgHandler argHandler : this.argHandlers) {
            enableDaemonMode = argHandler.getParameter().enableDaemonMode();
            if (enableDaemonMode) {
                break;
            }
        }
        return enableDaemonMode;
    }
    
    public boolean shouldEnableConsole() {
        return getArgHandler(Parameter.CONSOLE) != null;
    }

    public boolean shouldEnableHttp() {
        return getHttpPort() != -1;
    }

    public int getHttpPort() {
        ArgHandlerHttpPort argHttpPort = (ArgHandlerHttpPort)getArgHandler(Parameter.HTTP_PORT);
        return argHttpPort != null && argHttpPort.isValid() ? argHttpPort.getPort() : -1;
    }
}
