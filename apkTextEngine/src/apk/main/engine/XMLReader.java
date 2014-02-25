package apk.main.engine;

import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class XMLReader
{
	public static Document parse(URL url) throws DocumentException
	{
		SAXReader reader = new SAXReader();
		Document document = reader.read(url);
		
		return document;
	}
}
