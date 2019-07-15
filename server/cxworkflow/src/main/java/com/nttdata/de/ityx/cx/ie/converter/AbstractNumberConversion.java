package com.nttdata.de.ityx.cx.ie.converter;

import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;

import java.util.regex.Pattern;

/**
 * @author MEINUG
 */
public abstract class AbstractNumberConversion extends AbstractConversion {

	private final Pattern valueFormat = Pattern.compile("[0-9]*");

	//cleanPattern
	protected final Pattern clean_spaceChars = Pattern.compile("[:;,_\\-\\.~\\+\\*/\\\\\\s\\x0B\\u00A0]*");

	//replacementPattern
	private final Pattern chk_fullDate = Pattern.compile("((0[1-9])|([12][\\d])|(3[01]))((0[1-9])|(1[012]))(20)?1[3-5]");
	private final Pattern chk_fullReverseDate = Pattern.compile("(20)?1[3-5]((0[1-9])|(1[012]))((0[1-9])|([12][\\d])|(3[01]))");
	private final Pattern chk_smartDate = Pattern.compile("((0[1-9])|(1[012]))201[3-5]");
	private final Pattern chk_emailDate = Pattern.compile("201[3-5](([1-9])|(0[1-9])|(1[\\d])|(2[0-4]))[0-5][\\d]");
	private final Pattern chk_yearDate = Pattern.compile("201[3-5]");


	private final Pattern rpl_OCR_0 = Pattern.compile("(((?i)[ocö])|(Q))");
	private final Pattern rpl_OCR_1 = Pattern.compile("(?i)[il|]"); //"/"
	private final Pattern rpl_OCR_6 = Pattern.compile("b");
	private final Pattern rpl_OCR_7 = Pattern.compile("(?i)z");
	private final Pattern rpl_OCR_8 = Pattern.compile("[BR]");
	private final Pattern rpl_OCR_9 = Pattern.compile("[gq]");
	private final Pattern notNumber = Pattern.compile("[^0-9]");

	public double getLiteralToDigitsRatio() {
		return 0.35;
	}
	
	@Override
	public String executeConversion(String value) throws ValueRejectionException {
		value = cleanUsingPattern(clean_spaceChars, value);
		checkLiteralToDigitsRatio(value, getLiteralToDigitsRatio());

		value = replaceUsingPattern(rpl_OCR_0, value, "0");
		value = replaceUsingPattern(rpl_OCR_1, value, "1");
		value = replaceUsingPattern(rpl_OCR_6, value, "6");
		value = replaceUsingPattern(rpl_OCR_7, value, "7");
		value = replaceUsingPattern(rpl_OCR_8, value, "8");
		value = replaceUsingPattern(rpl_OCR_9, value, "9");
		value = cleanUsingPattern(notNumber, value);
		checkEmptyValue(value);
		
		if (value.length() >= 6 && value.length() <= 8) {
			checkBlacklistPartPattern(chk_fullDate, value, "FullDate");
			checkBlacklistPartPattern(chk_fullReverseDate, value, "fullReverseDate");
			checkBlacklistPartPattern(chk_emailDate, value, "emailDate");

		} else if (value.length() >= 4 && value.length() < 6) {
			checkBlacklistPartPattern(chk_smartDate, value, "smartDate");
			checkBlacklistPartPattern(chk_emailDate, value, "emailDate");
			checkBlacklistPartPattern(chk_yearDate, value, "yearDate");
		}
		return value;
	}


	@Override
	public Pattern getValueFormat() {
		return valueFormat;
	}

}
