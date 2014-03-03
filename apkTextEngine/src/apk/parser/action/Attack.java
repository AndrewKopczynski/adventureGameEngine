package apk.parser.action;

import apk.reference.WordList;

public class Attack extends WordList
{
	/** Used in more general wordlists for easier changing. */
	private String m_d = "drop";
	
	public Attack()
	{
		// combat starters, TODO: use eventually i guess
		list.put("fight", 		m_d);
		list.put("attack", 		m_d);
		list.put("kill", 		m_d);
		list.put("assault", 	m_d);
		list.put("charge", 		m_d);
		list.put("rush", 		m_d);
		list.put("strike", 		m_d);
		list.put("hit", 		m_d);
		list.put("combat", 		m_d);
		list.put("beat", 		m_d);
		list.put("punch", 		m_d);
		list.put("kick", 		m_d);
		list.put("clobber", 	m_d);
		list.put("pummel", 		m_d);
		
		list.put("err", 	"I can't fight that.");
	}
}
