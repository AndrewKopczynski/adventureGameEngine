package apk.parser.action;

import apk.parser.reference.WordList;

public class ShortMove extends WordList
{
	/** ShortMove is specific only. */
	
	public ShortMove()
	{
		list.put("north", 		"n");
		list.put("east", 		"e");
		list.put("south",		"s");
		list.put("west", 		"w");
		list.put("northeast", 	"ne");
		list.put("northwest", 	"nw");
		list.put("southeast", 	"se");
		list.put("southwest", 	"sw");
		list.put("n", 			"n");
		list.put("e", 			"e");
		list.put("s", 			"s");
		list.put("w", 			"w");
		list.put("ne",			"ne");
		list.put("nw", 			"nw");
		list.put("se", 			"se");
		list.put("sw", 			"sw");
		list.put("up", 			"u");
		list.put("down", 		"d");
		list.put("u", 			"u");
		list.put("d", 			"d");
		
		list.put("err", 		"[SHORTMOVE] Go where?");
	}
	
	public int[] parse(String dir)
	{
		int x = 0;
		int y = 0;
		int z = 0;
		
		String north = "n";
		String east = "e";
		String south = "s";
		String west = "w";
		String northeast = "ne";
		String northwest = "nw";
		String southeast = "se";
		String southwest = "sw";
		String up = "u";
		String down = "d";
		
		if (dir.equals(east))
			x++;
		else if (dir.equals(west))
			x--;
		else if (dir.equals(south))
			y++;
		else if (dir.equals(north))
			y--;
		else if (dir.equals(up))
			z++;
		else if (dir.equals(down))
			z--;
		else if (dir.equals(northeast))
		{
			y--;
			x++;
		} 
		else if (dir.equals(southeast))
		{
			y++;
			x++;
		} 
		else if (dir.equals(southwest))
		{
			y++;
			x--;
		} 
		else if (dir.equals(northwest))
		{
			x--;
			y--;
		}
		else 
			return null;
		
		int[] vel = {x, y, z};
		
		return vel;
	}
}
