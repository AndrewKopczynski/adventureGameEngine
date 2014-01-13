package apk.main.engine;

import apk.main.server.Server;

public class Render_ASCII
{
	/** Sight range. 
	 * <p>
	 * Implemented for sanity's sake, otherwise map printing took absolutely ages
	 * to print large maps.
	 * <p>
	 * Usage:
	 * <ul>
	 * <li>0: 0x0 - Map replaced with a ? mark, cannot even see self
	 * 					or surroundings. 
	 * 
	 * 					Appropriate for pitch-black rooms.
	 * 
	 * <li>1: 1x1 - [T]iny sight range, player can only see self, and
	 * 					the room/contents of the room they are in.
	 * 					Impares ability to read things or look at map.
	 * 
	 * 					Appropriate for moon-less nights or dark rooms.
	 * 					
	 * <li>2: 3x3 - [S]mall sight range, player can only see adjacent tiles.
	 * 					
	 * 					Appropriate for moon-lit nights or dimly lit rooms.
	 * 
	 * <li>3: 5x5 - [M]edium sight range; default sight range.
	 * 
	 * 					Used by default, used for a typical day outside
	 * 					or lit interiors.
	 * 
	 * <li>4: 7x7 - [L]arge sight range, intended for high ground.
	 * 
	 * 					Appropriate for the top of a building or
	 * 					mountain or whatever.
	 * 
	 * <li>5: 9x9 - [E]xtra large sight range
	 * </ul>
	 * <p>
	 * Avoid exceeding ranges above 4 or 9x9 - the longer the sight range,
	 * the longer it takes to print the 'map' view.
	 */
	private static int m_range = 3;
	
	// for [range] lines above current y 
	// to [range] lines below current y
	// then get x lines
	public static String[] renderMap()
	{
		int z = Server.player.getZ();
		int i = 0;
		String[] map = new String[Math.max(0, (m_range * 2) - 1)];
		
		for (int y = Server.player.getY() + 1 - m_range; y < Server.player.getY() + m_range; y++)
		{
			map[i] = renderMapLine(y, z);
			i++;
		}
		return map;
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
		for (int x = Server.player.getX() + 1 - m_range; x < Server.player.getX() + m_range; x++)
		{
			if (Map.isMapRoom(Map.roomArray, x, y, z))
			{
				if (x == Server.player.getX() && y == Server.player.getY())
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
				if (x == Server.player.getX() && y == Server.player.getY())
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
