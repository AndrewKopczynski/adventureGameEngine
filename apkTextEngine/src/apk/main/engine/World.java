package apk.main.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;

public class World 
{
	//TODO: overhaul map system, make it a point map instead of exit-based
	//TODO: level editor, should not be writing any maps by hand anymore
	
	public static final int HEIGHT_LIMIT = 512;
	
	private static int m_mapSize;
	private static String m_worldFilePath;
	private static String m_tileFilePath;
	private static Room[][][] m_world;
	
	public World(URL worldURL, URL tileURL) throws FileNotFoundException
	{
		loadWorld(worldURL);
		loadTileset(tileURL);
	}
	
	public World(String worldFilePath, String tileFilePath) throws FileNotFoundException
	{
		try
		{
			m_worldFilePath = worldFilePath;
			m_tileFilePath = tileFilePath;
			
			URL worldURL = new File(worldFilePath).toURI().toURL();
			URL tileURL = new File(tileFilePath).toURI().toURL();
			
			new World(worldURL, tileURL);
		}
		catch (MalformedURLException e)
		{
			throw new FileNotFoundException();
		}
	}
	
	private void loadWorld(URL worldURL) throws FileNotFoundException
	{
		Document worldD = XML.parse(worldURL);
		
		Element root = worldD.getRootElement();
		String worldName = root.attributeValue("name");
		
		m_mapSize = Integer.parseInt(root.attributeValue("size"));
		m_world = new Room[m_mapSize][m_mapSize][HEIGHT_LIMIT];
		
		/* Iterates through all elements with name "room". */
		for (Iterator<Element> i = root.elementIterator("room"); i.hasNext();)
		{
            Element element = (Element) i.next();
            
            String name = element.attributeValue("name");
            String type = element.attributeValue("type");
            String coord = element.attributeValue("coord");
            
            Room room = new Room(worldName, name, type, coord);
            m_world[room.getX()][room.getY()][room.getZ()] = room;
            
            System.out.println("[ROOM] " + m_world[room.getX()][room.getY()][room.getZ()]);
        }
	}
	
	public void loadTileset(URL tileURL) throws FileNotFoundException
	{
		Document tileD = XML.parse(tileURL);
		
		Element root = tileD.getRootElement();
		
		/* Iterates through all elements with name "tile" */
		for (Iterator<Element> i = root.elementIterator("tile"); i.hasNext();)
		{
			Element element = (Element) i.next();
			
			String name = element.attributeValue("name");
			String graphic = element.attributeValue("g");
			String exits = element.attributeValue("e");
			
			new Graphic(name, graphic, exits);
			
			System.out.println("[TILE] " + graphic);
		}
	}
	
	/** Check for Room[][] Arrays to see if they're empty.
	 *
	 * @return True if not empty, false if empty. */
	public static boolean isMapRoom(Room[][][] array, int x, int y, int z) 
	{	
		try
		{
			// ignore negatives, avoids out of bounds index
			if (x < 0 || y < 0 || z < 0)
				return false;
			
			// ignore values outside of array, avoids out of bounds index
			if (x > m_mapSize - 1 || y > m_mapSize - 1 || z > m_mapSize - 1)
				return false;
			else if (array[x][y][z] != null) 
				return true;
			else 
				return false;
		}
		catch(Exception e)
		{
			System.err.println("Checked for a room out of bounds! Ignoring room(s)...");
			return false;
		}
	}
	
	public static int getMapSize()
	{
		return m_mapSize;
	}
	
	public static Room[][][] getWorld()
	{
		return m_world;
	}
	
	public static String getWorldFilePath()
	{
		return m_worldFilePath;
	}
	public static String getTileFilePath()
	{
		return m_tileFilePath;
	}
}
