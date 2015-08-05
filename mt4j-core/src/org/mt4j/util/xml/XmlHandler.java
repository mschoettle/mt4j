/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4j.util.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * The Class XmlHandler.
 * 
 * @author Christopher Ruff
 */
public class XmlHandler {
	
	/** The xml handler. */
	private static XmlHandler xmlHandler = null;
	
	/** The name space aware. */
	private boolean nameSpaceAware;
	
	/** The validatig. */
	private boolean validatig;
	
	/**
	 * Instantiates a new xml handler.
	 */
	private XmlHandler(){
		nameSpaceAware = false;
		validatig = false;
	}

	/**
	 * Gets the single instance of XmlHandler.
	 * 
	 * @return single instance of XmlHandler
	 */
	public static XmlHandler getInstance(){
		if (xmlHandler == null){
			xmlHandler = new XmlHandler();
			return xmlHandler;
		}else
			return xmlHandler;
	}
	
	
	/**
	 * loads a xml file into memory and returns a document object.
	 * 
	 * @param file the file
	 * 
	 * @return the document
	 */
	public Document load(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		
		factory.setValidating(validatig); 
		factory.setNamespaceAware(nameSpaceAware); 
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(file);
			System.out.println("--> Parsed the xml file : " + file);
		} catch (SAXException sxe) {
			// Error generated during parsing
			Exception x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();

		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();

		} catch (IOException ioe) {
			// I/O error
			ioe.printStackTrace();
		}
				
		return document;
	}

	/**
	 * This method writes a DOM document to a file.
	 * 
	 * @param doc the doc
	 * @param file the file
	 * 
	 * @return true, if write xml file
	 */
	public boolean writeXmlFile(Document doc, File file) {
		try {
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputFormat format 	 = new OutputFormat(doc);
	        format.setLineWidth(900);
	        format.setIndenting(true);
	        format.setIndent(6);
	        format.setOmitComments(false);
	        
			XMLSerializer serializer = new XMLSerializer(fileOut, format); 
			serializer.serialize(doc);
			
			fileOut.close();
			System.out.println("Wrote the content of the document into the file: " + file);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}
	
	
	/**
	 * parses an xml file with the SaxParser and uses the provided
	 * defaulthandler to process the input.
	 * 
	 * @param defaultHandler the default handler
	 * @param filePath the file path
	 */
	public void saxParse(String filePath, DefaultHandler defaultHandler){
	 	SAXParserFactory spf = SAXParserFactory.newInstance();
	 	
	 	spf.setValidating(validatig);
	 	spf.setNamespaceAware(nameSpaceAware);
	 	
	       try{
	    	   //Dont parse external dtd, so we dont have to connect to http etc
	    	   spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
	    	   
		       SAXParser parser = spf.newSAXParser();
		       
		       parser.getXMLReader().setEntityResolver(new EntityResolver(){
					public InputSource resolveEntity(String arg0, String arg1) throws SAXException, IOException {
						return new InputSource(new ByteArrayInputStream(new byte[0]));
					}
		        });
		       
		       File file = new File(filePath);
		       if (file.exists()){
		    	   parser.parse(new File(filePath), defaultHandler);
		       }else{
		    	   InputStream in = null;
		    	   in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
		    	   if (in == null){
		    		   in = getClass().getResourceAsStream(filePath);
		    	   }
		    	   parser.parse(in, defaultHandler);
		       }
	       }
	       catch (Exception e){
	           System.err.println("Error while parsing! : " + filePath);
	          e.printStackTrace();
	       }
	}
	
	/**
	 * parses an xml file with the SaxParser and uses the provided
	 * defaulthandler to process the input.
	 * 
	 * @param defaultHandler the default handler
	 * @param string the string
	 */
	public void saxParseString(String string, DefaultHandler defaultHandler){
	 	SAXParserFactory spf = SAXParserFactory.newInstance();
	 	
	 	spf.setValidating(validatig);
	 	spf.setNamespaceAware(nameSpaceAware);
	 	
	 	byte stringAsByteArray[] = string.getBytes();
	 	ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stringAsByteArray);
	 	
	       try{
		       SAXParser parser = spf.newSAXParser();
		       
		       parser.getXMLReader().setEntityResolver(new EntityResolver(){
					public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
//						return new InputSource(new ByteArrayInputStream(new byte[0]));
						if (systemId.endsWith(".dtd"))
						      // this deactivates all DTDs by giving empty XML docs
						      return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
						    else return null;
					}
		        });
		       
		       parser.parse(byteArrayInputStream, defaultHandler);
	       }
	       catch (Exception e){
	           System.err.println("Error while parsing!");
	           System.err.println(e);
	       }
	}

	
	
	/**
	 * Checks if is name space aware.
	 * 
	 * @return true, if is name space aware
	 */
	public boolean isNameSpaceAware() {
		return nameSpaceAware;
	}

	/**
	 * Sets the name space aware.
	 * 
	 * @param nameSpaceAware the new name space aware
	 */
	public void setNameSpaceAware(boolean nameSpaceAware) {
		this.nameSpaceAware = nameSpaceAware;
	}

	/**
	 * Checks if is validatig.
	 * 
	 * @return true, if is validatig
	 */
	public boolean isValidatig() {
		return validatig;
	}

	/**
	 * Sets the validatig.
	 * 
	 * @param validatig the new validatig
	 */
	public void setValidatig(boolean validatig) {
		this.validatig = validatig;
	}
	
	
	
}
