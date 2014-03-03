package apk.parser.action;

import apk.reference.WordList;

public class Admin extends WordList
{
	public Admin()
	{	
		list.put("@setVis", 	"setVis"); //changes the size of the map recieved
		
		list.put("@add",		"add"); //creates an item in an actors inventory
		list.put("@give",		"add");
		
		list.put("@del",		"del"); //deletes an item from an actor
		list.put("@delete",     "del");
		list.put("@remove",		"del");
		
		list.put("@noclip",		"noclip"); //ignores collision checks for movement
		
		list.put("@hurt",		"hurt"); //hurts an actor/entity
		list.put("@damage", 	"hurt");
		
		list.put("@heal",  		"heal"); //heals an actor/entity
		
		list.put("@debug", 		"debug"); //prints all current entites and actors
		
		list.put("@test",		"test"); // temp, spawns 4 test NPCs
		list.put("@stressTest", "stress"); //temp, spawns 1000 test NPCs
		
		list.put("@saveAll", 	"saveAll"); //temp
		
		list.put("err",			"Probably not a valid admin command,"
				+ " or you lack adimistrative permissions.");
	}
}
