package apk.main.engine;

import java.util.ArrayList;
import java.util.List;

public class ID
{
	private static List<Integer> m_id = new ArrayList<Integer>();
	private static int m_lastID = 0;
	
	/** Adds to the ID list and returns the ID that was added.
	 * 
	 * @return ID added to list.
	 */
	protected static int add()
	{
		int id = getNext();
		m_id.add(id);
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
			throw new IDConflictException();
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
			m_id.remove(id);
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
		//temporary faster id fetch doesn't recycle any IDs
		int i = m_lastID;
		m_lastID++;
		
		return i;
		/*int i = 0;
		//while(m_idList.contains(i))
		while(m_id.contains(i))
			i++;
		return i;*/
	}
}

/** Thrown when there are conflicting IDs.
 * Used to prevent entities and actors from being created
 * if they try to use an ID that's already taken.
 */
class IDConflictException extends Exception
{
	private static final long serialVersionUID = 1L;

      public IDConflictException() {}

      public IDConflictException(String message)
      {
         super(message);
      }
 }

