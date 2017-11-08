package com.quicklogix.services;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;

import com.quicklogix.helpers.BaseXConnector;
import com.quicklogix.propertiesfileloader.PropertyLoader;
import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDException;

public class XMLService {
	private BaseXConnector connection;

	public XMLService() {
		PropertyLoader loader = new PropertyLoader();
		Properties props = loader.getpropObject();
		connection = BaseXConnector.connect(props.getProperty("basexhost"),
				Integer.parseInt(props.getProperty("basexport")), props.getProperty("basexusername"),
				props.getProperty("basexpassword"));
	}

	public boolean addXML(String dbName, String fileName, String inputXML) throws IOException {
		connection.openDB(dbName);
		return connection.addFile(fileName, inputXML);
	}

	public String getXML(String dbName, String fileName) throws IOException {
		connection.openDB(dbName);
		Map<String, String> elements = connection.getFile(dbName, fileName);
		JSONObject jsonObj = new JSONObject(elements);
		return jsonObj.toString();
	}

	public String[] getXpathWithN(String dbName, String fileName) throws IOException {
		connection.openDB(dbName);
		return connection.getFileXpathWithN(dbName, fileName);
	}
	
	public String[] getXpathN(String dbName, String fileName) throws IOException {
		connection.openDB(dbName);
		return connection.getFileXpath(dbName, fileName);
	}

	public boolean updateXML(String dbName, String fileName, String inputXML) throws IOException {
		connection.openDB(dbName);
		return connection.updateFile(dbName, fileName, inputXML);
	}

	public boolean addMultipleXML(String dbName, String fileName, String[] inputXML)
			throws IOException {
		try {
			connection.openDB(dbName);
			return connection.addMultipleFile(fileName, inputXML);
		} catch (VTDException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public void test() {
		try {
			String[] xpaths = connection.getFileXpathWithN("quicklogix", "quicklogix1509791585581");
			for (String str : xpaths) {
				System.out.println(str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}