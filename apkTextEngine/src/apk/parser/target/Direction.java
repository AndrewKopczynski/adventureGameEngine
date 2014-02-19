package apk.parser.target;

import apk.parser.reference.WordList;

public class Direction extends WordList
{
	public Direction()
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
		
		list.put("above", 		"above");
		list.put("below", 		"below");
		
		list.put("err", 		"I can't go that way.");
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
		String southwest = "w";
		String up = "u";
		String down = "d";
	
		//if (list.get(east).equalsIgnorescase(dir));
		
		if (list.get(dir).equalsIgnoreCase(east))
			x++;
		else if (list.get(dir).equalsIgnoreCase(west))
			x--;
		else if (list.get(dir).equalsIgnoreCase(south))
			y++;
		else if (list.get(dir).equalsIgnoreCase(north))
			y--;
		else if (list.get(dir).equalsIgnoreCase(up))
			z++;
		else if (list.get(dir).equalsIgnoreCase(down))
			z--;
		else if (list.get(dir).equalsIgnoreCase(northeast))
		{
			y--;
			x++;
		} 
		else if (list.get(dir).equalsIgnoreCase(southeast))
		{
			y++;
			x++;
		} 
		else if (list.get(dir).equalsIgnoreCase(southwest))
		{
			y++;
			x--;
		} 
		else if (list.get(dir).equalsIgnoreCase(northwest))
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
