package com.nttdata.de.ityx.cx.ie.converter;

import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;

public class NameConversion extends AbstractConversion {

    private final String logPreafix = getClass().getSimpleName();

    private static NameConversion instance = null;

    public static NameConversion getInstance() {
        if (instance == null) {
            instance = new NameConversion();
        }
        return instance;
    }

    @Override
    public String executeConversion(String value) throws ValueRejectionException {
        checkEmptyValue(value);
        checkDigitsToLiteralRatio(value, 0.1);
        return value;
    }

    @Override
    public String getLogPreafix() {
        return logPreafix;
    }
}
