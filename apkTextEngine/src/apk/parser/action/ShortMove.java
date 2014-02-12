package apk.parser.action;

import apk.main.engine.Actor;
import apk.main.engine.Render_ASCII;
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
	
	/** SHORT MOVEMENT ------------------------------------------------	|
	 * Short: north
	 */
	public String[] doAction(Actor actor, String dir)
	{
		String[] msg;
		
		String temp = actor.move(list.get(dir));
		msg = Render_ASCII.renderMap(actor.getX(), actor.getY(), actor.getZ());
		
		String[] arr = new String[msg.length + 1];
		System.arraycopy(msg, 0, arr, 0, msg.length);
		
		msg = arr;
		msg[msg.length - 1] = temp;
		
		return msg;
	}
	
}
