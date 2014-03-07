package openGLtest;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

public class Square extends Shape
{
	private Text tx;
	
	public Square(String name, float x, float y, int width, int height)
	{
		super(name, x, y, width, height);
		tx = new Text(name, x, y, 0, 0);
	}
	
	public Square(String name, float x, float y, int width, int height, float rot)
	{
		super(name, x, y, width, height, rot);
		tx = new Text(name, x, y, 0, 0, rot);
	}
	
	public void render()
	{	
		glPushMatrix();
			glTranslatef(getX(), getY(), 0);
			glRotatef(getRot(), 0f, 0f, 1f);
			glTranslatef(-getX(), -getY(), 0);

			glDisable(GL_TEXTURE_2D);
			glColor3f(getR(),getG(),getB());
			
			glBegin(GL_QUADS);
				glVertex2f(getX() - getWidth()/2, getY() - getHeight()/2);
				glVertex2f(getX() + getWidth()/2, getY() - getHeight()/2);
				glVertex2f(getX() + getWidth()/2, getY() + getHeight()/2);
				glVertex2f(getX() - getWidth()/2, getY() + getHeight()/2);
			glEnd();
			//Game.getFont().drawString(getX() - getWidth()/2, getY() - Game.getFont().getHeight() + getHeight()/2, getName(), Color.red);
			
		glPopMatrix();
		
		tx.setX(getX());
		tx.setY(-getY());
		tx.setStr(getX()+ ", " + -getY());
		tx.Render();
	}
}
