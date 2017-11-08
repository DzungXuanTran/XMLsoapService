package com.quicklogix.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.ximpleware.AutoPilot;
import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

public class BaseXConnector {
    private final String FILENAME = "quicklogix";
    private static BaseXClient session;
    private VTDGen vg;

    private BaseXConnector (){}

    public static BaseXConnector connect(String host,int port, String username, String password) {
        BaseXConnector instance = new BaseXConnector();
        instance.vg = new VTDGen();
        try {
			session = new BaseXClient(host, port, username, password);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return instance;
    }

    public boolean addFile(String fileName, String xml) throws IOException {
        if (session == null) {
            throw new IOException("No connection");
        } else {
        	try {
                vg.setDoc(xml.getBytes(StandardCharsets.UTF_8));
                vg.parse(false);
                byte[] bytes = vg.getNav().getXML().getBytes();
                session.add(fileName, new ByteArrayInputStream(bytes));
                return true;
        	} catch (ParseException ex) {
        		throw new IOException("Invalid XML");
        	}
        }
    }
    
    public Map<String, String> getFile(String dbName, String fileName) throws IOException{
    	if (session == null) {
    		throw new IOException("No connection");
    	} else {
    			String xml = session.execute("XQUERY collection('" + dbName + "/" + fileName + "')");
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
    
    public String[] getFileXpathWithN(String dbName, String fileName) throws IOException {
    	if (session == null) {
    		throw new IOException("No connection");
    	} else {
    		String xml = session.execute("XQUERY collection('" + dbName + "/" + fileName + "')");
			if (xml.isEmpty()) {
    			throw new IOException("File not found");
			} else {
				try {
			 SAXParserFactory spf = SAXParserFactory.newInstance();
			 SAXParser sp = spf.newSAXParser();
			 XMLReader xr = sp.getXMLReader();
		     xr.setContentHandler(new XPathStringsGeneratorN(xr));
		     xr.parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8.name()))));
		     ContentHandler contentHandler = xr.getContentHandler();
		     if (contentHandler instanceof XPathStringsGeneratorN) {
		    	 return ((XPathStringsGeneratorN) contentHandler).getElements();
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
    
    public String[] getFileXpath(String dbName, String fileName) throws IOException {
    	if (session == null) {
    		throw new IOException("No connection");
    	} else {
    		String xml = session.execute("XQUERY collection('" + dbName + "/" + fileName + "')");
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
    

    public void openDB(String dbName) throws IOException {
    	try {
    		session.execute("OPEN " + dbName);
        } catch(IOException ex) {
            if (ex.getMessage().contentEquals("Database '" + dbName + "' was not found.")) {
            	session.execute("CREATE DB " + dbName);
            	openDB(dbName);
            } else {
            	throw ex;
            }
        }
    }

    public boolean updateFile(String dbName, String pathName, String xml) throws IOException {
    	if (session == null) {
            throw new IOException("No connection");
        } else {
            session.replace(pathName, new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8.name())));
            return true;
        }
    }

    public boolean addMultipleFile(String fileName, String[] xmls) throws IOException, VTDException {
		StringBuffer buffer = new StringBuffer();
        Map<String, String> map = getContent(xmls[0]);
        String footer = map.get("footer");
        buffer.append(map.get("header"));
        buffer.append(map.get("content"));
        for (int i = 1; i < xmls.length; i++) {
            map = getContent(xmls[i]);
            if (footer.equalsIgnoreCase(map.get("footer"))) {
                buffer.append(map.get("content"));
            }
        }
        buffer.append(footer);
        try {
            vg.setDoc(buffer.toString().getBytes());
            vg.parse(false);
            byte[] bytes = vg.getNav().getXML().getBytes();
            session.add(fileName, new ByteArrayInputStream(bytes));
            return true;
        } catch (ParseException e) {
//            e.printStackTrace();
        	throw new IOException("Invalid XML");
        }
	}
	
	public Map<String, String> getContent(String xml) throws VTDException {
        Map<String, String> map = new HashMap<>();
        //parse XML
        vg.setDoc(xml.getBytes());
        vg.parse(false);
        VTDNav vn = vg.getNav();
        vn.toElement(VTDNav.ROOT);
        long l = vn.getContentFragment();
        byte[] bytes = vn.getXML().getBytes();

        //get main content
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        baos.write(bytes, (int) l, (int) (l>>32));
        map.put("content", baos.toString());

        //get header
        baos.reset();
        baos.write(bytes, 0, (int) l);
        map.put("header", baos.toString());

        //get footer
        int lastIndex = xml.lastIndexOf("</");
        map.put("footer", xml.substring(lastIndex));

        vg.clear();
        return map;
    }
}
