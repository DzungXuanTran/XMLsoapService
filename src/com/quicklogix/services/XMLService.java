package com.quicklogix.services;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;

import com.quicklogix.helpers.BaseXConnector;
import com.quicklogix.propertiesfileloader.PropertyLoader;

public class XMLService {
	private BaseXConnector connection;
    public XMLService() {
    	PropertyLoader loader = new PropertyLoader();
    	Properties props = loader.getpropObject();
        connection = BaseXConnector.connect(
        		props.getProperty("basexhost"), 
        		Integer.parseInt(props.getProperty("basexport")),
        		props.getProperty("basexusername"),
        		props.getProperty("basexpassword")
        		);
    }

    public String savexmltoserver(String dbName, String inputXML) throws IOException{
    	connection.openDB(dbName);
        return connection.addFile(inputXML);
    }
    
    public String getfilemetadata(String dbName, String fileName) throws IOException {
    	connection.openDB(dbName);
    	Map<String, String> elements = connection.getFileMetaData(dbName, fileName);
    	JSONObject jsonObj = new JSONObject(elements);
    	return jsonObj.toString();
    }
    
    public String[] getfilexpath(String dbName, String fileName) throws IOException {
    	connection.openDB(dbName);
    	return connection.getFileXPath(dbName, fileName);
    }
}
