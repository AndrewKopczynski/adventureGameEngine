package apk.main.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Represents an entity.
 * <p>
 * An entity has:
 * <ul>
 * <li>A generated unique ID
 * <li>Maximum hitpoints
 * <li>Current hitpoints
 * <li>An inventory (list of entities it contains)
 * </ul>
 * 
 * <p>
 * 
 * NOTE:
 * <br>
 * Entities just exist and chill. They can't move around,
 * or be moved around. 
 * <br>
 * WorldEntity extends Entity, and lets Entities
 * move around and junk.
 * 
 * <p>
 * 
 * Use:
 * <ul>
 * <li>Entity for items in inventories and things not in the game world
 * <li>WorldEntity for players, NPCs, and generally anything that can
 * move or be moved.
 * </ul>
 * */
public class Entity
{	
	/** Grand list of IDs */
	//testing hashmap instead of list
	//protected static List<Integer> m_idList = new ArrayList<Integer>();
	
	/** Inventory of entity. */
	protected List<Entity> m_inv = new ArrayList<Entity>();
	
	protected static Map<Integer, Entity> m_idList = new HashMap<Integer, Entity>();
	
	/** Unique ID */
	protected int m_id;
	
	/** Name of entity. */
	protected String m_name;
	/** Maximum health of entity. */
	protected int m_hpMax;
	/** Current health of entity. */
	protected int m_hp;
	
	/** Ignores collision checks, may go to any tile within array range.
	 * <p>
	 * <ul>
	 * <li>False: Normal checks (cannot go through walls, must have appropriate exits)
	 * <li>True: 'Noclip', ignores collision checks, can 'fly'/pass through walls.
	 * </ul>
	 */
	protected boolean m_ignoresCollision = false;
	
	public Entity()
	{
		/*m_id = getNextId();
		addId(m_id);*/
	}
	
	public Entity(String name, int hpMax)
	{
		m_id = getNextId();
		//addId(m_id);
		addId(m_id, this);
		
		m_name = name;
		m_hpMax = hpMax;
		m_hp = hpMax;
		
		Logger.log("Created new ent '" + toString() +  "' @ " + "inventory?");
		
		writeSave();
	}
	
	public Entity(String name, int hpMax, int hp)
	{
		m_id = getNextId();
		//addId(m_id);
		addId(m_id, this);
		
		m_name = name;
		m_hpMax = hpMax;
		m_hp = hp;
		
		Logger.log("Created new ent '" + toString() + "'.");
		
		writeSave();
	}
	
	/** Creates a new entity from a file.
	 * <p>
	 * Usually used in loading entities from a save,
	 * but can also be used for template entities.
	 * 
	 * @param filePath exampleEntity[id]
	 */
	public Entity(String filePath)
	{	
		String[] entElements = {"entity", "health", "inventory"};
		XMLReader invXML = new XMLReader(filePath, entElements);
		
		m_id = Integer.parseInt(invXML.getAttribute(entElements[0], 0, "id"));
		m_name = invXML.getAttribute("entity", 0, "name");
		
		Logger.log("Adding '" + m_name + "' to list with ID '" + m_id + "'...");
		//m_idList.add(m_id);
		m_idList.put(m_id, this);
		
		m_hpMax = Integer.parseInt(invXML.getAttribute("health", 0, "hpMax"));
		m_hp = Integer.parseInt(invXML.getAttribute("health", 0, "hp"));
		
		//System.out.println("printing inventory...");
		String ents[] = invXML.getChildren("inventory", 0);
		for (int a = 1; a < ents.length; a += 2)
		{
			//System.out.println("found ent: " + ents[a]);
			if (!ents[a].equals(toString()))
			{
				addToInventory(new Entity("ent/" + ents[a] + ".xml"));
			}
			else
			{
				String err = "!CRTICAL! ENTITY " + toString() + "TRIED TO LOAD ITSELF!"
						+ " CHECK ENTITY FILE " + getFilePath() + "!";
				System.out.println(err);
				Logger.log(err);
			}
		}
		writeSave();
	}
	
	/** Write to save file. */
	private void writeSave()
	{
		//create file (eg. ent/player[0].xml)
		XMLWriter w = new XMLWriter(getFilePath());
		
		//fetch id, name, and coords
		String[] a = {"id", "name", "coords"};
		String[] b = {"" + getId(), getName(), "inInventory"};
		
		//open root
		w.writeOpenTag("entity", a, b);
		
		//fetch hp
		String[] c = {"hpMax", "hp"};
		String[] d = {"" + getHPMax(), "" + getHP()};
		
		//write health
		w.writeTag("health", c, d);
		
		//open inventory, write items
		w.writeOpenTag("inventory");
		for (int i = 0; i < m_inv.size(); i++)
		{
			w.writeTextContent("entity", m_inv.get(i).toString());
		}
		//close inventory
		w.writeCloseTag("inventory");
		
		//close root
		w.writeCloseTag("entity");
		
		//close file writer
		w.close();
		Logger.log("Wrote " + toString() +"'s inventory to " + getFilePath());
	}
	
	public int getId()
	{
		return m_id;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public String getFilePath() 
	{
		return "ent/" + toString();
	}
	
	public void ignoresCollision(boolean ignores)
	{
		m_ignoresCollision = ignores;
	}
	
	public int getHPMax()
	{
		return m_hpMax;
	}
	public int getHP()
	{
		return m_hp;
	}
	/** Damage this entity.
	 * <p>
	 * TODO: flags
	 * <br>
	 * If this entity does not have the admin_GODMODE flag,
	 * and the damage brings the entities hp to or below
	 * 0, then it will check isAlive().
	 * 
	 * @param amount Amount to damage the entity by.
	 */
	public String hurt(int amount)
	{
		if (amount < 0)
		{
			//reroute to heal if negative
			return heal(amount * -1);
		}
		
		int temp = m_hp;
		
		if (m_hp - amount <= 0)
		{
			die();
			return printMeter(temp, 0, m_hpMax, 30) + " ow.";
		}
		else
		{
			m_hp = Math.min(m_hp - amount, m_hpMax);
			return printMeter(temp, m_hp, m_hpMax, 30) + " ow.";
		}
	}
	/** Heal this entity.
	 * <p>
	 * TODO: flags
	 * <br>
	 * <ul>Situations
	 * <li>overheal_PERM - will overheal and never lose extra HP
	 * <li>overheal_TEMP - will overheal and decay overhead overtime
	 * <li>none (default) - will not overheal
	 * </ul>
	 * 
	 * @param amount Amount to heal the entity by.
	 */
	public String heal(int amount)
	{	
		if (amount < 0) //reroute to damage if negative
		{
			return hurt(amount * -1);
		}
		
		if ("PLACEHOLDER_flag".equals("PLACEHOLDER_overheal_PERM"))
		{
			m_hp += amount;
			
			return printMeter(m_hp, m_hp + amount, m_hpMax, 30) + " Ahh.";
		}
		else if ("PLACEHOLDER_flag".equals("PLACEHOLDER_overheal_TEMP"))
		{
			m_hp += amount;
			
			return printMeter(m_hp, m_hp + amount, m_hpMax, 30) + " Ahh.";
		}
		else
		{
			int temp = m_hp;
			m_hp = Math.min(m_hp + amount, m_hpMax);
			
			return printMeter(temp, m_hp, m_hpMax, 30) + " Ahh.";
		}
	}
	
	private void die() //TODO: finish
	{
		m_hp = 0;
		
		String msg = "'" + toString() + "' died in inventory.";
		System.out.println(msg);
		Logger.log(msg);
	}
	
	/** Meters.
	 * <br>
	 * Examples:
	 * <br>
	 * [||||||||||] 50/50
	 * <br>
	 * [|| ] 20/40 
	 * 
	 * @param cur Current value (eg. hp)
	 * @param max Maximum value (eg. hpMax)
	 * @param length Length of meter in characters
	 * @return Meter
	 */
	public String printMeter(int pre, int post, int max, int length)
	{
		//TODO: does it matter enough to use doubles?
		int tickValue = max / length;
		
		String str = "[";
		
		for (int i = 0; i < max; i += tickValue)
		{
			if ((i < pre || i < post) && pre == post)
			{
				str += "|";
				//System.out.println(" tcknorm ");
			}
			else if (i < pre && pre < post)
			{
				str += "|";
				//System.out.println(" tckhurt ");
			}
			else if (i < post && post < pre)
			{
				str += "|";
				//System.out.println(" tckhheal ");
			}
			else if (i >= post - pre && i < pre)
			{
				str += "=";
				//System.out.println(" hurt ");
			}
			else if (i >= pre - post && i < post)
			{
				str += "*";
				//System.out.println(" heal ");
			}
			else
			{
				str += " ";
				//System.out.println(" spc ");
			}
		}
		str += "] " + post + "/" + max;
		
		return str;
	}
	
	/** [player****] 10/10 */
	public String printNamedHealthBar()
	{
		String str = "[";
		
		for (int i = 0; i < m_hpMax; i++)
		{
			if (i < m_name.length())
			{
				str += m_name.substring(i, i + 1);
			}
			else if (i < m_hp)
			{
				str += "*";
			}
			else
			{
				str += " ";
			}
		}
		
		str += "] " + m_hp + "/" + m_hpMax;
		
		return str;
	}

	public boolean addToInventory(Entity entity)
	{
		return m_inv.add(entity);
	}
	
	public boolean delFromInventory(Entity entity)
	{
		return m_inv.remove(entity);
	}
	
	public boolean delFromInventory(String entityName)
	{
		//System.out.println("doing entity removal from inventory by name for '" + entityName + "'");
		for (int i = 0; i < m_inv.size(); i++)
		{
			if (m_inv.get(i).getName().equals(entityName))
			{
				//System.out.println("found item!");
				m_inv.get(i).delEntity();
				m_inv.remove(i);
				return true;
			}	
			//System.out.println("checked " + m_inv.get(i).getName());
		}
		//System.out.println("removal failed");
		return false;
	}
	
	/** Adds an ID to the list of IDs
	 * 
	 * @param id ID to add
	 * @return true if added, false if not added.
	 */
	protected static boolean addId(int id, Entity entity)
	{
		System.out.println("ID: " + id);
		//if (!m_idList.contains(id))
		if (!m_idList.containsKey(id))
		{
			//return m_idList.add(id);
			m_idList.put(id, entity);
			return true;
		}
		else
		{
			String err = "!CRTICAL! DUPLICATE ID '" + id + "'.";
			System.out.println(err);
			Logger.log(err);
			
			throw new IllegalArgumentException("Method addId(id) failed due to duplicate ID.");
		}
	}
	/** Removes an ID from the ID list TODO: faster ID recycle
	 * 
	 * @param id ID to remove
	 * @return true if removed, false if not removed
	 */
	private static boolean delId(int id)
	{
		if (m_idList.containsKey(id))
		{
			m_idList.remove(id);
			return true;
		}
		/*for (int i = 0; i < m_idList.size() - 1; i++)
		{
			if (m_idList.get(i) == id)
			{
				m_idList.remove(i);
				return true;
			}	
		}*/
		return false;
	}
	/** Gets the next free ID
	 * TODO: make second array track free IDs instead of looping through list.
	 * 
	 * @return The lowest available ID
	 */
	private static int getNextId()
	{
		int i = 0;
		//while(m_idList.contains(i))
		while(m_idList.containsKey(i))
			i++;
		return i;
	}
	
	public static Entity getEntityById(int id)
	{
		if (m_idList.containsKey(id))
			return m_idList.get(id);
		else
			return null;
	}
	
	/** debug list print */
	public static void debug()
	{
		for (int i = 0; i < m_idList.size(); i++)
		{
			System.out.println(m_idList.get(i));
		}
	}
	
	/** Removes all references to this entity so that
	 * java's garbage collector will clean it up whenever. */
	protected void delEntity()
	{
		//TODO: make the entity drop all its stuff too
		try
		{
			File file = new File("ent/" + toString() + ".xml");
			
			if (file.delete())
			{
				Logger.log("Deleted: " + getFilePath());
			}
			else
			{
				System.out.println("!CRTICAL! ENTITY FILE NOT DELETED!");
				throw new IllegalArgumentException("Could not delete entity file " + getFilePath());
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		delId(m_id); //no references, should clean up
	}
	
	public String toString()
	{
		return m_name + "[" + m_id + "]";
	}
	
}
