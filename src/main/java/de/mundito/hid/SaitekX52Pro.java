/*
 * Copyright (c) 2015 Antony T Curtis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.mundito.hid;

import org.usb4java.*;

import java.nio.ByteBuffer;
import java.util.Calendar;


/**
 * @author antony
 * @since 2015-04-05
 */
public class SaitekX52Pro
        implements AutoCloseable
{

    private static Context context;

    public enum Type
    {
        X52,
        X52PRO,
        YOKE
    }

    public enum LED
    {
        FIRE(1),
        A_RED(2),
        A_GREEN(3),
        B_RED(4),
        B_GREEN(5),
        D_RED(6),
        D_GREEN(7),
        E_RED(8),
        E_GREEN(9),
        T1_RED(10),
        T1_GREEN(11),
        T2_RED(12),
        T2_GREEN(13),
        T3_RED(14),
        T3_GREEN(15),
        POV_RED(16),
        POV_GREEN(17),
        I_RED(18),
        I_GREEN(19),
        THROTTLE(20);

        final short id;

        LED(int id)
        {
            this.id = (short)(id << 8);
        }
    }

    private DeviceHandle hdl;
    private Type type;
    private boolean feat_mfd;
    private boolean feat_led;
    private boolean feat_sec;

    private static final short VENDOR_SAITEK = (short)0x06a3;
    private static final short PRODUCT_X52_0 = (short)0x0255;
    private static final short PRODUCT_X52_1 = (short)0x075c;
    private static final short PRODUCT_X52PRO = (short)0x0762;
    private static final short PRODUCT_YOKE = (short)0x0bac;

    private static final byte REQUEST = (byte)0x91;
    private static final byte CLEAR1 = (byte)0xd9;
    private static final byte CLEAR2 = (byte)0xda;
    private static final byte CLEAR3 = (byte)0xdc;
    private static final byte WRITE1 = (byte)0xd1;
    private static final byte WRITE2 = (byte)0xd2;
    private static final byte WRITE3 = (byte)0xd4;
    private static final byte SETLED = (byte)0xb8;
    private static final byte MFDBRI = (byte)0xb1;
    private static final byte LEDBRI = (byte)0xb2;

    private static final byte TIME = (byte)0xc0;
    private static final byte OFFS2 = (byte)0xc1;
    private static final byte OFFS3 = (byte)0xc2;
    private static final byte DATE = (byte)0xc4;
    private static final byte YEAR = (byte)0xc8;
    private static final byte SECOND = (byte)0xca;

    private static final byte[] write_idx = {0, WRITE1, WRITE2, WRITE3};
    private static final byte[] clear_idx = {0, CLEAR1, CLEAR2, CLEAR3};
    private static final byte[] offs_idx = {0, OFFS2, OFFS3};


    private SaitekX52Pro()
    {
        hdl = new DeviceHandle();
    }

    /**
     * Init function.
     *
     * @return instance to X52 device
     */
    public static synchronized SaitekX52Pro init()
    {
        initContext();

        SaitekX52Pro x52 = new SaitekX52Pro();

        DeviceList list = new DeviceList();
        int r = LibUsb.getDeviceList(context, list);
        if (r < 0)
            throw new LibUsbException("Unable to get device list", r);
        try
        {
            for (Device device : list)
            {
                DeviceDescriptor desc = new DeviceDescriptor();
                r = LibUsb.getDeviceDescriptor(device, desc);
                if (r < 0)
                    throw new LibUsbException(
                            "Unable to read device descriptor", r);
                if (desc.idVendor() != VENDOR_SAITEK)
                    continue;
                switch (desc.idProduct())
                {
                    case PRODUCT_X52_0:
                    case PRODUCT_X52_1:
                        x52.feat_mfd = true;
                        x52.type = Type.X52;
                        break;
                    case PRODUCT_X52PRO:
                        x52.feat_mfd = true;
                        x52.feat_led = true;
                        x52.type = Type.X52PRO;
                        break;
                    case PRODUCT_YOKE:
                        x52.feat_sec = true;
                        x52.type = Type.YOKE;
                        break;
                    default:
                        continue;
                }
                r = LibUsb.open(device, x52.hdl);
                if (r < 0)
                    throw new LibUsbException("Unable to open device", r);
                return x52;
            }
        }
        finally
        {
            LibUsb.freeDeviceList(list, true);
        }
        return null;
    }

    public static synchronized boolean isAvailableProductX52() {
        initContext();

        boolean foundSupportedDevice = false;
        DeviceList list = new DeviceList();
        int r = LibUsb.getDeviceList(context, list);
        if (r < 0) {
            throw new LibUsbException("Unable to get device list", r);
        }
        try {
            for (Device device : list) {
                DeviceDescriptor desc = new DeviceDescriptor();
                r = LibUsb.getDeviceDescriptor(device, desc);
                if (r < 0) {
                    throw new LibUsbException("Unable to read device descriptor", r);
                }
                if (desc.idVendor() != VENDOR_SAITEK) {
                    continue;
                }
                switch (desc.idProduct()) {
                    case PRODUCT_X52_0:
                    case PRODUCT_X52_1:
                    case PRODUCT_X52PRO:
                        foundSupportedDevice = true;
                        break;
                }
                if (foundSupportedDevice) {
                    break;
                }
            }
        }
        finally {
            LibUsb.freeDeviceList(list, true);
        }

        // TODO: check - may be use hotplugRegisterCallback() ?

        return foundSupportedDevice;
    }

    private static void initContext() {
        if (context == null) {
            context = new Context();
            Thread shutdownHook = new Thread(
                    new Runnable() {
                        public void run() {
                            LibUsb.exit(context);
                        }
                    }, "libusb shutdown");

            int result = LibUsb.init(context);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to initalize libusb.", result);
            }
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
    }

    /**
     * Get device type.
     *
     * @return type (X52, X52PRO, YOKE)
     */
    public Type getType()
    {
        return type;
    }

    @Override
    protected void finalize()
            throws Throwable
    {
        if (hdl != null)
        {
            LibUsb.close(hdl);
            hdl = null;
        }
        super.finalize();
    }

    public void close()
    {
        LibUsb.close(hdl);
        hdl = null;
    }

    public void setText(int line, CharSequence text)
    {
        if (!feat_mfd)
            throw new UnsupportedOperationException("setText not supported");
        if (line < 1 || line > 3)
            throw new IndexOutOfBoundsException("line number out of bounds");
        int r = controlMsg(clear_idx[line], (short)0x0000);
        if (r < 0)
            throw new LibUsbException("setText failed to clear", r);
        if (r < 0)
            throw new LibUsbException(
                    "failed at clear command: " + LibUsb.strError(r), r);
        for (int index = 0, length = text.length();
             length > 0;
             index += 2, length -= 2)
        {
            short chars;
            if (length == 1)
            {
                chars = (short)((' ' << 8) | text.charAt(index));
            }
            else
            {
                chars = (short)((text.charAt(index + 1) << 8) | text.charAt(
                        index));
            }
            r = controlMsg(write_idx[line], chars);
            if (r < 0)
                throw new LibUsbException("setText failed", r);
        }
    }

    public void setBrightness(boolean mfd, int brightness)
    {
        if (!feat_mfd)
            throw new UnsupportedOperationException(
                    "setBrightness not supported");
        int r = controlMsg(mfd ? MFDBRI : LEDBRI, (short)brightness);
        if (r < 0)
            throw new LibUsbException("setBrightness failed", r);
    }

    public void setLED(LED led, boolean on)
    {
        if (!feat_led)
            throw new UnsupportedOperationException("setLED not supported");
        int r = controlMsg(SETLED, (short)((on ? 1 : 0) | led.id));
        if (r < 0)
            throw new LibUsbException("setLED failed", r);
    }

    public void setTime(boolean h24, int hour, int minute)
    {
        int r = controlMsg(TIME,
                (short)(minute | (hour << 8) | (h24 ? 0x8000 : 0)));
        if (r < 0)
            throw new LibUsbException("setTime failed", r);
    }

    public void setOffs(int idx, boolean h24, boolean inv, int offset)
    {
        if (idx < 1 || idx > 2)
            throw new IndexOutOfBoundsException();
        int r = controlMsg(offs_idx[idx],
                (short)(offset | (inv ? 1024 : 0) | (h24 ? 0x8000 : 0)));
        if (r < 0)
            throw new LibUsbException("setOffs failed", r);
    }

    public void setSecond(int second)
    {
        if (!feat_sec)
            throw new UnsupportedOperationException("setSecond not supported");
        int r = controlMsg(SECOND, (short)(second << 8));
        if (r < 0)
            throw new LibUsbException("setSecond failed", r);
    }

    public void setDate(int year, int month, int day)
    {
        if (!feat_mfd)
            throw new UnsupportedOperationException("setDate not supported");
        int r = controlMsg(DATE, (short)(day | (month << 8)));
        if (r < 0)
            throw new LibUsbException("setDate failed", r);
        r = controlMsg(YEAR, (short)year);
        if (r < 0)
            throw new LibUsbException("setDate failed", r);
    }

    public void setDateTime(Calendar date, boolean enable24H)
    {
        if (feat_mfd)
        {
            setDate(date.get(Calendar.YEAR) % 100, date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
        }
        setTime(enable24H, enable24H ? date.get(Calendar.HOUR_OF_DAY) : date.get(Calendar.HOUR), date.get(Calendar.MINUTE));
        if (feat_sec)
        {
            setSecond(date.get(Calendar.SECOND));
        }
    }

    private final ByteBuffer NULL = ByteBuffer.allocateDirect(0);

    int controlMsg(byte index, short value)
    {
        return LibUsb.controlTransfer(hdl,
                (byte)(LibUsb.REQUEST_TYPE_VENDOR | LibUsb.RECIPIENT_DEVICE | LibUsb.ENDPOINT_OUT),
                REQUEST, value, index, NULL, 0);
    }
}
