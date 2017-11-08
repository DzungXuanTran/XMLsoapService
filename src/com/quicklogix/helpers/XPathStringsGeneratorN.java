package com.quicklogix.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XPathStringsGeneratorN extends DefaultHandler {
	private String xPath = "/";
	private XMLReader xmlReader;
	private XPathStringsGeneratorN parent;
	private StringBuilder characters = new StringBuilder();
	private Map<String, Integer> elementNameCount = new HashMap<String, Integer>();
	private List<String> elements;

	public XPathStringsGeneratorN(XMLReader xmlReader) {
		this.xmlReader = xmlReader;
		elements = new ArrayList<>();
	}

	private XPathStringsGeneratorN(String xPath, XMLReader xmlReader, XPathStringsGeneratorN parent,
			List<String> elements) {
		this.xmlReader = xmlReader;
		this.xPath = xPath;
		this.parent = parent;
		this.elements = elements;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		Integer count = elementNameCount.get(qName);
		if (null == count) {
			count = 1;
		} else {
			count++;
		}
		elementNameCount.put(qName, count);
		String childXPath = xPath + "/" + qName;
		if (count > 1) {
			childXPath += "[n]";
		}
		int attrsLength = atts.getLength();
		for (int i = 0; i < attrsLength; i++) {
			String name = atts.getLocalName(i);
			if (name == null) {
				name = atts.getQName(i);
			}
			elements.add(childXPath + "/@" + name);
		}
		XPathStringsGeneratorN child = new XPathStringsGeneratorN(childXPath, xmlReader, this, elements);
		xmlReader.setContentHandler(child);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		elements.add(xPath);
		xmlReader.setContentHandler(parent);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		characters.append(ch, start, length);
	}

	public String[] getElements() {
		return elements.toArray(new String[elements.size()]);
	}
}