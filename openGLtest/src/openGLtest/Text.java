package openGLtest;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

public class Text extends Shape
{
	private String m_str;
	
	public Text(String name, float x, float y, int width, int height)
	{
		super(name, x, y, width, height);
	}
	
	public Text(String name, float x, float y, int width, int height, float rot)
	{
		super(name, x, y, width, height, rot);
	}
	
	public void Render()
	{
		glEnable(GL_TEXTURE_2D);
		
		glPushMatrix();
			glTranslatef(getX(), getY(), 0);
			glRotatef(getRot(), 0f, 0f, 1f);
			glTranslatef(-getX(), -getY(), 0);
			
			
		
			TextureImpl.bindNone();
			Game.getFont().drawString(getX(), getY(), getName() + ":" +getStr(), Color.red);
		glPopMatrix();
	}
	
	public void setStr(String str)
	{
		m_str = str;
	}
	private String getStr()
	{
		return m_str;
	}
}
