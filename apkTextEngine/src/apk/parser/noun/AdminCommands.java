package apk.parser.noun;

import apk.parser.reference.WordList;

public class AdminCommands extends WordList
{
	public AdminCommands()
	{
		list.put("setRange", 	"setRange");
		list.put("give",		"give");
		list.put("remove",		"delete");
		list.put("delete",      "delete");
		list.put("noclip",		"noclip");
		
		list.put("err",			"Probably not a valid admin command,"
				+ " or you lack adimistrative permissions.");
	}
}
