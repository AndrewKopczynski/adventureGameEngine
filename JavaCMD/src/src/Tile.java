package src;

public class Tile 
{
	private String m_name;
	private String m_graphic;
	private String m_exits;
	
	/** Creates a tile.
	 * 
	 * @param name Name of tile, used to get graphic from
	 * @param graphic Graphic (ASCII) display of tile
	 * @param exits Default(?)(TODO: custom exit support?) tile exits
	 */
	public Tile(String name, String graphic, String exits) 
	{
		m_name = name;
		m_graphic = graphic;
		m_exits = exits;
	}
	public String getName() 
	{
		return m_name;
	}
	public String getGraphic() 
	{
		return m_graphic;
	}
	public String getExits() 
	{
		return m_exits;
	}
}
