package apk.parser.action;

import apk.main.engine.Actor;
import apk.reference.WordList;

public class Look extends WordList
{
	/** Used in more general wordlists for easier changing. */
	private String m_d = "look";
	
	public Look()
	{
		list.put("look",		m_d);
		list.put("l",			m_d);
		
		list.put("err", "Look where?");
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
		
		/** When I started to stress test how many actors the engine could
		 * handle, this function started to get ridiculously slow (due to
		 * the sheer amount of things it had to fetch from a list, assemble
		 * into a string, and then send to the client).
		 * 
		 * The work around right now is that if there's over 25 actors in a
		 * a room (not including you), the player will see a 'crowd' of people.
		 * The reasoning behind this is pretty simple - a ton of people in the
		 * same area makes it hard to find the person or persons you're trying
		 * to look for.
		 * 
		 * I'll probably make it so that you can search a crowd for a specific
		 * actor (given that you know they're there).
		 */
		
		if (actors == null)
			return "You feel dizzy and utterly lost amongst the waves of people in the crowd here.";
		if (actors.length <= 0)
			return "";
		else if (actors.length >= 25 && actors.length < 50) //TODO client-side language files
			return "You see a crowd of people here.";
		else if (actors.length >= 50 && actors.length < 100)
			return "You see a large crowd of people here.";
		else if (actors.length >= 100 && actors.length < 200)
			return "You see an absolutely massive crowd of people here.";
		
		String actorsInRoom = "You see: ";
		
		for (int i = 0; i < actors.length; i++)
		{
			actorsInRoom += actors[i].getName();
			
			if (actors.length - 1 - i == 1)
				actorsInRoom += " and ";
			else if (actors.length - 1 - i > 1)
				actorsInRoom += ", ";
			//else if (actors.length - 1 - i == 0)
				//System.out.println("4");
		}
		
		//TODO more advanced parsing~
		//actorsInRoom += " standing here.";
		
		return actorsInRoom;
	}
}
