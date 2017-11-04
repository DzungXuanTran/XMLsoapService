package com.quicklogix.services;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;

import com.quicklogix.helpers.BaseXConnector;
import com.quicklogix.propertiesfileloader.PropertyLoader;
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

	public String addXML(String dbName, String inputXML) throws IOException {
		connection.openDB(dbName);
		return connection.addFile(inputXML);
	}

	public String getXML(String dbName, String fileName) throws IOException {
		connection.openDB(dbName);
		Map<String, String> elements = connection.getFile(dbName, fileName);
		JSONObject jsonObj = new JSONObject(elements);
		return jsonObj.toString();
	}

	public String[] getXpath(String dbName, String fileName) throws IOException {
		connection.openDB(dbName);
		return connection.getFileXPath(dbName, fileName);
	}

	public boolean updateXML(String dbName, String pathName, String inputXML) throws IOException {
		connection.openDB(dbName);
		return connection.updateFile(dbName, pathName, inputXML);
	}

	public String addMultipleXML(String dbName, String[] inputXML)
			throws IOException {
		try {
			connection.openDB(dbName);
			return connection.addMultipleFile(inputXML);
		} catch (VTDException ex) {
			ex.printStackTrace();
		}
		return "";
	}
}