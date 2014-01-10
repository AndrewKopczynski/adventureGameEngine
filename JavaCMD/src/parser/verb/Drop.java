package parser.verb;

import parser.reference.WordList;

public class Drop extends WordList
{
	/** Used in more general wordlists for easier changing. */
	private String m_d = "drop";
	
	public Drop()
	{
		list.put("drop", 		m_d);
		list.put("dump", 		m_d);
		list.put("abandon", 	m_d);
		
		list.put("err", 	"I can't drop that.");
	}
}
