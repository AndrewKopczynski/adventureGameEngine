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
	private List<Item> m_inv = new ArrayList<Item>();
	private String m_identifier;
	private String m_name;
	private String m_invPath;

	
	/** Creates an inventory (typically) for a room.
	 * 
	 * @param identifier Map Name + Coordinates
	 * @param name Name of the room
	 * @param filePath Path where the entity should be saved
	 */
	public Inventory(String identifier, String name, String filePath)
	{
		m_identifier = identifier;
		m_name = name;
		m_invPath = filePath;
		load();
	}
	
	/** Creates an inventory (typically) for an entity.
	 * 
	 * @param id ID of entity
	 * @param name Name of entity
	 * @param filePath Path where the entity should be saved
	 */
	public Inventory(int id, String name, String filePath)
	{
		m_identifier = "" + id;
		m_name = name;
		m_invPath = filePath;
		load();
	}
	
	/** Saves inventory (under xxx/invID_name). */
	public void save() 
	{
		/** Clear save file in preparation for writing to it. */
		try
		{
	        BufferedWriter out = new BufferedWriter(new FileWriter(m_invPath + ".txt"));
	        Logger.log("Saving inventory for "+ m_identifier + ", " + m_name);
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
            Logger.log("Saved " + m_identifier + ", " + m_name + "'s inventory.");
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
				String textDump = scan.nextLine();
				m_inv.add(new Item(textDump));
				Logger.log(textDump + " was loaded from " + m_identifier + ", " + m_name + "'s inventory.");
			}
			scan.close();
			Logger.log("Loaded " + m_identifier + ", " + m_name + "'s inventory.");
		} catch (FileNotFoundException e)
		{
			Logger.log("Inventory file " + m_invPath + " couldn't be found."
					+ " Creating new inventory for room...");
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
	
	/** Adds something to inventory.
	 * TODO: Change to adding items instead of strings. 
	 */
	public void add(Item item)
	{
		m_inv.add(item);
		Logger.log("Added item '" +  item + "' to " + m_identifier + ", " + m_name + "'s inventory.");
	}
	
	public void remove(String item)
	{
		for (int i = 0; i < m_inv.size(); i++)
		{
			if (item.equals(m_inv.get(i).toString()))
			{
				m_inv.remove(i);
				Logger.log("Removed item '" +  item + "' from " + m_identifier + ", " + m_name + "'s inventory.");
			}
		}
	}
}
