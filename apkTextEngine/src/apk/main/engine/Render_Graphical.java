package apk.main.engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Render_Graphical extends JPanel
{
	/** TODO: probably scrap this and redo it later.
	 * Code is messy because I wanted to test how easy it would be
	 * to implement some sort of graphical map.
	 */
	private static final long serialVersionUID = -6928110710925627165L;
	/** Required. *//*
	private static final long serialVersionUID = 6419989246198401761L;
	
	private int m_x;
	private int m_y;
	
	private int m_tSize = 30;
	
	public void paintComponent(Graphics g)
	{
		m_x = 0;
		m_y = 0;
		
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g;
    	renderMap(g2d);
    	//	g2d.drawRect(m_x, m_y, m_tSize, m_tSize);
	}
	
	public void renderMap(Graphics2D g2d)
	{
		int z = Server.player.getZ();
		for (int y = 0; y < Map.mapSize; y++)
		{
			renderMapLine(g2d, y, z);
			m_y += m_tSize;
			m_x = 0;
		}
	}
	
	private void renderMapLine(Graphics2D g2d, int y, int z)
	{
		for (int x = 0; x < Map.mapSize; x++)
		{
			if (Map.isMapRoom(Map.roomArray, x, y, z))
			{
				if (x == Server.player.getX() && y == Server.player.getY())
				{
					// print player tile here, x++
					g2d.setColor(Color.red);
					g2d.fillRect(m_x, m_y, m_tSize, m_tSize);
					m_x += m_tSize;
				} 
				
				else
				{
					// print tile here, x++
					g2d.setColor(Color.black);
					g2d.fillRect(m_x, m_y, m_tSize, m_tSize);
					m_x += m_tSize;
				}
			} 
			
			else
			{
				// print blank tile here, x++
				g2d.setColor(Color.gray);
				g2d.drawRect(m_x, m_y, m_tSize, m_tSize);
				m_x += m_tSize;
			}
		}
	}*/
}
