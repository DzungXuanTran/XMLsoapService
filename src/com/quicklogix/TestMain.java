package com.quicklogix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.quicklogix.services.XMLService;
import com.ximpleware.VTDException;

public class TestMain {
	public static void main(String[] args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException, TransformerException, VTDException {
		XMLService service = new XMLService();
		/*File[] dir = new File("E:/New folder").listFiles();
		if (dir != null && dir.length > 0) {
			BufferedReader reader = null;
			StringBuffer sb = null;
			String[] xmls = new String[dir.length];
			int i = 0;
			for (File f : dir) {
				if (f.isFile()) {
					try {
						reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
						sb = new StringBuffer();
						String line;
						while ((line = reader.readLine()) != null) {
							sb.append(line.trim() + "\n");
						}
						String xml = sb.toString().trim().replaceFirst("^[\\W]*<", "<");
						xmls[i] = xml;
						i++;
					} 
					finally {
						if (reader != null) {
							reader.close();
						}
					}
				}
			}
			String fileName = service.addMultipleXML("quicklogix", xmls);
			System.out.println(fileName);
		}*/
		/*BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("E:/siemens.xml"), "UTF-8"));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line.trim() + "\n");
		}
		reader.close();
		String xml = sb.toString().trim().replaceFirst("^[\\W]*<", "<");
		String fileName = service.addXML("quicklogix", xml);
		System.out.println(fileName);*/
//		String[] strs = service.getXpath("quicklogix", "bookstore");
//		for (int i = 0; i < strs.length; i++) {
//			System.out.println(strs[i]);
//		}
		service.test();
	}
}
