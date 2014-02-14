package apk.parser.action;

import apk.main.engine.Actor;
import apk.parser.reference.WordList;

public class Look extends WordList
{
	/** Used in more general wordlists for easier changing. */
	private String m_d = "look";
	
	public Look()
	{
		list.put("look",		m_d);
		list.put("l",			m_d);
		
		list.put("err", "Usage: 'look [item]'");
	}
	
	public String getActorsInRoom(Actor actor, int vel[])
	{
		int x = actor.getX();
		int y = actor.getY();
		int z = actor.getZ();
		
		if (vel != null)
		{
			x += vel[0];
			y += vel[1];
			z += vel[2];
		}
		
		Actor[] actors = Actor.getActors(actor, x, y, z);
		
		if (actors.length == 0)
			return "";
		
		String actorsInRoom = "You see ";
		
		for (int i = 0; i < actors.length; i++)
		{
			actorsInRoom += actors[i].getName();
			
			if (actors.length - 1 - i == 1)
				actorsInRoom += " and ";
			else if (actors.length - 1 - i > 1)
				actorsInRoom += ", ";
			else if (actors.length - 1 - i == 0)
				System.out.println("4");
		}
		
		actorsInRoom += " standing here.";
		
		return actorsInRoom;
	}
}
