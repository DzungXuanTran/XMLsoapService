package com.quicklogix.helpers;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XPathJSONGenerator extends DefaultHandler{
	private String xPath = "/";
    private XMLReader xmlReader;
    private XPathJSONGenerator parent;
    private StringBuilder characters = new StringBuilder();
    private Map<String, Integer> elementNameCount = new HashMap<String, Integer>();
    private Map<String, String> elements;

    public XPathJSONGenerator(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
        elements = new HashMap<>();
    }

    private XPathJSONGenerator(String xPath, XMLReader xmlReader, XPathJSONGenerator parent, Map<String, String> elements) {
        this.xmlReader = xmlReader;
        this.xPath = xPath;
        this.parent = parent;
        this.elements = elements;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        Integer count = elementNameCount.get(qName);
        if(null == count) {
            count = 1;
        } else {
            count++;
        }
        elementNameCount.put(qName, count);
        String childXPath = xPath + "/" + qName + "[" + count + "]";
        int attrsLength = atts.getLength();
        for (int i = 0; i < attrsLength; i++) {
            String name = atts.getLocalName(i);
            if (name == null) {
                name = atts.getQName(i);
            }
            elements.put(childXPath + "/@" + name, atts.getValue(i));
        }
        XPathJSONGenerator child = new XPathJSONGenerator(childXPath, xmlReader, this, elements);
        xmlReader.setContentHandler(child);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String value = characters.toString().trim();
        if(value.length() > 0) {
            elements.put(xPath, characters.toString());
        }
        xmlReader.setContentHandler(parent);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        characters.append(ch, start, length);
    }

    public Map<String, String> getElements() {
        return elements;
    }
}
