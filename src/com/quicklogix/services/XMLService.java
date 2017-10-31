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
        		props.getProperty("basexpassword"),
        		props.getProperty("basexdatabase")
        		);
    }

    public String savexmltoserver(String inputXML) throws IOException{
        return connection.addFile(inputXML);
    }
    
    public String getfilemetadata(String fileName) throws IOException {
    	Map<String, String> elements = connection.getFileMetaData(fileName);
    	JSONObject jsonObj = new JSONObject(elements);
    	return jsonObj.toString();
    }
    
    public String[] getfilexpath(String fileName) throws IOException {
    	return connection.getFileXPath(fileName);
    }
    
    public void updatexml(String dbName, String pathName, String inputXML) throws IOException {
    	connection.updateFile(dbName, pathName, inputXML);
    }
}
