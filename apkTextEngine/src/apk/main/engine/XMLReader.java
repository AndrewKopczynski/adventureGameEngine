package apk.main.engine;

import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class XMLReader
{

	/** File to read XML from. *//*
	private File m_fileXML;
	
	*//** Seems to intialize docBuilderXML TODO: Temporary? *//*
	private DocumentBuilderFactory m_docFactoryXML;
	*//** Parses the document for docXML TODO: Temporary? *//*
	private DocumentBuilder m_docBuilderXML;
	*//** The document, once parsed. *//*
	private Document m_docXML;
	*//** List of nodes for that map AND THAT MAP ONLY
	 * <p>
	 * READ: DO NOT MAKE STATIC AGAIN. It was a dumb idea. *//*
	private List<NodeList> m_docNodeList = new ArrayList<NodeList>();
	
	*//** File path. TODO: Temporary? Do we still need this after loading an XML? *//*
	private String m_filePath;
	*//** Elements to be read from file. *//*
	private String[] m_elements;*/
	
	public static Document parse(URL url) throws DocumentException
	{
		SAXReader reader = new SAXReader();
		Document document = reader.read(url);
		
		return document;
	}
	
	
	/** Loads an XML File.
	 * 
	 * @param filePath Path of XML File to load
	 * @param elements Elements to be fetched/read from file
	 */
	/*public XMLReader(String filePath, String[] elements) throws FileNotFoundException
	{
		m_filePath = filePath;
		m_elements = elements;
		
		try {
			m_fileXML = new File(m_filePath);
			m_docFactoryXML = DocumentBuilderFactory.newInstance();
			m_docBuilderXML = m_docFactoryXML.newDocumentBuilder();
			m_docXML = m_docBuilderXML.parse(m_fileXML);
	
			m_docXML.getDocumentElement().normalize();
			
			// Setup NodeList
			setupNodeList();
			
		}
		catch (Exception e)
		{
			String err = m_filePath + " failed to load!";
			System.out.println(err);
			Logger.log(err);
			throw new FileNotFoundException(m_filePath + " failed to load!"); //TODO rewrite this whole class fadasd
		}
	}
	
	*//** Creates a NodeList used later.
	 * <p>
	 * **The index of the element in m_elements will be the
	 * same as the index in m_docNodeList.
	 *//*
	private void setupNodeList()
	{	
		for (int i = 0; i < m_elements.length; i++) {
			NodeList docNodeList = m_docXML.getElementsByTagName(m_elements[i]);
			m_docNodeList.add(docNodeList);
		}
	}
	
	*//** Gets the root.
	 * @return Root element's name
	 *//*
	public String getRootName()
	{
		return m_docXML.getDocumentElement().getNodeName();
	}
	
	public String getRootAttribute(String attribute)
	{
		return m_docXML.getDocumentElement().getAttribute(attribute);
	}
	
	public Integer getNumOfElements(String element)
	{
		int index = -1;
		for (int i = 0; i < m_elements.length; i++)
		{
			if (m_elements[i].equalsIgnoreCase(element))
			{
				index = i;
			}
		}
		return m_docNodeList.get(index).getLength();
	}

	*//** Gets an attribute from a specific element.
	 * 
	 * @param element Element name (eg. <room>)
	 * @param n Nth Element to get attribute from
	 * eg. Getting the first <room> element's attribute
	 * versus getting the seventh <room> element's attribute.
	 * 
	 * @param attribute
	 * @return
	 *//*
	public String getAttribute(String element, int n, String attribute)
	{
		String temp = null; // returner
		int index = -1;
		
		for (int i = 0; i < m_elements.length; i++)
		{
			if (m_elements[i].equalsIgnoreCase(element))
			{
				index = i;
			}
		}
		
		if (index == -1)
		{	//Early return to avoid crash if element is invalid.
			Logger.log(m_filePath + ": Didn't find element '" + element + "', returned early!");
			return temp;
		}
		
		// ----------
		Node nNode;
		
		try
		{
			nNode = m_docNodeList.get(index).item(n);
		}
		catch (Exception e)
		{
			System.out.println("Tried to get elements from an .xml that was not loaded!");
			return null;
		}
		
		
		if (nNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element eElement = (Element) nNode;
			temp = eElement.getAttribute(attribute);
		}
		
		if (temp.equals(""))
		{
			Logger.log(m_filePath + ": Didn't find attribute '" + attribute + "' under element '" + m_elements[index] + "'.");
			temp = "NULL";
		}
		return temp;	
	}
	
	public String[] getChildren(String element, int n)
	{
		String temp[] = new String[0]; // returner
		int index = -1;
		
		for (int i = 0; i < m_elements.length; i++)
		{
			if (m_elements[i].equalsIgnoreCase(element))
			{
				index = i;
			}
		}
		
		if (index == -1)
		{	//Early return to avoid crash if element is invalid.
			Logger.log(m_filePath + ": Didn't find element '" + element + "', returned early!");
			return temp;
		}
		
		Node nNode = m_docNodeList.get(index).item(n);
		
		if (nNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element eElement = (Element) nNode;
			temp = new String[eElement.getChildNodes().getLength()];
			for (int i = 0; i < eElement.getChildNodes().getLength(); i++)
			{
				temp[i] = eElement.getChildNodes().item(i).getTextContent();
			}
		}	
		return temp;	
	}*/
}
