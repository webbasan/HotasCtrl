package de.mundito.hid;

import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 08.05.15 Time: 22:17
 */
public class HotasX52Impl
        implements Hotas
{

    private final ParameterMappings parameterMappings;

    private SaitekX52Pro hotas;

    public HotasX52Impl() {
        this.parameterMappings = new ParameterMappings();
        this.hotas = null;
    }


    @Override
    public void init() {
        this.hotas = SaitekX52Pro.init();
    }

    @Override
    public void setBrightness(final Parameter.LightSource lightSource, final Parameter.Brightness brightness) {
        setBrightness(lightSource, brightness.value);
    }

    @Override
    public void setBrightness(final Parameter.LightSource lightSource, final int brightnessValue) {
        if (isSupportedDevice()) {
            for (InternalValues.LightSource internalValue : this.parameterMappings.getLightSourceMappings(lightSource)) {
                setBrightness(internalValue, brightnessValue);
            }
        }
    }

    @Override
    public void setLedColor(final Parameter.Led led, final Parameter.LedColor color) {
        if (isSupportedDevice() && isLedSupported()) {
            for (InternalValues.Led internalValue : this.parameterMappings.getLedMappings(led)) {
                InternalValues.LedColor ledColor = this.parameterMappings.getLedColorMappings(color);
                setLedColor(internalValue, ledColor);
            }
        }
    }

    @Override
    public void setText(final int lineNum, final String text) {
        if (isSupportedDevice()) {
            System.out.println("Set line " + lineNum + ": " + text);
            this.hotas.setText(lineNum, text);
        }
    }

    @Override
    public void shutdown() {
        this.hotas.close();
    }

    private boolean isSupportedDevice() {
        return this.hotas != null && this.hotas.getType() != null && this.hotas.getType() != SaitekX52Pro.Type.YOKE;
    }

    private boolean isLedSupported() {
        return isSupportedDevice() && this.hotas.getType() == SaitekX52Pro.Type.X52PRO;
    }

    private void setBrightness(InternalValues.LightSource lightSource, int brightnessValue) {
        System.out.println("Set " + lightSource.name() + " with brightness " + brightnessValue);
        this.hotas.setBrightness(lightSource == InternalValues.LightSource.MFD, brightnessValue);
    }

    private void setLedColor(InternalValues.Led led, InternalValues.LedColor color)
    {
        System.out.println("Set LED " + led.name() + " to color " + color.name());
        this.hotas.setLED(led.redLed, color.red);
        this.hotas.setLED(led.greenLed, color.green);
    }
}
