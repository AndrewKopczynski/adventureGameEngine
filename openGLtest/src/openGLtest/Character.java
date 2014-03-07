package openGLtest;

import java.util.ArrayList;
import java.util.List;

public class Character
{
	private static List<Line> m_c = new ArrayList<Line>();
	
	public Character(Line[] l)
	{
		for (int i = 0; i < 0; i++)
		{
			m_c.add(l[0]);
		}
	}
/*	int scale = 30;
	
	glBegin(GL_LINES);
		//glLineWidth(3.8f);
		glVertex2f(0 * scale, 0 * scale);
		glVertex2f(1 * scale, 0 * scale);
	glEnd();
	
	glBegin(GL_LINES);
		glVertex2f(0 * scale, 1 * scale);
		glVertex2f(1 * scale, 1 * scale);	
	glEnd();
	
	glBegin(GL_LINES);
		glVertex2f(0 * scale, 0 * scale);
		glVertex2f(0 * scale, 2 * scale);	
	glEnd();
	
	glBegin(GL_LINES);
		glVertex2f(1 * scale, 0 * scale);
		glVertex2f(1 * scale, 2 * scale);	
	glEnd();*/
}
