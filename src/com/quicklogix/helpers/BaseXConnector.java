package com.quicklogix.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class BaseXConnector {
    private final String FILENAME = "quicklogix";
    private static BaseXClient session;
    private String db;

    private BaseXConnector (){}

    public static BaseXConnector connect (String host,int port, String username, String password, String dbName) {
        return connect(host, port, username, password, dbName, true);
    }
    private static BaseXConnector connect(String host,int port, String username, String password, String dbName, boolean dbExist) {
        BaseXConnector instance = new BaseXConnector();
        try {
            session = new BaseXClient(host, port, username, password);
            // Open DB
            if (!dbExist) {
            	session.execute("CREATE DB " + dbName);
            }
            session.execute("OPEN " + dbName);
            instance.db = dbName;
        } catch(IOException ex) {
            // catch db not found
            if (ex.getMessage().contentEquals("Database '" + dbName + "' was not found.")) {
                instance = connect(host, port, username, password, dbName, false);
            } else {
            	ex.printStackTrace();
            }
        }
        return instance;
    }

    public String addFile(String xml) throws IOException {
        if (session == null) {
            throw new IOException("No connection");
        } else {
                String path = FILENAME + System.currentTimeMillis();
                session.add(path, new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8.name())));
                return path;
        }
    }
    
    public Map<String, String> getFileMetaData(String fileName) throws IOException{
    	if (session == null) {
    		throw new IOException("No connection");
    	} else {
    			String xml = session.execute("XQUERY collection('" + db + "/" + fileName + "')");
    			if (xml.isEmpty()) {
        			throw new IOException("File not found");
    			} else {
    				try {
				 SAXParserFactory spf = SAXParserFactory.newInstance();
				 SAXParser sp = spf.newSAXParser();
				 XMLReader xr = sp.getXMLReader();
    		     xr.setContentHandler(new XPathJSONGenerator(xr));
    		     xr.parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8.name()))));
    		     ContentHandler contentHandler = xr.getContentHandler();
    		     if (contentHandler instanceof XPathJSONGenerator) {
    		    	 return ((XPathJSONGenerator) contentHandler).getElements();
    		     }
    				} catch (SAXException ex) {
    					ex.printStackTrace();
    				} catch (ParserConfigurationException e) {
						e.printStackTrace();
					}
    			}
    	}
    	return null;
    }
    
    public String[] getFileXPath(String fileName) throws IOException {
    	if (session == null) {
    		throw new IOException("No connection");
    	} else {
    		String xml = session.execute("XQUERY collection('" + db + "/" + fileName + "')");
			if (xml.isEmpty()) {
    			throw new IOException("File not found");
			} else {
				try {
			 SAXParserFactory spf = SAXParserFactory.newInstance();
			 SAXParser sp = spf.newSAXParser();
			 XMLReader xr = sp.getXMLReader();
		     xr.setContentHandler(new XPathStringsGenerator(xr));
		     xr.parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8.name()))));
		     ContentHandler contentHandler = xr.getContentHandler();
		     if (contentHandler instanceof XPathStringsGenerator) {
		    	 return ((XPathStringsGenerator) contentHandler).getElements();
		     }
				} catch (SAXException ex) {
					ex.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
    	}
    	return null;
    }
}
