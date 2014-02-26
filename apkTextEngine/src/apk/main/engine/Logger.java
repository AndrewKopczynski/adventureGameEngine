package apk.main.engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
//import java.util.Date;

/** Class for logging absolutely everything that happens,
 * will be super useful later on.
 * 
 * @author Andrew Kopczynski
 * @version 0.1 */
public class Logger 
{
	/** Filename of output log file. */
	//private static Date date = new Date();
	//private static final String m_filename =  date.toString() + ".log";
	private static final String m_filename = "log.log";
	
	private static long m_start;
	private static long m_stop;
	
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
		try //disgustingly slow, seriously, like 20ms a write
		{
			start();
			
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(m_filename, true)));
		    StackTraceElement[] st = Thread.currentThread().getStackTrace();
		    
		    //don't bother fixing this srsly, stacktrace is really finicky
		    String prefix = st[2].getClassName().toUpperCase().replace("APK.MAIN.ENGINE.", "");
		    
		    prefix = prefix.toUpperCase() + ": " + "\t";
		    if (prefix.length() < 8) 
		    {
		    	prefix = prefix + "\t";
		    }
		    
		    out.println(prefix + entry);
		    out.close();
		    
		    stop(true);
		//} catch (IOException e)
		} catch (Exception e)
		{
			System.out.println("!CRITICAL! An entry failed to be logged in the debug log!");
		}
	}
	
	public static void start()
	{
		m_start = System.nanoTime();
	}
	
	public static void stop(boolean shouldPrint)
	{
		m_stop = System.nanoTime();
		
		if (shouldPrint)
		{
			System.out.print("[EXECUTION TIME]");
			if ((m_stop - m_start) / 1000000000 > 1)
				System.out.println(((m_stop - m_start) / 1000000000) + "s");
			else
				System.out.println(((m_stop - m_start) / 1000000) + "ms");
		}
	}
	
	public static void printDebug(String s)
	{
		//System.out.println(getLine(s));
		//System.out.println(s.toUpperCase());
		//System.out.println(getLine(s));
		
		log(getLine(s));
		log(s.toUpperCase());
		log(getLine(s));
	}
	
	/*public static void printDebug(boolean headerFooter, String s)
	{
		if (headerFooter)
			System.out.println(getLine(s));
		System.out.println(s);
		if (!headerFooter)
			System.out.println(getLine(s));
	}*/
	
	private static String getLine(String s)
	{
		String t = "";
		for (int i = 0; i < s.length() && !s.substring(i, i+1).equalsIgnoreCase("\n"); i++)
			t += "-";
		
		return t;
	}
}
