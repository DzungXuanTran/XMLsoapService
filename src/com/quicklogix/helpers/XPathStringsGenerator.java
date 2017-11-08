package com.quicklogix.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XPathStringsGenerator extends DefaultHandler{
	private String xPath = "/";
    private XMLReader xmlReader;
    private XPathStringsGenerator parent;
    private StringBuilder characters = new StringBuilder();
    private List<String> elements;

    public XPathStringsGenerator(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
        elements = new ArrayList<>();
    }

    private XPathStringsGenerator(String xPath, XMLReader xmlReader, XPathStringsGenerator parent, List<String> elements) {
        this.xmlReader = xmlReader;
        this.xPath = xPath;
        this.parent = parent;
        this.elements = elements;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        String childXPath = xPath + "/" + qName;
        int attrsLength = atts.getLength();
        for (int i = 0; i < attrsLength; i++) {
            String name = atts.getLocalName(i);
            if (name == null) {
                name = atts.getQName(i);
            }
            String newElem = childXPath + "/@" + name;
            elements.add(newElem);
        }
        XPathStringsGenerator child = new XPathStringsGenerator(childXPath, xmlReader, this, elements);
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
