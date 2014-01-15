package apk.main.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLReader {

	/* constructor:
	 * filePath
	 * array(?) of elements to be read from XML
	 * array(?) of attributes to be read from XML 
	 * 
	 * once created we should be able to access the XML pretty easily
	 * 
	 * methods:
	 * getRoot() {return String;}
	 * getAttribute(element, attributeToGet) {return String;}
	 * getLength(element) {return int;}
	 * -> equiv to 'number of nodes'
	 * -> for loops?
	 */
	/** File to read XML from. */
	private File m_fileXML;
	
	/** Seems to intialize docBuilderXML TODO: Temporary? */
	private DocumentBuilderFactory m_docFactoryXML;
	/** Parses the document for docXML TODO: Temporary? */
	private DocumentBuilder m_docBuilderXML;
	/** The document, once parsed. */
	private Document m_docXML;
	/** List of nodes for that map AND THAT MAP ONLY
	 * <p>
	 * READ: DO NOT MAKE STATIC AGAIN. It was a dumb idea. */
	private List<NodeList> m_docNodeList = new ArrayList<NodeList>();
	
	/** File path. TODO: Temporary? Do we still need this after loading an XML? */
	private String m_filePath;
	/** Elements to be read from file. */
	private String[] m_elements;
	
	/** Loads an XML File.
	 * 
	 * @param filePath Path of XML File to load
	 * @param elements Elements to be fetched/read from file
	 */
	public XMLReader(String filePath, String[] elements) 
	{
		m_filePath = filePath;
		m_elements = elements;
		
		try {
			
			// Setup File
			m_fileXML = new File(m_filePath);
			m_docFactoryXML = DocumentBuilderFactory.newInstance();
			m_docBuilderXML = m_docFactoryXML.newDocumentBuilder();
			m_docXML = m_docBuilderXML.parse(m_fileXML);
			m_docXML.getDocumentElement().normalize();
			
			// Setup NodeList
			setupNodeList();
			
		} catch (Exception e) {
			Logger.log(m_fileXML.getName() + " failed to load! " + e);
			e.printStackTrace();
		}
	}
	
	/** Creates a NodeList used later.
	 * <p>
	 * **The index of the element in m_elements will be the
	 * same as the index in m_docNodeList.
	 */
	private void setupNodeList()
	{	
		for (int i = 0; i < m_elements.length; i++) {
			NodeList docNodeList = m_docXML.getElementsByTagName(m_elements[i]);
			m_docNodeList.add(docNodeList);
		}
	}
	
	/** Gets the root.
	 * @return Root element's name
	 */
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

	/** Gets an attribute from a specific element.
	 * 
	 * @param element Element name (eg. <room>)
	 * @param n Nth Element to get attribute from
	 * eg. Getting the first <room> element's attribute
	 * versus getting the seventh <room> element's attribute.
	 * 
	 * @param attribute
	 * @return
	 */
	public String getAttribute(String element, int n, String attribute)
	{
		String temp = "NULL"; // returner
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
			temp = eElement.getAttribute(attribute);
		}
		
		if (temp.equals(""))
		{
			Logger.log(m_filePath + ": Didn't find attribute '" + attribute + "' under element '" + m_elements[index] + "'.");
			temp = "NULL";
		}
		return temp;	
	}
			
			/*NodeList tileNodeList = docXML.getElementsByTagName("tile");
			
			for (int i = 0; i < tileNodeList.getLength(); i++) {
				Node nNode = tileNodeList.item(i);
				
				//System.out.println("DEBUG TAG: " +  nNode.getNodeName());
				Logger.log("Tag found: " + nNode.getNodeName() + ", creating tile...");
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String name = eElement.getAttribute("name");
					String g = eElement.getAttribute("g");
					String e = eElement.getAttribute("e");
					new Graphic(name, g, e);
				}
			}
			Logger.log("All graphics from tileset (" + filePath + ") were loaded sucessfully.");
				
			*//** Loads the file, filePath is required to know where the map is. 
			 * Need loading zones for maps, or just have one huge map? 
			 * Regardless, loading zones should give new filePath. *//*
			
			Logger.log("Loading map...");
			File mapXmlFile = new File (mapFilePath);
			DocumentBuilderFactory mapDbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder mapDBuilder = mapDbFactory.newDocumentBuilder();
			Document mapDoc = mapDBuilder.parse(mapXmlFile);
			mapDoc.getDocumentElement().normalize();
			
			*//** Important. Never remove, only comment out. *//*
			//System.out.println("Root element: " + mapDoc.getDocumentElement().getNodeName());
			Logger.log("Root Element found: " + mapDoc.getDocumentElement().getNodeName());
			
			mapSize = Integer.parseInt(mapDoc.getDocumentElement().getAttribute("size"));
			roomArray = new Room[mapSize][mapSize];
			
			*//** Assign map name to room. *//*
			String map = mapDoc.getDocumentElement().getAttribute("name");
			
			*//** Gets list of all element areas, so attributes can be read. *//*
			NodeList mapNodeList = mapDoc.getElementsByTagName("room");
			
			for (int i = 0; i < mapNodeList.getLength(); i++) {
				Node nNode = mapNodeList.item(i);
				
				*//** Knowing what tag is currently loaded. *//*
				//System.out.println("DEBUG TAG: " + nNode.getNodeName());
				Logger.log("Tag found: " + nNode.getNodeName() + ", creating room...");
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					String name = eElement.getAttribute("name");
					String type = eElement.getAttribute("type");
					String coord = eElement.getAttribute("coord");
					String inv = eElement.getAttribute("i");
					Room room = new Room(map, name, type, coord, inv);
					roomArray[room.getRoomCoordsX()][room.getRoomCoordsY()] = room;
				}
			}
			Logger.log("All rooms in map (" + mapFilePath + ") were loaded sucessfully.");		
		} catch (Exception e) {
			Logger.log("Tileset OR Map ("+ filePath + " OR " + mapFilePath + ") failed to load! " + e);
			e.printStackTrace();
		}*/
}
