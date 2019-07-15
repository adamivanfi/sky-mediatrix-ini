package com.nttdata.de.ityx.cx.ie.converter.sky;

import com.nttdata.de.ityx.cx.ie.converter.EmailNormalizationConversion;
import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;

import java.util.regex.Pattern;

public class EmailConversion extends EmailNormalizationConversion {

    protected final Pattern skyEmail = Pattern.compile("(?i)[\\w\\.\\-_]+@sky\\.((de)|(at))");
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
        value = super.executeConversion(value);
        checkBlacklistPartPattern(skyEmail, value, "SKY EMAIL");
        return value;
    }

    @Override
    public String getLogPreafix() {
        return logPreafix;
    }

}
