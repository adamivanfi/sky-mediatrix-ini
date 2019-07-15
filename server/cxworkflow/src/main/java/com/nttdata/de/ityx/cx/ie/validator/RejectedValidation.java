/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nttdata.de.ityx.cx.ie.validator;

import de.ityx.contex.interfaces.document.StreamedDocument;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.contex.interfaces.extag.Validator;

import java.util.regex.Pattern;
/**
 *
 * @author MEINUG
 */
public class RejectedValidation implements Validator {

	private final Pattern rejectedpattern=Pattern.compile("^XXX:");

	@Override
	public boolean isValid(StreamedDocument streamedDocument, TagMatch match) throws Exception {
		return !rejectedpattern.matcher( match.getTagValue() ).find();
	}

}
