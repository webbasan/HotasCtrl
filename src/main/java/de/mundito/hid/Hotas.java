package de.mundito.hid;

import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 05.05.15 Time: 20:16
 */
public interface Hotas {

    void init();

    void setBrightness(Parameter.LightSource lightSource, Parameter.Brightness brightness);

    void setBrightness(Parameter.LightSource lightSource, int brightnessValue);

    void setLedColor(Parameter.Led led, Parameter.LedColor color);

    void setText(int lineNum, String text);

    void enableClock(Parameter.ClockVariant clock);

    void shutdown();

}
