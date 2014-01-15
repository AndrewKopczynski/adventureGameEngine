package apk.main.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/** There are 3 kinds of inventories:
 * <ul>
 * <li>Room inventories
 * <li>Entity inventories
 * <li>Item inventories
 * </ul>
 * <p>
 * Inventories are a list of items (which also have inventories).
 * <ul>
 * <li>When a room is created, it gets an inventory that may hold items and entities.
 * <li>When an entity is created, it gets an invetory that may hold items.
 * <li>When an item is created, it gets an inventory that may hold items.
 * Items can be physical things or abstract things like 'health' or things like
 * limbs and stuff.
 * 
 * @author Andrew Kopczynski
 * @version 1.0
 */
public class Inventory 
{
	private List<Entity> m_inv = new ArrayList<Entity>();
	private String m_identifier;
	private String m_name;
	private String m_invPath;

	
	/** Creates an inventory (typically) for a room.
	 * 
	 * @param identifier Map Name + Coordinates
	 * @param name Name of the room
	 * @param filePath Path where the entity should be saved
	 */
	public Inventory(Room room)
	{
		m_identifier = "room";
		m_name = room.toString();
		m_invPath = room.getFilePath();
		load();
	}
	
	/** Creates an inventory (typically) for an entity.
	 * 
	 * @param id ID of entity
	 * @param name Name of entity
	 * @param filePath Path where the entity should be saved
	 */
	public Inventory(Entity entity)
	{
		m_identifier = "entity";
		m_name = entity.toString();
		m_invPath = entity.getFilePath();
		load();
	}
	
	/** Saves inventory (under xxx/invID_name). */
	public void save() 
	{
		/** Clear save file in preparation for writing to it. */
		try
		{
	        BufferedWriter out = new BufferedWriter(new FileWriter(m_invPath + ".txt"));
	        Logger.log("Saving inventory for " + toString());
	        out.close();
		} catch (IOException e)
		{
	        System.out.println("!CRITICAL! Couldn't clear the old save before writing new save!");
	    }
		
		/** Write to save file. */
		try
		{
	        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(m_invPath + ".txt", true)));
            for (int i = 0; i < m_inv.size(); i++)
            {
                out.println(m_inv.get(i));
            }
            out.close();
            Logger.log("Saved " + toString() + "'s inventory.");
	    } catch (IOException e)
		{
	        Logger.log("Save failed in " + m_invPath + ", " + e);
	    }
	}
	
	/** Loads inventory (under ent/invID_name). 
	 * REMEMBER TO CLEAR iInv BEFORE LOADING AGAIN! */
	public void load()
	{
		File file = new File(m_invPath + ".txt");
		m_inv.clear();
		try
		{
			Scanner scan = new Scanner(file);
			
			while (scan.hasNextLine())
			{
				String temp = scan.nextLine();
				System.out.println(temp.substring(0, temp.length() - 3));
				m_inv.add(new Entity(temp.substring(0, temp.length() - 3), 1));
				Logger.log(temp + " was loaded from " + toString() + "'s inventory.");
			}
			scan.close();
			Logger.log("Loaded " + toString() + "'s inventory.");
		}
		catch (FileNotFoundException e)
		{
			Logger.log("Inventory file " + m_invPath + " couldn't be found."
					+ " Creating new inventory for " + toString() + "...");
			save();
		}
	}
	
	/** Prints out the contents of inventory. */
	public void print()
	{
		for (int i = 0; i < m_inv.size(); i++)
		{
			System.out.println(m_inv.get(i));
		} 
	}
	
	/** Adds something to inventory. */
	public void add(Entity entity)
	{
		m_inv.add(entity);
		Logger.log("Added item '" + entity + "' to " + toString());
	}
	
	public void remove(String item)
	{
		for (int i = 0; i < m_inv.size(); i++)
		{
			if (item.equals(m_inv.get(i).toString()))
			{
				m_inv.remove(i);
				Logger.log("Removed item '" +  item + "' from " + toString());
			}
		}
	}
	public String toString()
	{
		return m_identifier + " " + m_name;
	}
}
