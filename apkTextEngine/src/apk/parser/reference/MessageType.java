package apk.parser.reference;

/** MessageType.java
 * - Describes messages sent to clients so that they recieve the right messages.
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
 * <li>MSG_PERSONAL takes four arguments - [MSG_PERSONAL] [%SOURCE] [%ACTION] [%TARGET]
 * <li>MSG_ROOMWIDE takes five arguments - [MSG_ROOMWIDE] [%ACTOR]  [%SOURCE] [%TARGET] [%TARGET2]
 * <li>MSG_AREAWIDE takes five arguments - [MSG_AREAWIDE] [%SIZE]   [%SOURCE] [%ACTION] [%TARGET]
 * </ul>
 * 
 * @author Andrew Kopczynski
 *
 */
public class MessageType
{
	/** A message that should only be recieved by the person
	 * performing the action.
	 * <p>
	 * <ul><i><b>Examples:</b></i>
	 * <li>Looking around a room
	 * <li>Looking into your inventory
	 * <li>Generally any action that only you should be able to see or know about.
	 * </ul>
	 */
	public static final int MSG_PERSONAL = 0;
	
	/** A message that should be recieved by everyone in the same
	 * room (X/Y/Z coordinates)
	 * <p>
	 * <ul><i><b>Examples:</b></i>
	 * <li>Saying something (say blah, everyone should see 'actor says, "blah"')
	 * <li>Picking up or dropping things
	 * <li>Generally any action involving two actors
	 */
	public static final int MSG_ROOMWIDE = 1;
	
	/** A message that should be recieved by people in a given area.
	 * <p>
	 * <ul><i><b>Examples:</b></i>
	 * <li>A loud noise like an explosion
	 * <li>Someone yelling from a room or two away to someone else
	 * <li>Generally any other loud, noise based event.
	 */
	public static final int MSG_AREAWIDE = 2;
}
