package com.bjw.bean;

public class ObjCheckRecBean
{
	public String TagName;
    public String Value ;
    public ObjCheckRecBean(String sTag, String sValue)
    {
        TagName = sTag;
        Value = sValue;
    }
    @Override
    public String toString()
    {
    	StringBuilder buffer = new StringBuilder();
        buffer.append("TagName:" + TagName+"\n");
        buffer.append("Value:" + Value + "\n");
        return buffer.toString();
    }

}
