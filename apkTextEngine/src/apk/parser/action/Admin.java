package apk.parser.action;

import apk.parser.reference.WordList;

public class Admin extends WordList
{
	public Admin()
	{	
		list.put("@setVis", 	"setVis");
		
		list.put("@add",		"add");
		list.put("@give",		"add");
		
		list.put("@del",		"del");
		list.put("@delete",     "del");
		list.put("@remove",		"del");
		
		list.put("@noclip",		"noclip");
		
		list.put("@hurt",		"hurt");
		list.put("@damage", 	"hurt");
		
		list.put("@heal",  		"heal");
		
		list.put("err",			"Probably not a valid admin command,"
				+ " or you lack adimistrative permissions.");
	}
}
