package apk.parser.action;

import apk.parser.reference.WordList;

public class Put extends WordList
{
	/** Used in more general wordlists for easier changing. */
	private String m_d = "put";

	public Put()
	{
		list.put("put", 		m_d);
		list.put("store", 		m_d);
		
		list.put("%MOD", 		"in");
		
		list.put("err", 	"%ACTION %TARGET where?.");
	}
}
