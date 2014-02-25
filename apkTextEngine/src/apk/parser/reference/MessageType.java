package apk.parser.reference;

import apk.main.engine.Actor;

/** MessageType.java
 * - Describes messages sent to clients so that they receive the right messages.
 * <p>
 * <ul>Messages:
 * 
 * <li>Who should be able to see that action?
 * 
 * <li>Who is doing the action? (%ACTION by actor)
 * <li>What action is being done?
 * 
 * <li>Who they are doing that action to? (%TARGET)
 * <li>Is there a secondary target? (%TARGET2)
 * </ul>
 * <p>
 * <i><b>Examples:</b></i> Using 'player_zesty' and 'player_lemons'
 * <p>
 * 
 * <ul>'look'
 * <li>[MSG_PERSONAL] [player_zesty] [LOOK] [ROOM_XYZ]
 * </ul>
 * 
 * <ul>'get exampleItem'
 * <li>[MSG_ROOMWIDE] [player_zesty] [GET] [exampleItem] [//here?/ROOM_XYZ?]
 * </ul>
 * 
 * <ul>'put exampleItem in large crate'
 * <li>[MSG_ROOMWIDE] [player_zesty] [PUT] [exampleItem] [large crate]
 * </ul>
 * 
 * <ul>'give medkit to player_lemons'
 * <li>[MSG_ROOMWIDE] [player_zesty] [GIVE] [medkit] [player_lemons]
 * </ul>
 * 
 * <ul>'explode here'
 * <li>[MSG_AREAWIDE] [5] [WORLD] [EXPLODE] [ROOM_XYZ]
 * </ul>
 * 
 * <ul>Notes
 * <li>MSG_PERSONAL takes five arguments - [MSG_PERSONAL] [%ACTOR] [%ACTION] [%TARGET]            {CONTENTS OF MESSAGE}
 * <li>MSG_ROOMWIDE takes six arguments -  [MSG_ROOMWIDE] [%ACTOR] [%ACTION] [%TARGET] [%TARGET2] {CONTENTS OF MESSAGE}
 * <li>MSG_AREAWIDE takes six arguments -  [MSG_AREAWIDE] [%SIZE]  [%ACTOR]  [%ACTION] [%TARGET]  {CONTENTS OF MESSAGE}
 * </ul>
 * 
 * @author Andrew Kopczynski
 *
 */
public class MessageType
{
	/** A message that should only be received by the person
	 * performing the action.
	 * <p>
	 * <ul><i><b>Examples:</b></i>
	 * <li>Looking around a room
	 * <li>Looking into your inventory
	 * <li>Generally any action that only you should be able to see or know about.
	 * </ul>
	 */
	public static final int MSG_PERSONAL = 0;
	public static final int MSG_PERSONAL_LENGTH = 4; //how long the message stuff is before the contents
	
	/** A message that should be received by everyone in the same
	 * room (X/Y/Z coordinates)
	 * <p>
	 * <ul><i><b>Examples:</b></i>
	 * <li>Saying something (say blah, everyone should see 'actor says, "blah"')
	 * <li>Picking up or dropping things
	 * <li>Generally any action involving two actors
	 */
	public static final int MSG_ROOMWIDE = 1;
	public static final int MSG_ROOMWIDE_LENGTH = 5; //how long the message stuff is before the contents
	
	/** A message that should be received by people in a given area.
	 * <p>
	 * <ul><i><b>Examples:</b></i>
	 * <li>A loud noise like an explosion
	 * <li>Someone yelling from a room or two away to someone else
	 * <li>Generally any other loud, noise based event.
	 */
	public static final int MSG_AREAWIDE = 2;
	public static final int MSG_AREAWIDE_LENGTH = 5; //how long the message stuff is before the contents
	
	public static String[] personal(Actor actor, String action, String target, String[] contents)
	{
		//simply put, this message means the following:
		//this is a PERSONAL MESSAGE to the ACTOR (followed by extra stuff describing what they did)
		String[] msg = {"" + MSG_PERSONAL, actor.toString(), action, target};
		String[] compiled = new String[msg.length + contents.length];
		
		System.arraycopy(msg, 0, compiled, 0, msg.length);
		System.arraycopy(contents, 0, compiled, msg.length, contents.length);
		
		return compiled;
	}
	
	public static String[] roomwide(Actor actor, String action, String target, String target2, String[] contents)
	{
		String[] msg = {"" + MSG_ROOMWIDE, actor.toString(), action, target, target2};
		String[] compiled = new String[msg.length + contents.length];
		
		System.arraycopy(msg, 0, compiled, 0, msg.length);
		System.arraycopy(contents, 0, compiled, msg.length, contents.length);
		
		return compiled;
	}
	
	public static String[] areawide(int size, Actor actor, String action, String target, String[] contents)
	{
		String[] msg = {"" + MSG_AREAWIDE, "" + size, actor.toString(), action, target};
		String[] compiled = new String[msg.length + contents.length];
		
		System.arraycopy(msg, 0, compiled, 0, msg.length);
		System.arraycopy(contents, 0, compiled, msg.length, contents.length);
		
		return compiled;
	}
}
