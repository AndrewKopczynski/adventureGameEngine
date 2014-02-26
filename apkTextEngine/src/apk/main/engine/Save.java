package apk.main.engine;

//import static apk.main.engine.Logger.printDebug;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import apk.parser.reference.ActorIntializationException;
import apk.parser.reference.IDConflictException;

/** Responsible for saving and loading the game.
 * 
 * @author Andrew Kopczynski
 *
 */
public class Save
{

	public static final void state()
	{
		XML w = new XML("save");

		String[] a = {"worldPath"				, "tilePath"};
		String[] b = {World.getWorldFilePath()	, World.getTileFilePath()};
		
		w.writeOpenTag("save", a, b);
		
		for (int i = 0; i < Actor.getAll().length; i++)
		{
			w.writeTextContent("actor"			, Actor.getAll()[i].toString());
		}
		
		w.writeCloseTag("save");
		w.close();
	}
	
	public static final void load(String saveFilePath) throws MalformedURLException, DocumentException, FileNotFoundException, IDConflictException, ActorIntializationException
	{
		URL saveURL = new File(saveFilePath).toURI().toURL();
		
		Document saveD = XML.parse(saveURL);
		Element root = saveD.getRootElement();
		
		String world = root.attributeValue("worldPath");
		String tileset = root.attributeValue("tilePath");
		
		for (Iterator<Element> i = root.elementIterator("actor"); i.hasNext();)
		{
			Element element = (Element) i.next();
			new Actor("ent/" + element.getText() + ".xml");
		}
		new World(world, tileset);
	}
	
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
