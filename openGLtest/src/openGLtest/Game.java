package openGLtest;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

public class Game
{	
	/** time at last frame */
	private long m_lastFrame;
	
	/** frames per second */
	private int m_fps;
	/** last fps time */
	private long m_lastFps;
	
	private static TrueTypeFont m_font;
	private Square sq = new Square("it's a box!", 100, 100, 50, 50);
	private Text tx = new Text("text1", 0, 600, 50, 50);

	public static TrueTypeFont getFont()
	{
		return m_font;
	}
	public void start()
	{
		try
		{
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.create();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
			System.exit(0);
		}

		initGL(); // init OpenGL
		getDelta(); // call once before loop to initialize lastFrame
		m_lastFps = getTime(); // call before loop to initialize fps timer
		
		//loading can go here i guess
		sq.setRGB(1.0f, 1.0f, 0.0f);
		
		TextureImpl.bindNone();
		Font awtFont = new Font("Lucida Console", Font.PLAIN, 14);
		m_font = new TrueTypeFont(awtFont, false);
		//

		while (!Display.isCloseRequested())
		{	
			int delta = getDelta();
			
			update(delta);
			renderGL(delta);

			Display.update();
			Display.sync(60); // cap fps to 60fps
		}

		Display.destroy();
	}
	
	public void update(int delta)
	{
		/*// rotate quad
		rotation += 0.10f * delta;*/
		
		/*if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) x -= 0.35f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) x += 0.35f * delta;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) y += 0.35f * delta;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) y -= 0.35f * delta;
		
		// keep quad on the screen
		if (x < 0) x = 0;
		if (x > 800) x = 800;
		if (y < 0) y = 0;
		if (y > 600) y = 600;*/
		
		updateFPS(); // update FPS Counter
	}
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta()
	{
	    long time = getTime();
	    int delta = (int) (time - m_lastFrame);
	    m_lastFrame = time;
	 
	    return delta;
	}
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime()
	{
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS()
	{
		if (getTime() - m_lastFps > 1000)
		{
			Display.setTitle("FPS: " + m_fps);
			m_fps = 0;
			m_lastFps += 1000;
		}
		m_fps++;
	}
	
	public void initGL()
	{
		glEnable(GL_TEXTURE_2D);
		glShadeModel(GL_SMOOTH);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		
		glClearColor(0.1f, 0.1f, 0.1f, 0.0f);
		glClearDepth(1);
		
		glViewport(0, 0, 800, 600);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glMatrixMode(GL_MODELVIEW);
		
		glOrtho(0, 800, 0, 600, 1, -1);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	public void renderGL(int delta)
	{
		// Clear The Screen And The Depth Buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// R,G,B,A Set The Color To Blue One Time Only
		//glColor3f(0.5f, 0.5f, 1.0f);

		// draw stuff
		//float rot = 0.35f * getDelta();
		
		//System.out.println(rot);
		glPushMatrix();
			glScalef(1.0f, -1.0f, 1.0f);
			
			tx.setStr(Mouse.getX() + ", " + Mouse.getY());
			tx.Render();
			
			sq.addRot(0.10f * delta);
			getFont().drawString(800, 600, "asd");
			sq.setX(Mouse.getX());
			sq.setY(Mouse.getY());
			sq.render();
		glPopMatrix();
	}
	
	public static void main(String[] args)
	{
		Game g = new Game();
		g.start();
	}
}