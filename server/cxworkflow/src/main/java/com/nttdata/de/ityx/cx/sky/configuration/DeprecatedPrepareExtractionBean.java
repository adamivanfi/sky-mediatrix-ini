package com.nttdata.de.ityx.cx.sky.configuration;

import com.nttdata.de.ityx.utils.HTML2TextParser;

import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeprecatedPrepareExtractionBean {

	private static final String BUNDLE_NAME = "com.nttdata.de.ityx.sky.CustomerIndexingClippings";

	private static Set<Pattern> patterns = new CopyOnWriteArraySet<>();
	{
		ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);
		for (String key : bundle.keySet())
			patterns.add(Pattern.compile(bundle.getString(key), Pattern.MULTILINE | Pattern.DOTALL));
	}

	public String prepareText(String text) {
		String body = new HTML2TextParser().stripHTML(text);
		for (Pattern p : patterns) {
				Matcher matcher = p.matcher(body);
				body = matcher.replaceAll("");
		}
		return body;
	}
}
