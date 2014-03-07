package openGLtest;

import java.awt.Point;

public class Line
{
	private Point m_a;
	private Point m_b;
	
	public Line(int x1, int y1, int x2, int y2)
	{
		m_a = new Point(x1, y1);
		m_b = new Point(x2, y2);
	}
	
	public Point getA()
	{
		return m_a;
	}
	public Point getB()
	{
		return m_b;
	}
}
