package parser.reference;

import java.util.HashMap;
import java.util.Map;

public class WordList
{
	protected Map<String, String> list = new HashMap<String, String>();
	
	public String getMeaning(String input)
	{
		if (list.containsKey(input))
		{
			return list.get(input);
		}
		
		else
		{
			return "";
		}
	}

	public String getError(String input)
	{
		if (list.containsKey("err") || list.get(input).equalsIgnoreCase("null"))
		{
			return list.get("err");
		}
		
		else
		{
			return "Look man, it's a shortcut, not a verb."
					+ "\nI can't direction something, I just can't.";
		}
	}

	public boolean contains(String input)
	{
		return list.containsKey(input);
	}
}
