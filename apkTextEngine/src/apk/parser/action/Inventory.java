package apk.parser.action;

import apk.parser.reference.WordList;

public class Inventory extends WordList
{
	private String m_d = "i";
	
	public Inventory()
	{
		list.put("i", 			m_d);
		list.put("inv", 		m_d);
		list.put("inventory", 	m_d);
		
		list.put("err", 	"[INVENTORY] I don't understand '%ACTION'.");
	}
}
