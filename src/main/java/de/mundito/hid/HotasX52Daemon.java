package de.mundito.hid;

import de.mundito.args.Parameter;
import de.mundito.util.Util;
import org.usb4java.LibUsbException;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * User: webbasan Date: 10.07.15 Time: 14:41
 */
public class HotasX52Daemon
        implements Hotas
{
    private static final long SECOND_IN_MILLIS = 1000;
    private static final long MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;

    private static final long WAKE_UP_INTERVAL = MINUTE_IN_MILLIS; // Interval to check for HOTAS availability

    private final ParameterMappings parameterMappings;
    private final HotasState state;

    private UpdateTask updateTask;

    private Thread updaterThread;
    private ScheduledExecutorService executorService;

    private boolean useExecutorForUpdate;

    public HotasX52Daemon() {
        this.parameterMappings = new ParameterMappings();
        this.state = new HotasState();

        this.updateTask = null;

        this.updaterThread = null;
        this.executorService = null;

        this.useExecutorForUpdate = false;
    }

    @Override
    public void init() {
        this.state.init();
        enableDaemon();
    }

    @Override
    public void update() {
        if (this.updaterThread != null && this.updaterThread.isAlive()) {
            synchronized (this.state) {
                this.state.notify();
            }
        }
    }

    @Override
    public void setBrightness(final Parameter.LightSource lightSource, final Parameter.Brightness brightness) {
        this.state.setBrightness(lightSource, brightness);
    }

    @Override
    public void setBrightness(final Parameter.LightSource lightSource, final int brightnessValue) {
        this.state.setBrightness(lightSource, brightnessValue);
    }

    @Override
    public void setLedColor(final Parameter.Led led, final Parameter.LedColor color) {
        this.state.setLedColor(led, color);
    }

    @Override
    public void setText(final int lineNum, final String text) {
        this.state.setText(lineNum, text);
    }

    @Override
    public void enableClock(Parameter.ClockVariant clock) {
        this.state.enableClock(clock);
        if (!this.useExecutorForUpdate) {
            // if clock enabled -> create timer thread to trigger clock updates
            launchClockUpdater();
        }
    }

    @Override
    public void shutdown() {
        disableDaemon();
        this.state.shutdown();
    }

    private void enableDaemon() {
        Util.log("enableDaemon(): begin...");
        // TODO: -> UpdaterTask handles UpdateJobs
        // TODO: -> UpdateJob is ( ClockUpdate | TextScrollUpdate | CountDownUpdate | WaitForDevice )
        this.updateTask = new UpdateTask();

        if (this.useExecutorForUpdate) {
            Util.log("enableDaemon(): Using Executor for UpdateTask");
            this.executorService = Executors.newScheduledThreadPool(1);
            // TODO: get appropriate update interval:
            // TODO: - clock: 30 seconds
            // TODO: - text-scrolling, [countdown]: one second?
            // TODO: - wait-for-device: one minute
            long initialDelay = 10;
            long updatePeriod = 10;
            // run UpdaterTask
            this.executorService.scheduleAtFixedRate(this.updateTask, initialDelay, updatePeriod, TimeUnit.SECONDS);
        }
        else {
            Util.log("enableDaemon(): Using Thread for UpdateTask");
            this.updaterThread = new Thread(this.updateTask);
            this.updaterThread.setDaemon(true); // make sure this thread won't keep the VM running, if the main process dies.
            this.updaterThread.start();
            Util.log("enableDaemon(): Thread started");
            Thread.yield();
        }
        Util.log("enableDaemon(): done.");
    }

    private void disableDaemon() {
        Util.log("disableDaemon(): begin... shutting down UpdateTask");
        this.updateTask.shutdown();

        if (this.updaterThread != null) {
            Util.log("disableDaemon(): waiting to join with UpdaterThread");
            while (this.updaterThread.isAlive()) {
                try {
                    this.updaterThread.join();
                }
                catch (InterruptedException e) {
                    // nothing to do.
                }
            }
            this.updaterThread = null;
        }
        if (this.executorService != null) {
            Util.log("disableDaemon(): shutting down ExecutorService");
            this.executorService.shutdownNow();
            this.executorService = null;
        }
        Util.log("disableDaemon(): done.");
    }

    private void launchClockUpdater() {
        Util.log("creating timer for clock updates.");

        this.executorService = Executors.newScheduledThreadPool(1);
        long now = System.currentTimeMillis();
        // clock on X52 has only one minute resolution...
        // TODO: should do feature check and setup timer appropriately
        long timeToNextFullMinute = HotasX52Daemon.MINUTE_IN_MILLIS - (now % HotasX52Daemon.MINUTE_IN_MILLIS);
        long updatePeriod = HotasX52Daemon.MINUTE_IN_MILLIS;
        this.executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (HotasX52Daemon.this.updaterThread != null && HotasX52Daemon.this.updaterThread.isAlive()) {
                    synchronized (HotasX52Daemon.this.state) {
                        HotasX52Daemon.this.state.notify();
                    }
                }
            }
        }, timeToNextFullMinute, updatePeriod, TimeUnit.MILLISECONDS);
    }

    private class UpdateTask
            implements Runnable
    {
        private SaitekX52Pro device;
        private boolean keepGoing = true;

        @Override
        public void run() {
            while (this.keepGoing) {
                try {
                    if (this.device == null) {
                        this.device = SaitekX52Pro.init();
                        if (this.device != null) {
                            Util.log("HOTAS Updater: device became available.");
                        }
                    }

                    if (this.device != null) {
//                        Util.log("HOTAS Updater: device is available.");
                        if (SaitekX52Pro.isAvailableProductX52()) {
                            // do stuff.
                            if (isSupportedDevice()) {
                                // update clock
                                if (HotasX52Daemon.this.state.isClockEnabled()) {
                                    updateClock(HotasX52Daemon.this.state.getClock());
                                }

                                // check if state is changed -> apply new state
                                setupDevice();
                            }
                        } else {
                            Util.log("HOTAS Updater: device became unavailable.");
                            cleanupDevice();
                        }
                    }
                    else {
                        Util.log("HOTAS Updater: No supported device available.");
                    }
//                    Util.log("HOTAS Updater: waiting for notification");
                    waitForNotification();
                }
                catch (LibUsbException e) {
                    Util.log("HOTAS Updater: LibUsbException! -> " + e.getMessage());
                    Util.log("HOTAS Updater: device became unusable, triggering reset.");
                    cleanupDevice();
                }
            }
        }

        public void shutdown() {
            this.keepGoing = false;
        }

        private void waitForNotification() {
            synchronized (HotasX52Daemon.this.state) {
                try {
                    HotasX52Daemon.this.state.wait(HotasX52Daemon.WAKE_UP_INTERVAL); // check at least for availability
                }
                catch (InterruptedException e) {
                    // something woke us up...
                    Util.log("HOTAS Updater: awake.");
                }
            }
        }

        private boolean isSupportedDevice() {
            return this.device != null
                    && this.device.getType() != null
                    && this.device.getType() != SaitekX52Pro.Type.YOKE;
        }

        private void cleanupDevice() {
            if (this.device != null) {
                this.device.close();
                this.device = null;

                // mark state as dirty: will trigger reinitialization when device becomes available again.
                HotasX52Daemon.this.state.reset();
            }
        }

        private void setupDevice() {
            try {
                if (isLedSupported()) {
                    for (InternalValues.Led led : HotasX52Daemon.this.state.getDirtyLeds()) {
                        setLedColor(led, HotasX52Daemon.this.state.getLedColor(led));
                    }
                }
                for (InternalValues.LightSource light : HotasX52Daemon.this.state.getDirtyLights()) {
                    setBrightness(light, HotasX52Daemon.this.state.getBrightness(light));
                }
                for (int i : HotasX52Daemon.this.state.getDirtyLines()) {
                    this.device.setText(i, HotasX52Daemon.this.state.getText(i));
                }
            }
            catch (Exception e) {
                Util.log("HOTAS Updater: setupDevice() Error -> " + e.getMessage() + " (" + e + ")");
            }
        }

        private boolean isLedSupported() {
            return isSupportedDevice() && this.device.getType() == SaitekX52Pro.Type.X52PRO;
        }

        private void setBrightness(InternalValues.LightSource lightSource, int brightnessValue) {
            Util.log("HOTAS Updater: Set " + lightSource.name() + " with brightness " + brightnessValue);
            this.device.setBrightness(lightSource == InternalValues.LightSource.MFD, brightnessValue);
        }

        private void setLedColor(InternalValues.Led led, InternalValues.LedColor color) {
            Util.log("HOTAS Updater: Set LED " + led.name() + " to color " + color.name());
            this.device.setLED(led.redLed, color.red);
            this.device.setLED(led.greenLed, color.green);
        }

        private void updateClock(Parameter.ClockVariant clock) {
            Calendar currentDateTime = null;
            boolean enable24H = false;
            switch (clock) {
                case LOCAL_24H:
                    enable24H = true;
                case LOCAL_12H:
                    currentDateTime = Util.createLocalCalendar();
                    break;
                case UTC_24H:
                case GMT_24H:
                case ZULU_24H:
                    enable24H = true;
                case UTC_12H:
                case GMT_12H:
                case ZULU_12H:
                    currentDateTime = Util.createUtcCalendar();
                    break;
            }

//            Util.log("Set date to " + DateFormat.getDateTimeInstance().format(currentDateTime.getTime()));
            this.device.setDateTime(currentDateTime, enable24H);
            int tzOffs = currentDateTime.getTimeZone().getOffset(System.currentTimeMillis()) / 1000 / 60;
            this.device.setOffs(1, true, true, tzOffs); // set secondary time display to GMT time
        }

    }

    private class HotasState
            implements Hotas
    {
        private final Map<InternalValues.Led, InternalValues.LedColor> leds;
        private final Map<InternalValues.LightSource, Integer> lights;
        private final String[] textLines;
        private final Set<InternalValues.Led> dirtyLeds;
        private final Set<InternalValues.LightSource> dirtyLights;
        private final Set<Integer> dirtyLines;

        private Parameter.ClockVariant clock;

        public HotasState() {
            this.leds = new HashMap<>();
            this.lights = new HashMap<>();
            this.textLines = new String[3];

            this.dirtyLeds = new HashSet<>();
            this.dirtyLights = new HashSet<>();
            this.dirtyLines = new HashSet<>();
        }

        @Override
        public synchronized void init() {
            for (Parameter.Led led : Parameter.Led.values()) {
                setLedColor(led, Parameter.LedColor.OFF);
            }
            for (Parameter.LightSource light : Parameter.LightSource.values()) {
                setBrightness(light, Parameter.Brightness.OFF);
            }
            for (int i = 1; i <= this.textLines.length; i++) {
                setText(i, "");
            }
            this.clock = null;
        }

        @Override
        public void update() {
            // nothing to do.
        }

        @Override
        public synchronized void setBrightness(final Parameter.LightSource light, final Parameter.Brightness brightness) {
            setBrightness(light, brightness.value);
        }

        @Override
        public synchronized void setBrightness(final Parameter.LightSource light, final int brightnessValue) {
            for (InternalValues.LightSource internalName : HotasX52Daemon.this.parameterMappings.getLightSourceMappings(light)) {
                this.lights.put(internalName, brightnessValue);
                this.dirtyLights.add(internalName);
            }
        }

        public synchronized int getBrightness(final InternalValues.LightSource lightSource) {
            this.dirtyLights.remove(lightSource);
            return this.lights.get(lightSource);
        }

        public synchronized InternalValues.LightSource[] getDirtyLights() {
            return this.dirtyLights.toArray(new InternalValues.LightSource[this.dirtyLights.size()]);
        }

        @Override
        public synchronized void setLedColor(final Parameter.Led led, final Parameter.LedColor color) {
            for (InternalValues.Led internalName : HotasX52Daemon.this.parameterMappings.getLedMappings(led)) {
                InternalValues.LedColor ledColor = HotasX52Daemon.this.parameterMappings.getLedColorMappings(color);
                this.leds.put(internalName, ledColor);
                this.dirtyLeds.add(internalName);
            }
        }

        public synchronized InternalValues.LedColor getLedColor(final InternalValues.Led led) {
            this.dirtyLeds.remove(led);
            return this.leds.get(led);
        }

        public synchronized InternalValues.Led[] getDirtyLeds() {
            return this.dirtyLeds.toArray(new InternalValues.Led[this.dirtyLeds.size()]);
        }

        @Override
        public synchronized void setText(final int lineNum, final String text) {
            if (lineNum > 0 && lineNum <= this.textLines.length) {
                this.textLines[lineNum - 1] = text;
                this.dirtyLines.add(lineNum);
            }
            // else: silently ignore invalid access
        }

        public synchronized String getText(final int lineNum) {
            if (lineNum > 0 && lineNum <= this.textLines.length) {
                this.dirtyLines.remove(lineNum);
                return this.textLines[lineNum - 1];
            }
            else {
                return ""; // silently ignore invalid access
            }
        }

        public synchronized Integer[] getDirtyLines() {
            return this.dirtyLines.toArray(new Integer[this.dirtyLines.size()]);
        }

        @Override
        public synchronized void enableClock(Parameter.ClockVariant clock) {
            this.clock = clock;
        }

        public synchronized boolean isClockEnabled() {
            return this.clock != null;
        }

        public synchronized Parameter.ClockVariant getClock() {
            return this.clock;
        }

        public void reset() {
            Collections.addAll(this.dirtyLeds, InternalValues.Led.values());
            Collections.addAll(this.dirtyLights, InternalValues.LightSource.values());
            for (int i = 1; i <= this.textLines.length; i++) {
                this.dirtyLines.add(i);
            }
        }

        @Override
        public synchronized void shutdown() {
            this.leds.clear();
            this.dirtyLeds.clear();

            this.lights.clear();
            this.dirtyLights.clear();

            for (int i = 0; i < this.textLines.length; i++) {
                this.textLines[i] = null;
            }
            this.dirtyLines.clear();

            this.clock = null;
        }
    }
}
