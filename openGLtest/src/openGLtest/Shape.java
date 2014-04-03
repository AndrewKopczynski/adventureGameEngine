package openGLtest;

public class Shape extends Color
{
	//private Origin m_origin;
	private float m_x;
	private float m_y;
	private int m_h;
	private int m_w;
	
	private float m_rot = 0.0f;
	private final String m_name;
	
	public Shape(String name, float x, float y, int width, int height)
	{
		m_name = name;
		m_x = x;
		m_y = y;
		m_w = width;
		m_h = height;
	}
	
	public Shape(String name, float x, float y, int width, int height, float rot)
	{
		m_name = name;
		m_x = x;
		m_y = y;
		m_w = width;
		m_h = height;
		m_rot = rot;
	}

	public String getName()
	{
		return m_name;
	}
	
	public float getX()
	{
		return m_x;
	}
	public void setX(float x)
	{
		m_x = x;
	}
	
	public float getY()
	{
		return -m_y;
	}
	public void setY(float y)
	{
		m_y = y;
	}
	
	public int getWidth()
	{
		return m_w;
	}
	public void setWidth(int width)
	{
		m_w = width;
	}
	
	public int getHeight()
	{
		return m_h;
	}
	public void setHeight(int height)
	{
		m_h = height;
	}
	
	public float getRot()
	{
		return m_rot;
	}
	public void setRot(float rot)
	{
		m_rot = rot;
	}
	public void addRot(float rot)
	{
		m_rot += rot;
	}
	
	public void setRGB(float red, float grn, float blu)
	{
		setR(red);
		setG(grn);
		setB(blu);
	}
}
