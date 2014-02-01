package apk.parser.action;

import apk.parser.reference.WordList;

public class Say extends WordList
{	
	public Say()
	{
		// saying stuff
		list.put("say",			"say");
		list.put("\"", 			"say");
		
		// not sure why i would ever have an error saying stuff but sure
		list.put("err", 		"Something went horribly wrong with 'say'!");
	}
}
