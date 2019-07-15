package com.nttdata.de.ityx.cx.ie.converter;

import com.nttdata.de.ityx.cx.ie.validator.ValueRejectionException;
import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.document.StreamedDocument;
import de.ityx.contex.interfaces.extag.Conversion;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class AbstractConversion implements Conversion {
	abstract public String getLogPreafix();
	
        public final Pattern preafix = Pattern.compile("^");
        public final Pattern suffix = Pattern.compile("$");
        public final Pattern valueFormat = Pattern.compile(".*");
        
	public Pattern getValueFormat(){
            return valueFormat;
        }
        
        public Pattern getPreafixPattern(){
            return preafix;
        }
        public Pattern getSuffixPattern(){
            return suffix;
        }
	
	public abstract String executeConversion(String value) throws ValueRejectionException;

	@Override
	public HashSet<TagMatch> convert(StreamedDocument var1, TagMatch match) {

		String value = match.getTagValue();
		try {
                        checkEmptyValue(value);
                        SkyLogger.getCXIELogger().debug(getLogPreafix()+" Check: >" + value+"<");
                        value = cleanUsingPattern(getPreafixPattern(),value);
                        value = cleanUsingPattern(getSuffixPattern(),value);
                        checkEmptyValue(value);
                        
		      	value=executeConversion(value);
                        checkEmptyValue(value);
                        
                        checkWhitelistPattern(getValueFormat(), value, "ValueFormat");
                        SkyLogger.getCXIELogger().debug(getLogPreafix()+" OK:>" + value+"<");
			
		} catch (ValueRejectionException e) {
			SkyLogger.getCXIELogger().debug( getLogPreafix() + " NOK:>" + value + "< Reason: " + e.getMessage());
		                value="XXX:Blacklist_" + e.getMessage();
		}

		match.setTagValue(value);
		HashSet<TagMatch> hs=new HashSet<>();
				hs.add(match);

		return hs ;
	}
        
        /*
        thows Exception in Case value contains the pattern
        */
	
	public void checkBlacklistPartPattern(Pattern pattern, String value, String patternName) throws ValueRejectionException {
		if (pattern.matcher(value).find()) {
			throw new ValueRejectionException(patternName, value);
		}
	}
        /*
        thows Exception in Case value does not match the pattern
        */
        public void checkWhitelistPattern(Pattern pattern, String value, String patternName) throws ValueRejectionException {
		if (!pattern.matcher(value).matches()) {
			throw new ValueRejectionException(patternName, value);
		}
	}

	public String cleanUsingPattern(Pattern pattern, String value) {
		return replaceUsingPattern(pattern, value, "");
	}

	public String replaceUsingPattern(Pattern pattern, String value, String replacement) {
		return pattern.matcher(value).replaceAll(replacement);
	}

	public void checkEmptyValue(String value) throws ValueRejectionException{
		if (value==null || value.isEmpty()){
		    throw new ValueRejectionException("emptyValue", value);
		}
	}
        
        public void checkLiteralToDigitsRatio(String value, double minratio) throws ValueRejectionException {
 //BlackList Ratio literalsToDigits
             if (getLiteralToDigitsRatio(value) > minratio) { //ratio not ok
			throw new ValueRejectionException("checkLiteralToDigitsRatio", value);
	     }
        }
        
        public void checkDigitsToLiteralRatio(String value, double minratio) throws ValueRejectionException {
 //BlackList Ratio literalsToDigits
             if (getLiteralToDigitsRatio(value) > minratio) { //ratio not ok
			throw new ValueRejectionException("checkDigitsToLiteralRatio", value);
	     }
        }
        
            
        public double getLiteralToDigitsRatio(String value) {

		int count_digits = 0;
		for (Matcher m = Pattern.compile("\\d").matcher(value); m.find();) {
			count_digits += m.group().length();
		}
		int count_literal = 0;
		for (Matcher m = Pattern.compile("[a-zA-Z]").matcher(value); m.find();) {
			count_literal += m.group().length();
		}
		return (count_digits > 0) ? count_literal / count_digits : 1;
	}
        
        public double getDigitsToLiteralRatio(String value) {

		int count_digits = 0;
		for (Matcher m = Pattern.compile("\\d").matcher(value); m.find();) {
			count_digits += m.group().length();
		}
		int count_literal = 0;
		for (Matcher m = Pattern.compile("[a-zA-Z]").matcher(value); m.find();) {
			count_literal += m.group().length();
		}
		return (count_literal > 0) ?  count_digits /count_literal: 1;
	}
	
}
