package parser.verb;

import parser.reference.WordList;

public class Look extends WordList
{
	/** Used in more general wordlists for easier changing. */
	private String m_d = "look";
	
	public Look()
	{
		list.put("look",		m_d);
		list.put("l",			m_d);
		
		list.put("err", "Usage: 'look item'");
	}


}
