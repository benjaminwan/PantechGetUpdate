package com.bjw.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ObjCheckRec
{
	public ArrayList<ObjCheckRecBean> mListInfo=null;
    public ObjCheckRec(String sRec)
    {
    	if (sRec==null || sRec.equals(""))
		{
			return;
		}
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;
        Document document =	null;
		try
		{
			builder = factory.newDocumentBuilder();
			document = builder.parse(new ByteArrayInputStream(sRec.getBytes())); 
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		} catch (SAXException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}  


        mListInfo = new ArrayList<ObjCheckRecBean>();
        
        Element info = (Element) document.getElementsByTagName("APK_INFO").item(0);
        if (info!=null && info.hasChildNodes())
		{
        	NodeList apkInfo = info.getChildNodes(); 

            ObjCheckRecBean mRecBean;
            for (int i = 0; i < apkInfo.getLength(); i++)
    		{
            	mRecBean = new ObjCheckRecBean(apkInfo.item(i).getNodeName(), apkInfo.item(i).getTextContent());
            	mListInfo.add(mRecBean);
    		}
		}

    }
    @Override
    public String toString()
    {
    	StringBuilder buffer = new StringBuilder();
        if (mListInfo.size()>0 )
        {
        	for (int i = 0; i < mListInfo.size(); i++)
			{
				buffer.append(mListInfo.get(i).toString());
				
			}
        }
        return buffer.toString();
    }
}
