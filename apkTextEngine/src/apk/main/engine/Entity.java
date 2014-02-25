package apk.main.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import static apk.main.engine.Logger.printDebug;
import apk.parser.reference.EntityIntializationException;
import apk.parser.reference.IDConflictException;

/** Represents an entity.
 * <p>
 * An entity has:
 * <ul>
 * <li>A generated unique ID
 * <li>Maximum hit points
 * <li>Current hit points
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
	private static Map<Integer, Entity> m_ents = new HashMap<Integer, Entity>();
	private static final int DEFAULT_VISIBILITY = 3; //how far an actor/entity can see
	
	protected List<Entity> m_inv = new ArrayList<Entity>();
	protected int m_id;
	protected String m_name;
	protected int m_hpMax;
	protected int m_hp;
	protected boolean m_ignoresCollision = false;
	
	private String m_parent;
	protected int m_type;
	
	protected int m_visionRange = DEFAULT_VISIBILITY;
	
	//TODO does this even make sense lol
	//TODO is this needed?
	/* changed to Entity for now, because having Actor extend
	 * entity but also have Entity have a parent of a class that
	 * extends itself was super nuts */
	
	public Entity() {}
	
	// entities can only be created in inventories of actors
	public Entity(String name, int type, int hpMax, int hp, Actor parent) throws EntityIntializationException //actor
	{		
		printDebug("CREATING ENTITY WITH ACTOR PARENT");
		
		m_id = ID.add();
		m_ents.put(m_id, this);
		
		m_name = name;
		m_type = type;
		
		m_hpMax = hpMax;
		m_hp = hp;
		
		if (parent != null)
		{
			m_parent = parent.toString();
		/*	
			System.out.println(m_parent.getName());
			System.out.println(m_parent.getId());
			System.out.println(m_parent.toString());*/
		}
		else
			throw new EntityIntializationException();
		
		
		Logger.log("Created new ent '" + getName() +  "' @ " + m_parent + "'s inventory.");
		printDebug("DONE!");
	}
	
	// entities can only be created in inventories of actors
	public Entity(String name, int type, int hpMax, int hp, Entity parent) //ent
	{
		printDebug("CREATING ENTITY WITH ENTITY PARENT");
		
		m_id = ID.add();
		m_ents.put(m_id, this);
		
		m_name = name;
		m_type = type;
		
		m_hpMax = hpMax;
		m_hp = hp;
		
		m_parent = parent.toString();
		
		Logger.log("Created new entity '" + getName() + "' @ " + m_parent + "'s inventory.");
		
		printDebug("DONE!");
	}
	
	/** Creates a new entity from a file.
	 * <p>
	 * Usually used in loading entities from a save,
	 * but can also be used for template entities.
	 * 
	 * @param filePath exampleEntity[id]
	 * @throws IDConflictException 
	 * @throws DocumentException 
	 */
	public Entity(String filePath) throws FileNotFoundException, IDConflictException, DocumentException
	{	
		printDebug("LOADING ENTITY FROM FILE");
		
		try
		{
			URL entURL = new File(filePath).toURI().toURL();
			
			int id;
			int type;
			int hpMax = -1;
			int hp = -1;
			
			String name;
			String parent;
			//String[] split;
			
			Document actorD = XMLReader.parse(entURL);
			Element root = actorD.getRootElement();
			
			id = Integer.parseInt(root.attributeValue("id"));
			type = Integer.parseInt(root.attributeValue("type"));
			
			name = root.attributeValue("name");
			parent = root.attributeValue("inInventoryOf");
			
			for (Iterator<Element> i = root.elementIterator("health"); i.hasNext();)
			{
				Element element = (Element) i.next();
				
				hpMax = Integer.parseInt(element.attributeValue("hpMax"));
				hp = Integer.parseInt(element.attributeValue("hp"));
			}
			
			for (Iterator<Element> i = root.elementIterator("inventory"); i.hasNext();)
			{
				Element element = (Element) i.next();
				for (Iterator<Element> j = element.elementIterator(); j.hasNext();)
				{
					Element children = (Element) j.next();
					System.out.println(children.getText()); //TODO add stuff to inventory
				}
			}
			
			m_id = id;
			m_parent = parent;
			ID.add(m_id);
			
			m_type = type;
			m_name = name;
			m_hpMax = hpMax;
			m_hp = hp;
			
			/*split = coords.split(",");
			m_x = Integer.parseInt(split[0]);
			m_y = Integer.parseInt(split[1]);
			m_z = Integer.parseInt(split[2]);*/
			
			/* put into list of actors */
			m_ents.put(m_id, this);
			
			printDebug("DONE!");
		}
		catch (MalformedURLException e)
		{
			printDebug("FAILED TO LOAD ACTOR FROM FILE");
			e.printStackTrace();
		}
		finally
		{
			printDebug("FINISHED LOADING ACTOR FROM FILE");
		}
		
		
		/*try
		{
			String[] entElements = {"entity", "health", "inInventoryOf"};
			XMLReader invXML = new XMLReader(filePath, entElements);
			
			m_id = Integer.parseInt(invXML.getAttribute("entity", 0, "id"));
			ID.add(m_id);
			
			m_ents.put(m_id, this);
			
			m_name = invXML.getAttribute("entity", 0, "name");
			m_type = Integer.parseInt(invXML.getAttribute("entity", 0, "type"));
			
			//inv
			String t2 = invXML.getAttribute("entity", 0, "inInventoryOf");
			System.out.println("WS: " + t2);
			System.out.println("WS: " + Actor.stripID(t2));
			System.out.println("WS: " + Actor.getActorsByName(t2));
			
			//m_parent = Actor.getByName(invXML.getAttribute("entity", 0, "inInventoryOf"));
			m_parent = invXML.getAttribute("entity", 0, "inInventoryOf");
			
			Logger.log("Adding '" + m_name + "' to list with ID '" + m_id + "'...");
			
			m_hpMax = Integer.parseInt(invXML.getAttribute("health", 0, "hpMax"));
			m_hp = Integer.parseInt(invXML.getAttribute("health", 0, "hp"));
			
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
		catch (IDConflictException e)
		{
			kill();
		}*/
		printDebug("DONE!");
	}
	
	/** Write to save file. */
	public void writeSave() //TODO merge actor and entity saving~
	{
		Logger.start();
		
		//create file (eg. ent/player[0].xml)
		XMLWriter w = new XMLWriter(getFilePath());
		
		//fetch id, name, and coords
		String[] a = {"id", "name", "type", "inInventoryOf"};
		String[] b = {"" + getId(), getName(), "" + getType(), m_parent.toString()};
		
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
		
		System.out.print("[ENTITY] Save write time: ");
		Logger.stop(true);
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
	
	public int getType()
	{
		return m_type;
	}
	
	public int getVisionRange()
	{
		return m_visionRange;
	}
	
	public void setVisionRange(int visionRange)
	{
		m_visionRange = visionRange;
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

	
	public String[] getInventory()
	{
		int header = 1;
		int footer = 1;
		
		String[] msg = new String[header + m_inv.size() + footer];
		
		if (m_inv.size() == 0)
		{
			msg[0] = "You're not carrying anything.";
		}
		else
		{
			msg[0] = "---- Inventory ----";
			for (int i = 0; i < m_inv.size(); i++)
			{
				msg[i+header] = m_inv.get(i).toString();
			}
		}
		msg[msg.length - 1] =  "-------------------";
		return msg;
	}
	
	public boolean addToInventory(Entity entity)
	{
		System.out.println("tried to add " 
				+ entity.toString() 
				+ " to " 
				//+ ((Entity) m_parent).toString()
				+ "'s inventory.");
		if (m_inv.add(entity))
		{
			System.out.println("did!");
			return true;
		}
		else
		{
			System.out.println("failed!");
			return false;
		}
	}
	
	public boolean hasInInventory(Entity entity)
	{
		return m_inv.contains(entity);
	}
	public boolean hasInInventory(String string)
	{
		for (int i = 0; i < m_inv.size(); i++)
		{
			if (m_inv.get(i).getName().equalsIgnoreCase(string))
				return true;
		}
		return false;
	}
	
	public String dropFromInventory(int x, int y, int z, Entity entity)
	{
		
		if (delFromInventory(entity))
		{
			String t = entity.getName();
			
			m_ents.remove(entity);
			new Actor(x, y, z, entity);
			
			return "Dropped '" + t + "'.";
			//return "%DROP_DEFAULT";
		}
		else
			return "%DROP_ERR";
	}
	public String dropFromInventory(int x, int y, int z, String name)
	{
		for (int i = 0; i < m_inv.size(); i++)
		{
			Entity[] list = getByName(name); //extra step!
			//whoops forgot to remove it from the game also
			
			if (list.length > 0
				&& hasInInventory(list[i])
				&& delFromInventory(list[i]))
			{
				String t = list[i].getName();
				
				new Actor(x, y, z, list[i]);
				list[i].kill();
				
				return "Dropped '" + t + "'.";
			}
			
			
			/*if (entity != null && delFromInventory(entity))
			{
				//m_entites.remove(entity);
				new Actor(x, y, z, entity);
				
				entity.kill();
				
				return "Dropped '" + entity.getName() + "'.";
				//return "%DROP_DEFAULT";
			}*/
		}
		return "%DROP_ERR";
	}
	
	public boolean delFromInventory(Entity entity)
	{
		boolean did = m_inv.remove(entity);
		if (m_parent != null)
		{
			System.out.println("removing from inventory of " + m_parent.toString() + " " + did);
		}
		else
		{
			System.out.println("parent was null");
		}
		
		return did;
	}
	public boolean delFromInventory(String name)
	{
		return m_inv.remove(getByName(name));
	}
	
	public static Entity getById(int id)
	{
		if (m_ents.containsKey(id))
			return m_ents.get(id);
		else
			return null;
	}
	
	public static boolean existsById(int id)
	{
		if (m_ents.containsKey(id))
			return true;
		else
			return false;
	}
	
	public static Entity[] getByName(String name)
	{
		Entity[] list = new Entity[0];
		
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_ents.get(ID.get(i)) != null 
				&& m_ents.get(ID.get(i)).getName().equalsIgnoreCase(name))
			{
				System.out.println("found '" + name + "' !");
				Entity[] temp = new Entity[list.length + 1];
				System.arraycopy(list, 0, temp, 0, list.length);
				temp[temp.length - 1] = m_ents.get(ID.get(i));
				
				list = temp;
			}
		}
		return list;
	}
	
	public static boolean existsByName(String name)
	{
		for (int i = 0; i < m_ents.size(); i++)
		{
			if (m_ents.get(i).toString().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	/** debug list print */
	public static String[] debug()
	{
		String[] list = new String[m_ents.size()];
		int a = 0;
		
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_ents.get(ID.get(i)) != null)
			{
				list[a] = "[ENTITY]" + m_ents.get(ID.get(i)).toString();
				a++;
			}
			//else
				//System.out.println(m_entites.get(i));
		}
		return list;
	}
	
	public static void saveAll()
	{
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_ents.get(ID.get(i)) != null)
			{
				m_ents.get(ID.get(i)).writeSave();
			}
		}
	}
	
	/** Removes all references to this entity so that
	 * java's garbage collector will clean it up whenever. */
	public void kill()
	{
		try //TODO: make the entity drop all its stuff too
		{
			File file = new File("ent/" + toString() + ".xml");
			
			if (file.delete())
				Logger.log("Deleted: " + getFilePath());
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
		m_ents.remove(m_id); //no references, should clean up
		m_name = null;
		m_inv = null;
		try
		{
			this.finalize();
		} catch (Throwable e)
		{
			System.err.println("Couldn't clean up entity!");
			e.printStackTrace();
		}
	}
	
	public String toString()
	{
		return m_name + "[" + m_id + "]";
	}
}
