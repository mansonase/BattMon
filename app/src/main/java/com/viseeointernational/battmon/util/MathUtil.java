package com.viseeointernational.battmon.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

    public static float formatFloat2(float f) {
        BigDecimal bigDecimal = new BigDecimal(f).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }

    public static float formatFloat1(float f) {
        BigDecimal bigDecimal = new BigDecimal(f).setScale(1, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }
}
