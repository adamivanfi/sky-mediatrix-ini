package com.nttdata.de.ityx.cx.ie.converter;

import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;

import java.util.regex.Pattern;

public class EmailNormalizationConversion extends AbstractConversion {

    protected final Pattern emailCleaner = Pattern.compile("[<>\\\"\\s]");
    private final String logPreafix = getClass().getSimpleName();

    private static EmailNormalizationConversion instance = null;

    public static EmailNormalizationConversion getInstance() {
        if (instance == null) {
            instance = new EmailNormalizationConversion();
        }
        return instance;
    }

    @Override
    public String executeConversion(String value) throws ValueRejectionException {
        checkEmptyValue(value);
        value = cleanUsingPattern(emailCleaner, value);
        //return super.executeConversion(value);
        checkDigitsToLiteralRatio(value, 0.1);
        return value;
    }

    @Override
    public String getLogPreafix() {
        return logPreafix;
    }
}
