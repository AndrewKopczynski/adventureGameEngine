package parser.verb;

import parser.reference.WordList;

public class Quit extends WordList
{
	/** Used in more general wordlists for easier changing. */
	private String m_d = "quit";
	
	public Quit()
	{
		//input string			//meaning
		list.put("quit",		m_d);
		
		list.put("err",	"It's just 'quit'. No need to get fancy.");	
	}
}
