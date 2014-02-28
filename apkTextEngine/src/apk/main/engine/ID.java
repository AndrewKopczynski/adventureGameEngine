package apk.main.engine;

import java.util.ArrayList;
import java.util.List;
import apk.parser.reference.IDConflictException;

public class ID
{
	private static List<Integer> m_id = new ArrayList<Integer>();
	private static List<Integer> m_recycle = new ArrayList<Integer>();
	
	/** Adds to the ID list and returns the ID that was added.
	 * 
	 * @return ID added to list.
	 */
	protected static int add()
	{
		int id = getNext();
		m_id.add(id);
		Logger.logDebug("added ID: " + id);
		return id;
	}
	
	/** Adds a specific ID to the list.
	 * Example usage is in loading.
	 * 
	 * @param id ID to add
	 * @return True if added, false if not.
	 * @throws IDConflictException If ID is already taken, throws this.
	 */
	protected static boolean add(int id) throws IDConflictException
	{
		if (m_id.contains(id))
			throw new IDConflictException("Conflicting IDs! " + m_id);
		else
			return m_id.add(id);
	}
	
	protected static int size()
	{
		return m_id.size();
	}
	
	protected static int get(int i)
	{
		return m_id.get(i);
	}
	
	protected static boolean contains(int id)
	{
		return m_id.contains(id);
	}
	
	protected static boolean remove(int id)
	{
		if (m_id.contains(id))
		{
			m_id.remove(id);
			m_recycle.add(id);
		}
		else
			return false;
		return true;
	}
	
	protected static void debug()
	{
		for (int i = 0; i < m_id.size(); i++)
			System.out.println(m_id.get(i));
	}
	
	private static int getNext()
	{
		int i = 0;
		
		if (m_recycle.size() > 0) //'cache'
		{
			//if has, get first and remove it from recycle
			m_recycle.get(0);
			m_recycle.remove(0);
		}
		
		while(m_id.contains(i))
			i++;
		return i;
	}
}
