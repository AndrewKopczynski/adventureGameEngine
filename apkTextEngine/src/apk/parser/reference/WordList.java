package apk.parser.reference;

import java.util.HashMap;
import java.util.Map;

public class WordList
{
	protected Map<String, String> list = new HashMap<String, String>();
	
	public String getMeaning(String input) //TODO remove
	{
		if (list.containsKey(input))
			return list.get(input);
		return null;
	}
	
	public boolean checkForMod(String[] in)
	{
		for (int i = 0; i < in.length; i++)
			if (getMeaning(in[i]) != null)
				return true;
		return false;
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
}
