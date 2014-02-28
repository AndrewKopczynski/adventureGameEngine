package apk.main.engine;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import static apk.main.engine.Logger.logDebug;
import apk.parser.reference.EntityIntializationException;

public class Entity
{	
	private static final int DEFAULT_VISIBILITY = 3; //how far an actor/entity can see
	private static Map<Integer, Entity> m_ents = new HashMap<Integer, Entity>();
	protected List<Entity> m_inv = new ArrayList<Entity>();
	
	protected int m_id;
	protected String m_name;
	protected int m_hpMax;
	protected int m_hp;
	protected boolean m_ignoresCollision = false;
	
	private Actor m_parent; //todo: entity parents later
	protected int m_type;
	
	protected int m_visionRange = DEFAULT_VISIBILITY;
	
	public Entity() {}
	
	// entities can only be created in inventories of actors
	public Entity(String name, int type, int hpMax, int hp, Actor parent) throws EntityIntializationException //actor
	{		
		logDebug("-", "CREATING ENTITY WITH ACTOR PARENT");
		
		m_id = ID.add();
		
		m_name = name;
		m_type = type;
		
		m_hpMax = hpMax;
		m_hp = hp;
		
		setParent(parent);
		m_ents.put(m_id, this);
		
		System.out.println("CREATED ENTITY WITH FOLLOWING:");
		System.out.println("LIST: " + m_ents.get(m_id));
		System.out.println("ID  : " + m_id);
		System.out.println("PAR : " + m_parent);
		System.out.println("NAME: " + m_name);
		System.out.println("TYPE: " + m_type);
		System.out.println("MXHP: " + m_hpMax);
		System.out.println("HP  : " + m_hp);
		
		logDebug("-", "DONE CREATING ENTITY WITH ACTOR PARENT");
	}
	
	public Entity(String filePath, Actor parent) throws EntityIntializationException
	{	
		logDebug("-", "LOADING ENTITY FROM FILE");
		
		try
		{
			URL entURL = new File(filePath).toURI().toURL();
			
			int id;
			int type;
			int hpMax = -1;
			int hp = -1;
			
			String name;
			//String parent;
			//String[] split;
			
			Document actorD = XML.parse(entURL);
			Element root = actorD.getRootElement();
			
			id = Integer.parseInt(root.attributeValue("id"));
			type = Integer.parseInt(root.attributeValue("type"));
			
			name = root.attributeValue("name");
			//parent = root.attributeValue("inInventoryOf");
			
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
			
			/* print out everything before assignment*/
			System.out.println("LOADED THE FOLLOWING:");
			System.out.println("LIST: " + m_ents.get(m_id));
			System.out.println("ID  : " + id);
			System.out.println("PAR : " + parent);
			System.out.println("NAME: " + name);
			System.out.println("TYPE: " + type);
			System.out.println("MXHP: " + hpMax);
			System.out.println("HP  : " + hp);
			
			m_id = id;
			ID.add(m_id);
			
			//m_parent = Actor.getActorsByNameAndID(parent);
			setParent(parent);
			m_type = type;
			m_name = name;
			m_hpMax = hpMax;
			m_hp = hp;
			
			/* put into list of entities */
			m_ents.put(m_id, this);
			
			System.out.println("CREATED ENTITY WITH FOLLOWING:");
			System.out.println("LIST: " + m_ents.get(m_id));
			System.out.println("ID  : " + m_id);
			System.out.println("PAR : " + m_parent);
			System.out.println("NAME: " + m_name);
			System.out.println("TYPE: " + m_type);
			System.out.println("MXHP: " + m_hpMax);
			System.out.println("HP  : " + m_hp);
		}
		catch (Exception e) //if for any reason an exception is thrown, DON'T load entity
		{
			throw new EntityIntializationException("Failed to load an entity! " + e);
		}
		finally
		{
			logDebug("-", "FINISHED LOADING ENTITY FROM FILE");
		}
	}
	
	/** Write to save file. */
	public void writeSave() //TODO merge actor and entity saving~
	{
		logDebug("-", "WRITING SAVE FOR ENTITY");
		
		logDebug("SAVED ENTITY WITH FOLLOWING:");
		logDebug("LIST: " + m_ents.get(m_id));
		logDebug("ID  : " + m_id);
		logDebug("PAR : " + m_parent);
		logDebug("NAME: " + m_name);
		logDebug("TYPE: " + m_type);
		logDebug("MXHP: " + m_hpMax);
		logDebug("HP  : " + m_hp);
		
		//create file (eg. ent/player[0].xml)
		XML w = new XML(getFilePath());
		
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
		
		logDebug("-", "FINISHED WRITING SAVE FOR ENTITY");
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
	
	public Actor getParent()
	{
		return m_parent;
	}
	public void setParent(Actor parent) throws EntityIntializationException
	{
		if (parent != null)
			m_parent = parent;
		else
			throw new EntityIntializationException();
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
			String[] t = {"You're not carrying anything."};
			return t;
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
		if (m_inv.add(entity))
			return true;
		else
			return false;
	}
	
	public boolean hasInInv(Entity entity)
	{
		if (m_inv.contains(entity))
		{
			System.out.println(toString() + " has " + entity.toString() + "!");
			return true;
		}
		return false;
	}
	
	public String dropFromInv(Entity entity)
	{
		
		if (m_inv.remove(entity))
		{
			System.out.println(entity);
			String t = entity.getName();
			
			m_ents.remove(entity);
			new Actor(
					m_parent.getX(),
					m_parent.getY(),
					m_parent.getZ(),
					entity);
			
			return "Dropped '" + t + "'.";
			//return "%DROP_DEFAULT";
		}
		else
			return "%DROP_ERR";
	}
	public String dropFromInv(String name)
	{
		Entity[] list = getByName(name); //extra step!
		
		if (list.length > 0)
		{
			return dropFromInv(list[0]);
		}
		return "%DROP_ERR";
		
		/*for (int i = 0; i < m_inv.size(); i++)
		{
			//whoops forgot to remove it from the game also
			
			if (list.length > 0
				&& hasInInv(list[i])
				&& m_inv.remove(list[i]))
			{
				new Actor(
						m_parent.getX(),
						m_parent.getY(),
						m_parent.getZ(),
						list[i]);
				return "Dropped '" + name + "'.";
			}
		}
		return "%DROP_ERR";*/
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
				list[a] = "[ENTITY]" + m_ents.get(ID.get(i)).toString()
					+ " in inventory of " + m_ents.get(ID.get(i)).getParent().getName();
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
		
		ID.remove(m_id);
		m_ents.remove(m_id); //no references, should clean up
		m_name = null;
		m_parent = null;
		m_inv = null;
		
		try
		{
			this.finalize();
		}
		catch (Throwable e)
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
