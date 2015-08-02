package de.mundito.hid;

import de.mundito.args.Parameter;
import de.mundito.util.Util;

import java.text.DateFormat;
import java.util.Calendar;


/**
 * User: webbasan Date: 08.05.15 Time: 22:17
 */
public class HotasX52Simple
        implements Hotas
{

    private final ParameterMappings parameterMappings;

    private SaitekX52Pro device;

    public HotasX52Simple() {
        this.parameterMappings = new ParameterMappings();
        this.device = null;
    }


    @Override
    public void init() {
        this.device = SaitekX52Pro.init();
    }

    @Override
    public void update() {
        // nothing to do.
    }

    @Override
    public void setBrightness(final Parameter.LightSource lightSource, final Parameter.Brightness brightness) {
        if (isSupportedDevice()) {
            setBrightness(lightSource, brightness.value);
        }
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
            this.device.setText(lineNum, text);
        }
    }

    @Override
    public void enableClock(Parameter.ClockVariant clock) {
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
        System.out.println("Set date to " + DateFormat.getDateInstance().format(currentDateTime.getTime()));
        this.device.setDateTime(currentDateTime, enable24H);
    }

    @Override
    public void shutdown() {
        if (this.device != null) {
            this.device.close();
            this.device = null;
        }
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
