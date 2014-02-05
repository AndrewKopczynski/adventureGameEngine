package apk.main.engine;

import java.util.ArrayList;
import java.util.List;

public class ID
{
	private static List<Integer> m_id = new ArrayList<Integer>();
	
	protected static boolean add(int id)
	{
		if (m_id.add(id))
			return true;
		else
			return false;
	}
	
	protected static boolean exists(int id)
	{
		return m_id.contains(id);
	}
	
	protected static boolean del(int id)
	{
		if (m_id.contains(id))
		{
			m_id.remove(id);
			return true;
		}
		else
			return false;
	}
	
	protected static void debug()
	{
		for (int i = 0; i < m_id.size(); i++)
			System.out.println(m_id.get(i));
	}
	
	protected static int getNext()
	{
		int i = 0;
		//while(m_idList.contains(i))
		while(m_id.contains(i))
			i++;
		return i;
	}
	
	
}

class IDConflictException extends Exception
{
      //Parameterless Constructor
      public IDConflictException() {}

      //Constructor that accepts a message
      public IDConflictException(String message)
      {
         super(message);
      }
 }

