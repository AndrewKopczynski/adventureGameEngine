package apk.parser.action;

import apk.reference.WordList;

public class Move extends WordList {

	/** Used in more general wordlists for easier changing. */
	private String m_d = "move";
	
	public Move()
	{
		// long movement (verb noun)
		list.put("move", 		m_d);
		list.put("go", 			m_d);
		list.put("walk", 		m_d);
		list.put("run", 		m_d);
		list.put("travel", 		m_d);
		list.put("traverse", 	m_d);
		list.put("proceed", 	m_d);
		list.put("jog", 		m_d);
		list.put("head",		m_d);

		list.put("err", 	"[MOVE] Go where?");
	}
}
