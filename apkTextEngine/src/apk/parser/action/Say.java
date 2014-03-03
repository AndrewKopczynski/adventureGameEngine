package apk.parser.action;

import apk.reference.WordList;

public class Say extends WordList
{	
	public Say()
	{
		// saying stuff
		list.put("say",			"say");
		list.put("\"", 			"say");
		
		// not sure why i would ever have an error saying stuff but sure
		// TODO: secret achievement for breaking say?
		list.put("err", 		"Say what?");
	}
}
