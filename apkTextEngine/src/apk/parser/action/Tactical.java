package apk.parser.action;

import apk.main.engine.Actor;
import apk.reference.WordList;

public class Tactical extends WordList
{
	/** For printing named health bars for all WorldEntites */
	public Tactical()
	{
		list.put("tactical", 	"tactical");
		list.put("tac", 		"tactical");
	}
	
	public String[] getActorsTacticalInRoom(Actor actor)
	{
		String[] msg = new String[1];
		
		int x = actor.getX();
		int y = actor.getY();
		int z = actor.getZ();
		
		Actor[] actors = Actor.getActors(x, y, z);
		
		if (actors == null || actors.length > 10)
			msg[0] = "You don't think you can tactically appraise all these people.";
		else
		{
			msg = new String[actors.length];
			
			for (int i = 0; i < actors.length; i++)
			{
				msg[i] = actors[i].printNamedHealthBar();
			}
		}
		return msg;
	}
}
