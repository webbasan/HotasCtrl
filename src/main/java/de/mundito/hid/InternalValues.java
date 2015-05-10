package de.mundito.hid;

import de.mundito.args.Parameter;


/**
 * User: webbasan Date: 05.05.15 Time: 20:47
 */
interface InternalValues {

    Parameter.Sub[] getKeys();

    enum LightSource implements InternalValues {
        MFD(true, Parameter.LightSource.MFD, Parameter.LightSource.ALL),
        LED(false, Parameter.LightSource.LED, Parameter.LightSource.ALL);

        public final Parameter.Sub[] subParameters;
        public final boolean isMfd;

        LightSource(final boolean isMfd, final Parameter.Sub... subParameters) {
            this.isMfd = isMfd;
            this.subParameters = subParameters;
        }

        public Parameter.Sub[] getKeys() {
            return this.subParameters;
        }
    }

    enum Led implements InternalValues {
        FIRE(SaitekX52Pro.LED.FIRE, SaitekX52Pro.LED.FIRE, Parameter.Led.FIRE, Parameter.Led.ALL),
        THROTTLE(SaitekX52Pro.LED.THROTTLE, SaitekX52Pro.LED.THROTTLE, Parameter.Led.THROTTLE, Parameter.Led.ALL),
        A(SaitekX52Pro.LED.A_RED, SaitekX52Pro.LED.A_GREEN, Parameter.Led.A, Parameter.Led.ALL),
        B(SaitekX52Pro.LED.B_RED, SaitekX52Pro.LED.B_GREEN, Parameter.Led.B, Parameter.Led.ALL),
        D(SaitekX52Pro.LED.D_RED, SaitekX52Pro.LED.D_GREEN, Parameter.Led.D, Parameter.Led.ALL),
        E(SaitekX52Pro.LED.E_RED, SaitekX52Pro.LED.E_GREEN, Parameter.Led.E, Parameter.Led.ALL),
        I(SaitekX52Pro.LED.I_RED, SaitekX52Pro.LED.I_GREEN, Parameter.Led.I, Parameter.Led.ALL),
        T1(SaitekX52Pro.LED.T1_RED, SaitekX52Pro.LED.T1_GREEN, Parameter.Led.T1, Parameter.Led.ALL),
        T2(SaitekX52Pro.LED.T2_RED, SaitekX52Pro.LED.T2_GREEN, Parameter.Led.T2, Parameter.Led.ALL),
        T3(SaitekX52Pro.LED.T3_RED, SaitekX52Pro.LED.T3_GREEN, Parameter.Led.T3, Parameter.Led.ALL),
        POV(SaitekX52Pro.LED.POV_RED, SaitekX52Pro.LED.POV_GREEN, Parameter.Led.POV, Parameter.Led.ALL);

        public final Parameter.Sub[] subParameters;
        public final SaitekX52Pro.LED redLed;
        public final SaitekX52Pro.LED greenLed;

        Led(final SaitekX52Pro.LED redLed, final SaitekX52Pro.LED greenLed, final Parameter.Sub... subParameters) {
            this.redLed = redLed;
            this.greenLed = greenLed;
            this.subParameters = subParameters;
        }

        public Parameter.Sub[] getKeys() {
            return this.subParameters;
        }
    }

    enum LedColor implements InternalValues {
        OFF(false, false, Parameter.LedColor.OFF),
        ON(false, true, Parameter.LedColor.ON),
        RED(true, false, Parameter.LedColor.RED),
        AMBER(true, true, Parameter.LedColor.AMBER),
        GREEN(false, true, Parameter.LedColor.GREEN);

        public final Parameter.Sub[] subParameters;
        public final boolean red;
        public final boolean green;

        LedColor(final boolean red, final boolean green, final Parameter.Sub... subParameters) {
            this.red = red;
            this.green = green;
            this.subParameters = subParameters;
        }

        public Parameter.Sub[] getKeys() {
            return this.subParameters;
        }
    }
}
