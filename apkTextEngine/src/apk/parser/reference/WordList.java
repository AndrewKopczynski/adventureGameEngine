package apk.parser.reference;

import java.util.HashMap;
import java.util.Map;

public class WordList
{
	protected Map<String, String> list = new HashMap<String, String>();
	
	public String getMeaning(String input)
	{
		if (list.containsKey(input))
			return list.get(input);
		return null;
	}
	
	public boolean check(String[] in)
	{
		if (getMeaning(in[0]) != null)
			return true;
		return false;
	}
	
	public boolean check(String in)
	{
		if (getMeaning(in) != null)
			return true;
		return false;
	}
		

	public String getError(String input)
	{
		if (list.containsKey("err") || list.get(input).equalsIgnoreCase("null"))
			return list.get("err");
		return "MISSING_ERROR";
	}
}
