package apk.parser.verb;

import apk.parser.reference.WordList;

public class Admin extends WordList
{
	public Admin()
	{
		list.put("admin", 		"admin");
		list.put("err",			"Probably not a valid admin command,"
				+ " or you lack adimistrative permissions.");
	}
}
