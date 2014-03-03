package apk.parser.action;

import apk.reference.WordList;

public class Take extends WordList
{
	/** Used in more general wordlists for easier changing. */
	private String m_d = "take";
	
	public Take()
	{
		// taking stuff
		// verb noun - get stuff locally, like on the ground
		// verb noun noun - get stuff out of containers and off people
		list.put("take", 		m_d);
		list.put("get", 		m_d);
		list.put("fetch", 		m_d);
		list.put("hustle", 		m_d);
		list.put("reap", 		m_d);
		list.put("attain", 		m_d);
		list.put("acquire", 	m_d);
		list.put("obtain", 		m_d);
		list.put("grab", 		m_d);
		
		list.put("%MOD",		"from"); //TODO: come up with a better name
		
		list.put("err", 		"%ACTION what?");
	}
}
