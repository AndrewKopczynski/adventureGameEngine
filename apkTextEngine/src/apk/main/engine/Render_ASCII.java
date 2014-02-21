package apk.main.engine;

public class Render_ASCII
{
	/** Maximum allowed offset at any given time.
	 * Precautionary. */
	//private static final int M_OFFSET_CAP = 1;
	
	/** Offset for X vision. */
	//private static int m_xOffset = 0;
	/** Offset for Y vision. */
	//private static int m_yOffset = 0;
	/** Offset for Z vision. */
	//private static int m_zOffset = 0;
	
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
	//private static int visibilityRange = 3;

	// for [range] lines above current y 
	// to [range] lines below current y
	// then get x lines
	public static String[] renderMap(int entX, int entY, int entZ, int visibilityRange)
	{
		if(visibilityRange < 1)
		{
			String[] map = {Graphic.getGraphic("lost"), "You can't see much of anything."};
			return map;
		}
		
		int i = 0;
		String[] map = new String[Math.max(0, (visibilityRange * 2))];
		
		for (int line = (entY + 1 - visibilityRange); line < (entY + visibilityRange); line++)
		{
			map[i] = renderMapLine(entX, entY, entZ, line, visibilityRange);
			i++;
		}
		
		if (World.isMapRoom(World.roomArray, entX, entY, entZ))
			map[i] = ("Exits: " + World.roomArray[entX][entY][entZ].getRoomExits());
		else
			map[i] = ("Exits: ?");
		
		/*m_xOffset = 0;
		m_yOffset = 0;
		m_zOffset = 0;*/
		return map;
	}
	
	/** Renders one horizontal line of the map in the X direction.
	 * 
	 * @param line Y level (vertical)
	 * @param z Z level (depth)
	 * @return A line of rooms in ASCII (eg. [_]---[_][^][v])
	 */
	private static String renderMapLine(int entX, int entY, int entZ, int line, int visibilityRange)
	{
		String str = "";
		for (int x = entX + 1 - visibilityRange; x < entX + visibilityRange; x++)
		{
			if (World.isMapRoom(World.roomArray, x, line, entZ))
			{
				//ignore offset for player drawing
				if (x == entX && line == entY)
				{
					str += insertIntoGraphic(Graphic.getGraphic("plyr"),
							World.roomArray[x][line][entZ].getRoomGraphic(),
							x, line, entZ, visibilityRange);
				} 
				
				else
				{
					str += World.roomArray[x][line][entZ].getRoomGraphic();
				}
			} 
			
			else
			{
				//ignore offset for play drawing
				//TODO: figure out what the above comment even means
				if (x == entX && line == entY)
				{
					String player = " " + Graphic.getGraphic("plyr") + " ";
					str += player;
				}
				
				else
				{
					str += Graphic.getGraphic("blnk");
				}
			}
		}
		return str;
	}
	
	/** Inserts a given graphic into the tile.
	 * <p>
	 * Typical usage:
	 * <ul>Inserting a player graphic into a tile:
	 * <li>eg. 'x' into '[_]' = '[x]'
	 * <li>eg. 'x' into '[___]' = '[_x_]'
	 * <li>eg. 'plr' into '[___]' = '[plr]'
	 * </ul>
	 * 
	 * <ul>Overriding a tile's graphic entirely
	 * <li>eg. '()' into '[]' = '()'
	 * </ul>
	 */
	private static String insertIntoGraphic(String graphic, String tile, int x, int y, int entZ, int visibilityRange)
	{
		String temp;
		String r = tile;
		String p = graphic;
		
		if (visibilityRange > 0)
		{
			if (p.length() % 2 == 0) // graphic is even length
			{
				temp = r.substring(0, (int) (r.length() / 2) - p.length()/2);
				temp += p;
				temp += r.substring((int) (r.length() / 2) + p.length()/2, r.length());
						
				return temp;
			}
			else // graphic is odd length
			{	
				temp = r.substring(0, (int) ((r.length() - 0.5) / 2) - p.length()/2);
				temp += p;
				temp += r.substring((int) (((r.length() - 0.5) / 2) + 1) + p.length()/2, r.length());
				
				return temp;
			}
		}
		else
		{
			return Graphic.getGraphic("lost");
		}
	}
	
	/*public static Integer getVisionRange()
	{
		return visibilityRange;
	}
	
	public static void setVisionRange(int i)
	{
		visibilityRange = i;
	}*/
	
	/*public static void setxOffset(int x)
	{
		m_xOffset = Math.min(M_OFFSET_CAP, x);
	}
	
	public static void setyOffset(int y)
	{
		m_yOffset = Math.min(M_OFFSET_CAP, y);
	}
	
	public static void setzOffset(int z)
	{
		m_zOffset = Math.min(M_OFFSET_CAP, z);
	}*/
}
