package apk.main.engine;

import java.io.*;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class XML //TODO: maybe change writing to dom4j? It works pretty well right now tho
{
	private String m_filePath;
	private PrintWriter m_out = null;
	private int m_tab = 0;
	
	public static Document parse(URL url) throws FileNotFoundException
	{
		SAXReader reader = new SAXReader();
		Document document;
		try
		{
			document = reader.read(url);
		}
		catch (DocumentException e)
		{
			throw new FileNotFoundException();
		}
		
		return document;
	}
	
	public XML(String filePath)
	{
		m_filePath = filePath;
		
		/** Clear save file in preparation for writing to it. */
		try
		{
	        BufferedWriter clear = new BufferedWriter(new FileWriter(m_filePath + ".xml"));
	        clear.close();
		} 
		catch (IOException e)
		{
	        System.out.println("!CRITICAL! Couldn't clear old XML @ " + m_filePath + "!");
	    }
		
		try
		{
		m_out = new PrintWriter(new BufferedWriter(new FileWriter(m_filePath + ".xml", true)));
		m_out.println("<?xml version=\"1.0\" ?>");
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
	}
    
    public void writeTag(String element, String[] attributes, String[] values)
    {
    	String temp = tab();
    	temp += "<" + element;
    	
    	for (int i = 0; i < attributes.length; i++)
    	{
    		temp += " " + attributes[i] + "=" + "\"" + values[i] + "\"";
    	}
    	
    	temp += "/>";
    	
    	m_out.println(temp);
    }
    
    public void writeTag(String element, String attribute, String value)
    {
    	String temp = tab();
    	temp += "<" + element;
    	
    	temp += " " + attribute + "=" + "\"" + value + "\"";
    	temp += "/>";
    	
    	m_out.println(temp);
    }
    
    public void writeOpenTag(String root)
    {
		String temp = tab();
		temp += "<" + root + ">";
		
    	m_out.println(temp);
    	m_tab++;
    }
    
    public void writeOpenTag(String root, String attribute, String value)
    {
    	String temp = tab();
		temp += "<" + root;
    	temp += " " + attribute + "=" + "\"" + value + "\"";
    	temp += ">";
    	
    	m_out.println(temp);
    	m_tab++;
    }
    
    public void writeOpenTag(String root, String[] attributes, String[] values)
    {
    	String temp = tab();
		temp += "<" + root;
		for (int i = 0; i < attributes.length; i++)
    	{
    		temp += " " + attributes[i] + "=" + "\"" + values[i] + "\"";
    	}
		temp += ">";
		
		m_out.println(temp);
		m_tab++;
    }
    
    public void writeCloseTag(String root)
    {
    	m_tab--;
    	
    	String temp = tab();
    	temp += "</" + root + ">";
    	
    	m_out.println(temp);
    }
    
    public void writeTextContent(String element, String text)
    {
    	String temp = tab();
    	temp += "<" + element + ">" + text + "</" + element + ">"; 
    	
    	m_out.println(temp);
    }
    
    public void close()
    {
    	m_out.close();
    }
    
    private String tab()
    {
    	String temp = "";
    	
    	for (int i = 0; i < m_tab; i++)
    	{
    		temp += "\t";
    	}
    	
    	return temp;
    }
}
