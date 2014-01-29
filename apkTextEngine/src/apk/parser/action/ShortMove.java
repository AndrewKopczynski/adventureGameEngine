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
		
		list.put("err", 		"I can't go that way.");
	}
	
	/** SHORT MOVEMENT ------------------------------------------------	|
	 * Short: north
	 */
	/*if (vnn.length == 1 && m_shortMove.contains(vnn[0])) 
	{
		entity.move(m_shortMove.getMeaning(vnn[0]));
		return Render_ASCII.renderMap(entity.getX(), entity.getY(), entity.getZ());
	} */
}
