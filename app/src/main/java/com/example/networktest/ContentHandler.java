package com.example.networktest;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandler extends DefaultHandler {
    private String nodeName;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;

    @Override
    public void startDocument() throws SAXException { //开始解析XML时调用
        id=new StringBuilder();
        name=new StringBuilder();
        version =new StringBuilder();
    }

    @Override //解析某个节点时调用
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //记录当前节点名
        nodeName=localName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //根据当前节点名判断内容添加到哪一个StringBuilder 对象中
        if("id".equals(nodeName)){
            id.append(ch,start,length);
        }else if("name".equals(nodeName)){
            name.append(ch,start,length);
        }else if("version".equals(nodeName)){
            version.append(ch,start,length);
        }
    }
    @Override //解析完成某个节点调用
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("app".equals(localName)){
            Log.d("ContentHandler","id is "+id.toString().trim());
            Log.d("ContentHandler","name is "+name.toString().trim());
            Log.d("ContentHandler","version is "+version.toString().trim());
            //最后要将StringBuilder 清空掉
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
