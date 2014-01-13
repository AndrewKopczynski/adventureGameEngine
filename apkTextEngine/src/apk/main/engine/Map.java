package apk.main.engine;

public class Map 
{
	//TODO: overhaul map system, make it a point map instead of exit-based
	//TODO: level editor, should not be writing any maps by hand anymore
	
	/** Total size of map */
	public static int mapSize;
	
	/** Arbitrary limit, need to figure out a better way to handle later. */
	public static final int HEIGHT_LIMIT = 512;
	
	/** Map is loaded into this 3D array */
	public static Room[][][] roomArray;
	
	/** Loads a map using a given map file and tileset.
	 * 
	 * @param mapFilePath Filepath to the map.xml
	 * @param tileFilePath Filepath to the tileset.xml
	 */
	public Map(String mapFilePath, String tileFilePath)
	{	
		/** Tileset */
		try 
		{
			Logger.log("Loading tileset...");
			
			String[] tElements = {"tile"};
			XML tileXML = new XML(tileFilePath, tElements);
			
			for (int i = 0; i < tileXML.getNumOfElements(tElements[0]); i++)
			{
				String name = tileXML.getAttribute(tElements[0], i, "name");
				String g = tileXML.getAttribute(tElements[0], i, "g");
				String e = tileXML.getAttribute(tElements[0], i, "e");
				System.out.println(g);
				new Graphic(name, g, e);
			}	
		}
		catch (Exception e)
		{
			Logger.log("Tileset ("+ tileFilePath + ") failed to load! " + e);
			e.printStackTrace();
		}
		Logger.log("Tileset (" + tileFilePath + ") was loaded sucessfully.");
		
		/** Map ------------------------------------------------------------------ */
		try
		{	
			Logger.log("Loading map...");
			
			String[] mElements = {"room"};
			XML mapXML = new XML(mapFilePath, mElements);
			
			String mapName = mapXML.getRootAttribute("name");
			mapSize = Integer.parseInt(mapXML.getRootAttribute("size"));
			roomArray = new Room[mapSize][mapSize][HEIGHT_LIMIT];
			
			for (int i = 0; i < mapXML.getNumOfElements(mElements[0]); i++)
			{
				String name = mapXML.getAttribute(mElements[0], i, "name");
				String type = mapXML.getAttribute(mElements[0], i, "type");
				String coord = mapXML.getAttribute(mElements[0], i, "coord");
				String inv = mapXML.getAttribute(mElements[0], i, "i");
				Room room = new Room(mapName, name, type, coord, inv);
				roomArray[room.getX()][room.getY()][room.getZ()] = room;
				System.out.println(roomArray[room.getX()][room.getY()][room.getZ()]);
			}		
		} 
		catch (Exception e)
		{
			Logger.log("Tileset OR Map (" + mapFilePath + ") failed to load! " + e);
			e.printStackTrace();
		}
		Logger.log("Map (" + mapFilePath + ") was loaded sucessfully.");
	}
	
	/** Check for Room[][] Arrays to see if they're empty.
	 *
	 * @return True if not empty, false if empty. */
	public static boolean isMapRoom(Room[][][] array, int x, int y, int z) 
	{
		/** For debugging TODO: Remove in later builds.
		 */
		
		try {
			// ignore negatives, avoids out of bounds index
			if (x < 0 || y < 0 || z < 0)
			{
				return false;
			}
			
			// ignore values outside of array, avoids out of bounds index
			if (x > mapSize - 1 || y > mapSize - 1 || z > mapSize - 1)
			{
				return false;
			}
			
			else if (array[x][y][z] != null) 
			{
				return true;
			} 
			
			else 
			{
				return false;
			}
		}
		catch(Exception e)
		{
			System.out.println("Checked for a room out of bounds! Ignoring room(s)...");
			return false;
		}
	}
}
