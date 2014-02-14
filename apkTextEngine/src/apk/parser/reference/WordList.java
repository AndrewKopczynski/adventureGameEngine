package apk.parser.reference;

import java.util.HashMap;
import java.util.Map;

public class WordList
{
	protected Map<String, String> list = new HashMap<String, String>();
	
	public String getMeaning(String input) //TODO remove probably
	{
		if (list.containsKey(input))
			return list.get(input);
		return null;
	}
	
	public boolean checkForMod(String[] in)
	{
		for (int i = 0; i < in.length; i++)
			if (list.get("%MOD") != null 
			&& list.get("%MOD").equalsIgnoreCase(in[i]))
				return true;
		return false;
	}
	
	public String getMod()
	{
		return list.get("%MOD");
	}
	
	public boolean check(String in)
	{
		if (list.containsKey(in))
			return true;
		return false;
	}
	
	public boolean check(String[] in)
	{
		if (list.containsKey(in[0]))
			return true;
		return false;
	}

	public String[] getError()
	{
		String err[] = {"MISSING_ERROR"};
		if (list.containsKey("err"))
			err[0] = list.get("err");
		
		return err;
	}
	
	public String[] getError(String in)
	{
		String[] err = getError();
		
		err[0] = err[0].replace("%ACTION", in.substring(0, 1).toUpperCase() 
				+ in.substring(1, in.length()));
		
		return err;
	}
	
	public String[] getError(String a, String b)
	{
		String[] err = getError();
		
		err[0] = err[0].replace("%ACTION", a.substring(0, 1).toUpperCase() 
				+ a.substring(1, a.length()));
		err[0] = err[0].replace("%TARGET", b);
		
		return err;
	}
}
