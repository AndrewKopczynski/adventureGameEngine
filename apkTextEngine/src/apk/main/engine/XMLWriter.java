package apk.main.engine;

import java.io.*;

public class XMLWriter
{
	
	private String m_filePath;
	private PrintWriter out = null;
	
	public XMLWriter(String filePath)
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
		out = new PrintWriter(new BufferedWriter(new FileWriter(m_filePath + ".xml", true)));
		out.println("<?xml version=\"1.0\" ?>");
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
	}
    
    public void writeTag(String element, String[] attributes, String[] values)
    {
    	String temp = "\t<" + element;
    	for (int i = 0; i < attributes.length; i++)
    	{
    		temp += " " + attributes[i] + "=" + "\"" + values[i] + "\"";
    	}
    	temp += "/>";
    	out.println(temp);
    }
    
    public void writeTag(String element, String attribute, String value)
    {
    	String temp = "\t<" + element;
    	temp += " " + attribute + "=" + "\"" + value + "\"";
    	temp += "/>";
    	out.println(temp);
    }
    
    public void writeRootOpenTag(String root, String attribute, String value)
    {
		String temp = "<" + root;
    	temp += " " + attribute + "=" + "\"" + value + "\"";
    	temp += ">";
    	out.println(temp);
    }
    
    public void writeRootOpenTag(String root, String[] attributes, String[] values)
    {
		String temp = "<" + root;
		for (int i = 0; i < attributes.length; i++)
    	{
    		temp += " " + attributes[i] + "=" + "\"" + values[i] + "\"";
    	}
		temp += ">";
		out.println(temp);
    }
    
    public void writeRootCloseTag(String root)
    {
    	String temp = "<" + root + "/>";
    	out.println(temp);
    }
    
    public void close()
    {
    	out.close();
    }
}
