package com.viseeointernational.battmon.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

    public static float formatDouble2(double d) {
        BigDecimal bigDecimal = new BigDecimal(d).setScale(2, RoundingMode.UP);
        return bigDecimal.floatValue();
    }
}
