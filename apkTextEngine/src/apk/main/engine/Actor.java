package apk.main.engine;

import static apk.main.engine.Logger.logDebug;
import apk.reference.ActorIntializationException;
import apk.reference.EntityIntializationException;
import apk.reference.IDConflictException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;



/** Represents an entity in the world (a world entity).
 * <p>
 * A world entity has:
 * <ul>
 * <li>A ID (int, contained in list of int)
 * <li>A X coordinate (int)
 * <li>A Y coordinate (int)
 * <li>A Z coordinate (int)
 * <li>Maximum HP (int)
 * <li>Current HP (int)
 * <li>An inventory (list of entities)
 * </ul>
 * */
@SuppressWarnings("unused")
public class Actor extends Entity
{
	/**List of ID/Actors */
	private static Map<Integer, Actor> m_actors = new HashMap<Integer, Actor>();
	
	private int m_x;
	private int m_y;
	private int m_z;
	
	public Actor(int x, int y, int z, String name, int type, int hpMax, int hp)
	{
		System.out.println(
				//"-", 
				"CREATING NEW ACTOR");
		
		m_id = ID.add();
		m_actors.put(m_id, this);
		
		m_x = x;
		m_y = y;
		m_z = z;
		
		m_name = name;
		m_type = type;
		
		m_hpMax = hpMax;
		m_hp = hp;
		
		System.out.println("CREATED ACTOR WITH FOLLOWING:");
		System.out.println("LIST: " + m_actors.get(m_id));
		System.out.println("ID  : " + m_id);
		System.out.println("X   : " + m_x);
		System.out.println("Y   : " + m_y);
		System.out.println("Z   : " + m_z);
		System.out.println("NAME: " + m_name);
		System.out.println("TYPE: " + m_type);
		System.out.println("MXHP: " + m_hpMax);
		System.out.println("HP  : " + m_hp);
		
		System.out.println(
				//"-", 
				"DONE CREATING NEW ACTOR!");
	}
	
	public Actor(int x, int y, int z, Entity entity) throws IDConflictException
	{
		System.out.println(
				//"-", 
				"CONVERTING ENTITY TO ACTOR");
		
		//TODO can i just cast this stuff or no
		m_id = entity.getId();
		
		m_x = x;
		m_y = y;
		m_z = z;
		
		m_name = entity.getName();
		m_type = entity.getType();
		
		m_hpMax = entity.getHPMax();
		m_hp = entity.getHP();
		
		entity.kill();
		ID.add(m_id); //after entity is killed always remember to accolate ID, duh
		m_actors.put(m_id, this); //after kill() to make sure id is available
		
		System.out.println("CONVERTED ENTITY TO ACTOR WITH FOLLOWING:");
		System.out.println("LIST: " + m_actors.get(m_id));
		System.out.println("ID  : " + m_id);
		System.out.println("X   : " + m_x);
		System.out.println("Y   : " + m_y);
		System.out.println("Z   : " + m_z);
		System.out.println("NAME: " + m_name);
		System.out.println("TYPE: " + m_type);
		System.out.println("MXHP: " + m_hpMax);
		System.out.println("HP  : " + m_hp);
		
		System.out.println(
				//"-", 
				"DONE CONVERTING ENTITY TO ACTOR!");
	}
	
	public Actor(String filePath) throws IDConflictException, ActorIntializationException, FileNotFoundException
	{	
		System.out.println(
				//"-", 
				"LOADING ACTOR FROM FILE");
		
		try
		{
			URL actorURL = new File(filePath).toURI().toURL();
			
			int id;
			int type;
			int hpMax = -1;
			int hp = -1;
			
			String name;
			String coords;
			String[] split;
			
			Document actorD = XML.parse(actorURL);
			Element root = actorD.getRootElement();
			
			id = Integer.parseInt(root.attributeValue("id"));
			type = Integer.parseInt(root.attributeValue("type"));
			
			name = root.attributeValue("name");
			coords = root.attributeValue("coords");
			
			for (Iterator<Element> i = root.elementIterator("health"); i.hasNext();)
			{
				Element element = (Element) i.next();
				
				hpMax = Integer.parseInt(element.attributeValue("hpMax"));
				hp = Integer.parseInt(element.attributeValue("hp"));
			}
			
			/* print out everything before assignment*/
			System.out.println("LOADED THE FOLLOWING:");
			System.out.println("LIST: " + m_actors.get(m_id));
			System.out.println("ID  : " + id);
			System.out.println("XYZ : " + coords);
			System.out.println("NAME: " + name);
			System.out.println("TYPE: " + type);
			System.out.println("MXHP: " + hpMax);
			System.out.println("HP  : " + hp);
			
			m_id = id;
			ID.add(m_id);
			
			m_type = type;
			m_name = name;
			m_hpMax = hpMax;
			m_hp = hp;
			
			split = coords.split(",");
			m_x = Integer.parseInt(split[0]);
			m_y = Integer.parseInt(split[1]);
			m_z = Integer.parseInt(split[2]);
			
			/* put into list of actors */
			m_actors.put(m_id, this);
			
			System.out.println("CREATED ACTOR USING ABOVE:");
			System.out.println("LIST: " + m_actors.get(m_id));
			System.out.println("ID  : " + m_id);
			System.out.println("X   : " + m_x);
			System.out.println("Y   : " + m_y);
			System.out.println("Z   : " + m_z);
			System.out.println("NAME: " + m_name);
			System.out.println("TYPE: " + m_type);
			System.out.println("MXHP: " + m_hpMax);
			System.out.println("HP  : " + m_hp);
			
			System.out.println(
					//"-", 
					"LOADING ACTOR'S INVENTORY");
			
			for (Iterator<Element> i = root.elementIterator("inventory"); i.hasNext();)
			{
				Element element = (Element) i.next();
				for (Iterator<Element> j = element.elementIterator(); j.hasNext();)
				{
					try
					{
					Element children = (Element) j.next();
					//System.out.println(children.getText()); //TODO add stuff to inventory
					
					String item = children.getText();
					addToInventory(new Entity("ent/" + item + ".xml", this));
					}
					catch(EntityIntializationException e)
					{
						System.err.println("Failed to load an entity for actor's inventories.");
					}
				}
			}
			
			System.out.println(
					//"-", 
					"DONE LOADING ACTOR'S INVENTORY");
			
		}
		catch (Exception e)
		{
			/* Besdies EntityInitializationExceptions, if an exception is thrown,
			 * then DO NOT load the actor. */
			throw new ActorIntializationException("Failed to load an actor! " + e);
		}
		finally
		{
			System.out.println(
					//"-", 
					"FINISHED LOADING ACTOR FROM FILE");
		}
	}
	
	@Override
	public String getFilePath() //temp
	{
		return "ent/" + toString();
	}
	
	@Override
	public void writeSave() 
	{
		System.out.println(
				//"-", 
				"WRITING SAVE FOR ACTOR");
		
		System.out.println("SAVING ACTOR USING FOLLOWING:");
		System.out.println("LIST: " + m_actors.get(m_id));
		System.out.println("ID  : " + m_id);
		System.out.println("X   : " + m_x);
		System.out.println("Y   : " + m_y);
		System.out.println("Z   : " + m_z);
		System.out.println("NAME: " + m_name);
		System.out.println("TYPE: " + m_type);
		System.out.println("MXHP: " + m_hpMax);
		System.out.println("HP  : " + m_hp);
		
		//create file (eg. ent/player[0].xml)
		XML w = new XML(getFilePath());
		
		//fetch id, name, type, and coords
		String[] a = {"id", "name", "type", "coords"};
		String[] b = {"" + getId(), getName(), "" + getType(), getXYZ()};
		
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
		//System.out.println("Wrote " + toString() +"'s inventory to " + getFilePath());
		
		System.out.println(
				//"-", 
				"FINISHED WRITING SAVE FOR ACTOR");
	}
	
	/** Refactoring from move because it just did too much in one method. */
	public String[] requestMap(int[] vel, boolean moveToLocation)
	{
		String[] msg = new String[1];
		boolean isInBounds;
		boolean isConnected;
		
		int x = getX();
		int y = getY();
		int z = getZ();
		
		if (vel != null)
		{
			x += vel[0];
			y += vel[1];
			z += vel[2];
		}
		
		isInBounds = boundryCheck(x, y, z);
		isConnected = (m_ignoresCollision || exitCheck(x, y, z));
		
		if (isInBounds && isConnected)
		{
			msg = Render_ASCII.renderMap(x, y, z, m_visionRange);
			
			if(moveToLocation)
				move(vel);
		}
		else
			msg[0] = "No map recieved.";
		
		return msg;
	}
	
	private boolean boundryCheck(int x, int y, int z)
	{
		return (x >= 0 && y >= 0 && z >= 0
				&& x < World.getMapSize() - 1
				&& y < World.getMapSize() - 1
				&& z < World.HEIGHT_LIMIT - 1
				&& (World.isMapRoom(World.getWorld(), x, y, z) || m_ignoresCollision));
	}
	
	private boolean exitCheck(int x, int y, int z)
	{
		return true; //TODO do later
	}
	
	private void move(int[] vel)
	{
		goX(vel[0]);
		goY(vel[1]);
		goZ(vel[2]);
	}
	
	/** Returns X coordinate of entity.
	 * @return X coord of entity */
	public int getX() 
	{
		return m_x;
	}
	/** Returns Y coordinate of entity.
	 * @return Y coord of entity */
	public int getY() 
	{
		return m_y;
	}
	/** Returns Z coordinate of entity.
	 * @return Z coord of entity */
	public int getZ() 
	{
		return m_z;
	}
	/** Returns XYZ as a string.
	 * @return XYZ in format "0,0,0" */
	public String getXYZ()
	{
		return m_x + "," + m_y + "," + m_z;
	}
	
	// goDirection
	private void goX(int x) 
	{
		m_x += x;
	}
	private void goY(int y) 
	{
		m_y += y;
	}
	private void goZ(int z) 
	{
		m_z += z;
	}
	
	/** Get all actors BESIDES the one specified.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return List of actors at those coordinates
	 */
	public static Actor[] getActors(Actor actor, int x, int y, int z)
	{
		Actor[] actors = new Actor[0];
		
		for (int i = 0; i < ID.size(); i++)
		{
			if (actors.length > 200)
			{
				/** If there's an ungodly amount of actors here
				 * then forget fetching the list.
				 */
				return null;
			}
			if (m_actors.get(ID.get(i)) != null
				&& m_actors.get(ID.get(i)) != actor
				&& m_actors.get(ID.get(i)).getX() == x
				&& m_actors.get(ID.get(i)).getY() == y
				&& m_actors.get(ID.get(i)).getZ() == z)
			{
				Actor[] temp = new Actor[actors.length + 1];
				System.arraycopy(actors, 0, temp, 0, actors.length);
				temp[temp.length - 1] = m_actors.get(ID.get(i));
				
				actors = temp;
			}
		}
		return actors;
	}
	
	/** Gets all actors in room including this one. */
	public static Actor[] getActors(int x, int y, int z)
	{
		Actor[] actors = new Actor[0];
		
		for (int i = 0; i < ID.size(); i++)
		{
			if (actors.length > 200)
			{
				/** If there's an ungodly amount of actors here
				 * then forget fetching the list.
				 */
				return null;
			}
			if (m_actors.get(ID.get(i)) != null
				//&& m_actors.get(ID.get(i)) != actor
				&& m_actors.get(ID.get(i)).getX() == x
				&& m_actors.get(ID.get(i)).getY() == y
				&& m_actors.get(ID.get(i)).getZ() == z)
			{
				Actor[] temp = new Actor[actors.length + 1];
				System.arraycopy(actors, 0, temp, 0, actors.length);
				temp[temp.length - 1] = m_actors.get(ID.get(i));
				
				actors = temp;
			}
		}
		return actors;
	}
	
	public static Actor getById(int id)
	{
		if (m_actors.containsKey(id))
		{
			System.out.println("fetched something");
			return m_actors.get(id);
		}
		else
		{
			System.out.println("didn't fetch something");
			return null;
		}
	}
	
	public static boolean existsById(int id)
	{
		return m_actors.containsKey(id);
	}
	
	public static Actor getActorsByName(String name) throws NullPointerException
	{
		for (int i = 0; i < ID.size(); i++)
		{
			if(m_actors.get(ID.get(i)) != null)
			{
				System.out.println("CHECKING ID " + ID.get(i));
				System.out.println("checking " + name + " against " 
						+ stripID(m_actors.get(ID.get(i)).toString())
						+ " (" + m_actors.get(ID.get(i)).toString() + ")");
				
				if (m_actors.get(ID.get(i)).getName().equalsIgnoreCase(stripID(name))
						//|| m_actors.get(ID.get(i)).toString().equalsIgnoreCase(name)
						) 
				{
					//why compare toString against something that strips toString()???
					return m_actors.get(ID.get(i));
				}
			}
		}
		System.out.println("failed to match " + name);
		return null;
	}
	
	public static Actor getActorsByNameAndID(String fullname) throws NullPointerException
	{
		for (int i = 0; i < ID.size(); i++)
		{
			if(m_actors.get(ID.get(i)) != null)
			{
				if(m_actors.get(ID.get(i)).getName().equals(fullname))
					return m_actors.get(ID.get(i));
			}
		}
		return null;
	}
	
	public static String stripID(String name)
	{
		if (name.contains("["))
			return name.substring(0, name.lastIndexOf("["));
		else
			return name;
	}
	
	public static boolean existsByName(String name)
	{
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_actors.get(ID.get(i)) != null
				&& m_actors.get(ID.get(i)).toString().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	public String dropFromInv(Entity entity)
	{
		entity.dropFromInv();
		
		return "asdfu";
	}
	
	/** debug list print */
	public static String[] debug()
	{
		String[] list = new String[m_actors.size()];
		int a = 0;
		
		for (int i = 0; i < ID.size(); i++)
		{
			//System.out.println(i);
			if (m_actors.get(ID.get(i)) != null)
			{
				list[a] = "[ACTOR]" + m_actors.get(ID.get(i)).toString();
				a++;
			}
		}
		return list;
	}
	
	public static final void saveAll()
	{
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_actors.get(ID.get(i)) != null)
			{
				m_actors.get(ID.get(i)).writeSave();
			}
		}
	}
	
	public static final Actor[] getAll()
	{
		Actor[] list = new Actor[m_actors.size()];
		int a = 0;
		
		for (int i = 0; i < ID.size(); i++)
		{
			if(m_actors.get(ID.get(i)) != null)
			{
				list[a] = m_actors.get(ID.get(i));
				a++;
			}
		}
		return list;
	}
	
	/** Removes all references to this entity so that
	 * java's garbage collector will clean it up whenever. */
	@Override
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
		m_actors.remove(m_id); //no references, should clean up
		m_name = null;
		m_inv = null;
	}
	
}




