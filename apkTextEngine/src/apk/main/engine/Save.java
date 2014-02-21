package apk.main.engine;

/** Responsible for saving and loading the game.
 * 
 * @author Andrew Kopczynski
 *
 */
public class Save
{

	public static final void all()
	{
		entities();
		actors();
	}
	
	public static final void entities()
	{
		Entity.saveAll();
	}
	
	public static final void actors()
	{
		Actor.saveAll();
	}
}
