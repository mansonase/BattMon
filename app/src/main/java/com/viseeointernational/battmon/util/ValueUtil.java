package com.viseeointernational.battmon.util;

public class ValueUtil {

    public static int getValue(byte h, byte l) {
        return (h & 0xff) * 0x100 + (l & 0xff);
    }

    public static float getRealVoltage(byte h, byte l, byte calH, byte calL) {
        int voltage = getValue(h, l);
        int calibration = getValue(calH, calL);
        return voltage * calibration / 1000000f;
    }

    public static int getVoltage(float realVoltage, byte calH, byte calL){
        int calibration = getValue(calH, calL);
        return (int)(realVoltage * 1000000 / calibration);
    }

    public static byte getH(int value) {
        return (byte) (value / 0x100);
    }

    public static byte getL(int value) {
        return (byte) (value % 0x100);
    }
}
