package com.bjw.bean;   

import java.io.ByteArrayOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ObjCheckSend
{
	private String sCheckheader = "--pkgname\nContent-Disposition:form-data;name='file';filename=GET_PKG_DETAIL_INFO.xml\n\n";
    private String sCheckEnd = "\n--pkgname--";
    private Document xmlDoc;
    
    public ObjCheckSend(String sPhoneModel, String sVersion)
    {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try
		{
			builder = factory.newDocumentBuilder();
			xmlDoc = builder.newDocument();
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		
		//创建根节点   
		Element root = xmlDoc.createElement("SKY_REQUEST"); 

		Element cmd1 = xmlDoc.createElement("SKY_CMD"); 
		cmd1.appendChild(xmlDoc.createTextNode("GET_PKG_DETAIL_INFO")); 
		
		Element cmd2 = xmlDoc.createElement("TERMINAL_NAME"); 
		cmd2.appendChild(xmlDoc.createTextNode(sPhoneModel)); 
		
		Element cmd3 = xmlDoc.createElement("BOARD_SOFT_VER"); 
		cmd3.appendChild(xmlDoc.createTextNode(sVersion)); 
		
		Element cmd4 = xmlDoc.createElement("PKG_NAME"); 
		cmd4.appendChild(xmlDoc.createTextNode("com.pantech.firmware.bin." + sPhoneModel));
		
        root.appendChild(cmd1); 
        root.appendChild(cmd2); 
        root.appendChild(cmd3); 
        root.appendChild(cmd4); 

        xmlDoc.appendChild(root);  
    }
    @Override
    public java.lang.String toString()
    {
    	StringBuilder buffer = new StringBuilder();
        buffer.append(sCheckheader);
        buffer.append(XmlToString(xmlDoc));
        buffer.append(sCheckEnd);
        return buffer.toString();
    }
    
    private static String XmlToString(Document doc)
	{
    	TransformerFactory  tf  =  TransformerFactory.newInstance();    
  
//    	t.setOutputProperty(\"encoding\",\"GB23121\");//解决中文问题，试过用GBK不行   
    	  
    	ByteArrayOutputStream  bos  =  new  ByteArrayOutputStream();   
    	  
    	try
		{
    		Transformer t = tf.newTransformer();  
			t.transform(new DOMSource(doc), new StreamResult(bos));
		} catch (TransformerException e)
		{
			e.printStackTrace();
		}    
    	  
    	String xmlStr = bos.toString();
    	return xmlStr;
	}
    
}
