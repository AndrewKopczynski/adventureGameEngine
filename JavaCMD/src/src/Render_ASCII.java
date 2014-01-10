package src;

public class Render_ASCII
{
	/** Sight range. 
	 * <p>
	 * Implemented for sanity's sake, otherwise map printing took absolutely ages
	 * to print large maps.
	 * <p>
	 * Usage:
	 * <ul>
	 * <li>-1: 0x0 - Map replaced with a ? mark, cannot even see self
	 * 					or surroundings. 
	 * 
	 * 					Appropriate for pitch-black rooms.
	 * 
	 * <li> 0: 1x1 - [T]iny sight range, player can only see self, and
	 * 					the room/contents of the room they are in.
	 * 					Impares ability to read things or look at map.
	 * 
	 * 					Appropriate for moon-less nights or dark rooms.
	 * 					
	 * <li> 1: 3x3 - [S]mall sight range, player can only see adjacent tiles.
	 * 					
	 * 					Appropriate for moon-lit nights or dimly lit rooms.
	 * 
	 * <li> 2: 5x5 - [M]edium sight range; default sight range.
	 * 
	 * 					Used by default, used for a typical day outside
	 * 					or lit interiors.
	 * 
	 * <li> 3: 7x7 - [L]arge sight range, intended for high ground.
	 * 
	 * 					Appropriate for the top of a building or
	 * 					mountain or whatever.
	 * 
	 * <li> 4: 9x9 - [E]xtra large sight range
	 * </ul>
	 * <p>
	 * Avoid exceeding ranges above 4 or 9x9 - the longer the sight range,
	 * the longer it takes to print the 'map' view.
	 */
	private static int m_range = 2;
	
	// TODO: check performance - array copy VS array accessing
	public static void renderMap()
	{
		int z = Client.player.getZ();
		// explained:
		// for [range] lines above current y 
		// to [range] lines below current y
		// then get x lines
		for (int y = Client.player.getY() + 1 - m_range; y < Client.player.getY() + m_range; y++)
		{
			System.out.println(renderMapLine(y, z));
		}
		// stabilizer, causes less jitter in debug terminal
		System.out.print("");
	}
	
	/** Renders one horizontal line of the map in the X direction.
	 * 
	 * @param y Y level (vertical)
	 * @param z Z level (depth)
	 * @return A line of rooms in ASCII (eg. [_]---[_][^][v])
	 */
	private static String renderMapLine(int y, int z)
	{
		//force local rendering for now (7x7)
		String line = "";
		for (int x = Client.player.getX() + 1 - m_range; x < Client.player.getX() + m_range; x++)
		{
			if (Map.isMapRoom(Map.roomArray, x, y, z))
			{
				if (x == Client.player.getX() && y == Client.player.getY())
				{
					String player = Map.roomArray[x][y][z].getRoomGraphic().substring(0, 1)
						+ Graphic.getGraphic("plyr")
						+ Map.roomArray[x][y][z].getRoomGraphic().substring(2, 3);
					line += player;
				} 
				
				else
				{
					line += Map.roomArray[x][y][z].getRoomGraphic();
				}
			} 
			
			else
			{
				if (x == Client.player.getX() && y == Client.player.getY())
				{
					String player = " " + Graphic.getGraphic("plyr") + " ";
					line += player;
				}
				
				else
				{
					line += Graphic.getGraphic("blnk");
				}
				
			}
		}
		return line;
	}
	
	public static Integer getRange()
	{
		return m_range;
	}
	
	public static void setRange(int i)
	{
		m_range = i;
	}
}
