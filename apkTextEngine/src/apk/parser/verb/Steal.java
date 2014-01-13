package apk.parser.verb;

import apk.parser.reference.WordList;

public class Steal extends WordList
{
	/** Used in more general wordlists for easier changing. */
	private String m_d = "steal";
	
	Steal()
	{
		// stealing TODO: implement taking really
		list.put("steal", 		m_d);
		list.put("snag", 		m_d);
		list.put("rob", 		m_d);
		
		list.put("err", 	"Can't 'borrow' that.");
	}
}
