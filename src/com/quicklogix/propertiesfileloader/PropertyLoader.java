package com.quicklogix.propertiesfileloader;

import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader 
{
	public Properties getpropObject()
	{
		Properties prop  = new Properties();
		InputStream ins = null;
		try
		{
			String propFileName = "config.properties";
			ins = getClass().getClassLoader().getResourceAsStream(propFileName);
			prop.load(ins);
			ins.close();
		}
		catch(Exception exp)
		{
			if(ins!=null)
			{
				try{ins.close();}
				catch(Exception e){ e.printStackTrace();}
			}
		}
		return prop;
	}
}
