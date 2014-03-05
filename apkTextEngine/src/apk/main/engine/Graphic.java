package apk.main.engine;

import java.util.ArrayList;
import java.util.List;

public class Graphic {

	private static List<Tile> m_tileList = new ArrayList<Tile>();
	
	public Graphic(String name, String graphic, String exits) 
	{
		m_tileList.add(new Tile(name, graphic, exits));
	}
	
	/** Gets the tile's ASCII Graphic by name of the tile.
	 * 
	 * @param name Name attribute of tile
	 * @return 1 x 'X' ASCII graphic (eg. [_] or [], dependant on tileset)
	 */
	public static String getGraphic(String name)
	{
		String graphic = "ERR";
		for(Tile i : m_tileList) {
			if (i.getName().equals(name))
			{
				graphic = i.getGraphic();
			}
		}
		return graphic;
	}
	
	/** Gets the tile's exits by name of the tile.
	 * 
	 * @param name Name attribute of tile
	 * @return Exits in format (eg. 'n, e, w, s, u, d')
	 */
	public static String getExits(String name)
	{
		String exits = "ERR";
		for(Tile i : m_tileList)
		{
			if (i.getName().equals(name))
			{
				exits = i.getExits();
			}
		}
		return exits;
	}
}
