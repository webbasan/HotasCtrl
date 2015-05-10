package de.mundito.hid;

import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 05.05.15 Time: 20:19
 */
public class HotasMockImpl
        implements Hotas
{

    private final ParameterMappings parameterMappings;

    public HotasMockImpl() {
        this.parameterMappings = new ParameterMappings();
    }


    public void init() {
        System.out.println("HOTAS initialized.");
    }

    public void setBrightness(Parameter.LightSource lightSource, Parameter.Brightness brightness) {
        setBrightness(lightSource, brightness.value);
    }

    public void setBrightness(Parameter.LightSource lightSource, int brightnessValue) {
        for (InternalValues.LightSource internalValue : this.parameterMappings.getLightSourceMappings(lightSource)) {
            setBrightness(internalValue, brightnessValue);
        }
    }

    public void setLedColor(Parameter.Led led, Parameter.LedColor color) {
        for (InternalValues.Led internalValue : this.parameterMappings.getLedMappings(led)) {
            InternalValues.LedColor ledColor = this.parameterMappings.getLedColorMappings(color);
            setLedColor(internalValue, ledColor);
        }
    }

    public void setText(int lineNum, String text) {
        System.out.println("Set line " + lineNum + ": " + text);
    }

    public void shutdown() {
        System.out.println("HOTAS shutdown.");
    }

    private void setBrightness(InternalValues.LightSource lightSource, int brightnessValue) {
        System.out.println("Set " + lightSource.name() + " with brightness " + brightnessValue);
    }

    private void setLedColor(InternalValues.Led led, InternalValues.LedColor color) {
        System.out.println("Set LED " + led.name() + " to color " + color.name());
    }

}
