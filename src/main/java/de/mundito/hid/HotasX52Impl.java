package de.mundito.hid;

import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 08.05.15 Time: 22:17
 */
public class HotasX52Impl
        implements Hotas
{

    private final ParameterMappings parameterMappings;

    private SaitekX52Pro device;

    public HotasX52Impl() {
        this.parameterMappings = new ParameterMappings();
        this.device = null;
    }


    @Override
    public boolean isAvailable() {
        return SaitekX52Pro.isAvailableProductX52();
    }

    @Override
    public void init() {
        this.device = SaitekX52Pro.init();
    }

    @Override
    public void enableDaemon() {
        // TODO: implement method.
    }

    @Override
    public void disableDaemon() {
        // TODO: implement method.
    }

    @Override
    public void setBrightness(final Parameter.LightSource lightSource, final Parameter.Brightness brightness) {
        setBrightness(lightSource, brightness.value);
    }

    @Override
    public void setBrightness(final Parameter.LightSource lightSource, final int brightnessValue) {
        if (isAvailable()) {
            if (isSupportedDevice()) {
                for (InternalValues.LightSource internalValue : this.parameterMappings.getLightSourceMappings(lightSource)) {
                    setBrightness(internalValue, brightnessValue);
                }
            }
        }
        else if (isDeferredUpdateEnabled()) {
            // TODO: add UpdateJob
        }
    }

    @Override
    public void setLedColor(final Parameter.Led led, final Parameter.LedColor color) {
        if (isAvailable()) {
            if (isSupportedDevice() && isLedSupported()) {
                for (InternalValues.Led internalValue : this.parameterMappings.getLedMappings(led)) {
                    InternalValues.LedColor ledColor = this.parameterMappings.getLedColorMappings(color);
                    setLedColor(internalValue, ledColor);
                }
            }
        }
        else if (isDeferredUpdateEnabled()) {
            // TODO: add UpdateJob
        }
    }

    @Override
    public void setText(final int lineNum, final String text) {
        if (isAvailable()) {
            if (isSupportedDevice()) {
                System.out.println("Set line " + lineNum + ": " + text);
                this.device.setText(lineNum, text);
            }
        }
        else if (isDeferredUpdateEnabled()) {
            // TODO: add UpdateJob
        }
    }

    @Override
    public void shutdown() {
        this.device.close();
    }

    private boolean isDeferredUpdateEnabled() {
        // TODO: check if UpdaterTask is available
        return false;
    }

    private boolean isSupportedDevice() {
        return this.device != null && this.device.getType() != null && this.device.getType() != SaitekX52Pro.Type.YOKE;
    }

    private boolean isLedSupported() {
        return isSupportedDevice() && this.device.getType() == SaitekX52Pro.Type.X52PRO;
    }

    private void setBrightness(InternalValues.LightSource lightSource, int brightnessValue) {
        System.out.println("Set " + lightSource.name() + " with brightness " + brightnessValue);
        this.device.setBrightness(lightSource == InternalValues.LightSource.MFD, brightnessValue);
    }

    private void setLedColor(InternalValues.Led led, InternalValues.LedColor color)
    {
        System.out.println("Set LED " + led.name() + " to color " + color.name());
        this.device.setLED(led.redLed, color.red);
        this.device.setLED(led.greenLed, color.green);
    }
}
