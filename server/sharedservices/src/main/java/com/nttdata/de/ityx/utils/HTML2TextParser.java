package com.nttdata.de.ityx.utils;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class HTML2TextParser {

	private StringBuffer textBuffer;

	// HTMLTextParser Constructor
	public HTML2TextParser() {
	}

	// Gets the text content from Nodes recursively
	private void processNode(Node node) {
		if (node == null)
			return;

		// Process a text node
		if (node.getNodeType() == Node.TEXT_NODE) {
			textBuffer.append(node.getNodeValue());
		} else if (node.hasChildNodes()) {
			// Process the Node's children

			NodeList childList = node.getChildNodes();
			int childLen = childList.getLength();

			for (int count = 0; count < childLen; count++)
				processNode(childList.item(count));
		}
	}

	// Extracts text from HTML Document. Returns the input text if a parse error occurs.
	public String stripHTML(String data) {
		InputSource inSource = null;
		try {
			inSource = new InputSource(new StringReader(data));
		} catch (Exception e) {
			return null;
		}

		DOMFragmentParser parser = new DOMFragmentParser();
		CoreDocumentImpl codeDoc = new CoreDocumentImpl();
		DocumentFragment doc = codeDoc.createDocumentFragment();

		try {
			parser.parse(inSource, doc);
		} catch (Exception e) {
			return data;
		}

		textBuffer = new StringBuffer();
		processNode(doc);

		return textBuffer.toString();
	}
}
