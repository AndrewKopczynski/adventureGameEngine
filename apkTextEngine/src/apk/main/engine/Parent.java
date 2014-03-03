package apk.main.engine;

public class Parent
{
	private final int m_id;
	
	public Parent(int id)
	{
		m_id = id;
	}
	
	public int getID()
	{
		return m_id;
	}
	
	public String toString()
	{
		return "PAR ID: " + m_id;
	}
}
