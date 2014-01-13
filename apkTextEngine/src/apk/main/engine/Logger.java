package apk.main.engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/** Class for logging absolutely everything that happens,
 * will be super useful later on.
 * 
 * @author Andrew Kopczynski
 * @version 0.1 */
public class Logger 
{
	/** Filename of output log file. */
	private static final String m_filename = "log.log";
	/** Clears the log.txt file. */
	public static void clear() 
	{
		try 
		{
	        BufferedWriter out = new BufferedWriter(new FileWriter(m_filename));
	        log("Log cleared!");
	        out.close();
	    } catch (IOException e) 
	    {
			System.out.println("!CRITICAL! Couldn't clear the log");
	    }
	}
	
	/** Logs an entry in the following format:
	 * <p>
	 * CLASSNAME:	entry
	 * 
	 * @param entry String to be printed in log
	 */
	public static void log(String entry) 
	{
		try 
		{
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(m_filename, true)));
		    StackTraceElement[] st = Thread.currentThread().getStackTrace();
		    
		    String prefix = st[2].getClassName().toUpperCase().replace("APK.MAIN.ENGINE.", "");
		    
		    prefix = prefix.toUpperCase() + ": " + "\t";
		    if (prefix.length() < 8) 
		    {
		    	prefix = prefix + "\t";
		    }
		    
		    out.println(prefix + entry);
		    out.close();
		//} catch (IOException e)
		} catch (Exception e)
		{
			System.out.println("!CRITICAL! An entry failed to be logged in the debug log!");
		}
	}
}
