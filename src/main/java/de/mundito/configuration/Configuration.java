package de.mundito.configuration;

import de.mundito.args.ArgHandler;
import de.mundito.hid.Hotas;
import de.mundito.hid.HotasX52Daemon;
import de.mundito.hid.HotasX52Simple;

import java.util.Collections;
import java.util.List;


/**
 * User: webbasan Date: 01.05.15 Time: 23:26
 */
public class Configuration {

    private final List<ArgHandler> argHandlers;


    public Configuration(final List<ArgHandler> argHandlers) {
        this.argHandlers = argHandlers;
    }

    public List<ArgHandler> getArgHandlers() {
        return Collections.unmodifiableList(this.argHandlers);
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

    private boolean shouldEnableDaemonMode() {
        boolean enableDaemonMode = false;
        for (ArgHandler argHandler : this.argHandlers) {
            enableDaemonMode = argHandler.getParameter().enableDaemonMode();
            if (enableDaemonMode) {
                break;
            }
        }
        return enableDaemonMode;
    }
}
