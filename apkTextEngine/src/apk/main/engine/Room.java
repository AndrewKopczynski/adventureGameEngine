package apk.main.engine;

public class Room 
{
	private String m_mapName;
	private String m_roomName;
	private String m_type;
	private int m_coords[] = new int[3];
	
	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;
	
	/** Creates a new room.
	 * 
	 * @param map Map to which this room belongs to
	 * @param name Name of the room
	 * @param type Type of the room (used for graphics)
	 * @param coords X/Y/Z coordinates of the room, used for rendering and movement
	 * @param invFilePath File path for the inventory of the room. 'd' is default
	 */
	public Room(String map, String name, String type, String coords)
	{
		String[] temp;
		
		m_mapName = map;
		m_roomName = name;
		m_type = type;
		
		//TODO: Redo coordinates system in a way that makes sense.
		//TODO: 3D maps, rendered 2D?
		temp = coords.split(",");
		int array[] = {Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2])};
		m_coords = array;
		
		//m_inv = new Inventory(this);
	}
	
	/** Gets the graphic for this room.
	 * Uses the room's type and matches it to a tile name.
	 * 
	 * @return Graphic for room, string
	 */
	public String getRoomGraphic()
	{
		return Graphic.getGraphic(m_type);
	}
	
	/** Gets the exits for this room.
	 * Uses the room's type and matches it to a tile name.
	 * 
	 * @return Exits for room, string
	 */
	public String getRoomExits()
	{
		return Graphic.getExits(m_type);
	}
	
	/** Gets the name of the room.
	 * 
	 * @return Name property (attribute) of room
	 */
	public String getRoomName()
	{
		return m_roomName;
	}
	
	/** Gets the type of the room.
	 * 
	 * @return Type property (attribute) of room
	 */
	public String getRoomType()
	{
		return m_type;
	}
	
	/** Gets the X coordinate of the room.
	 * 
	 * @return X coordinate
	 */
	public int getX()
	{
		int x = m_coords[X];
		return x;
	}
	
	/** Gets the Y coordinate of the room.
	 * 
	 * @return Y coordinate
	 */
	public int getY()
	{
		int y = m_coords[Y];
		return y;
	}
	
	/** Gets the Z coordinate of the room.
	 * 
	 * @return Z coordinate
	 */
	public int getZ()
	{
		int z = m_coords[Z];
		return z;
	}
	
	/** Gets the XYZ
	 * 
	 * @return "X-Y-Z"
	 */
	public String getXYZ()
	{
		return m_coords[X] + "-" + m_coords[Y] + "-" + m_coords[Z];
	}
	
	/*public Inventory getInventory()
	{
		return m_inv;
	}*/
	
	public String getFilePath() 
	{
		return "maps/" + m_mapName + "/" + "inv_" + toString();
	}
	
	/** Debug
	 * @return Retuns all room information. */
	public String toString()
	{
		return m_roomName + "[" + getXYZ() + "]";
	}
}
