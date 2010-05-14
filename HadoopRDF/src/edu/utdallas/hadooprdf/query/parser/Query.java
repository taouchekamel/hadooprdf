package edu.utdallas.hadooprdf.query.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Query 
{
	private ArrayList<HadoopElement> elements = new ArrayList <HadoopElement> ();
	private HashMap <String, String> nsPrefixMap = new HashMap<String, String> ();
	
	public Query (List<HadoopElement> elementList, Map <String,String> prefixMap) 
	{
		elements.addAll(elementList);
		nsPrefixMap.putAll(prefixMap);
	}
	
	public List<HadoopElement> getElements () { return elements; }
	
	public Set<String> getNsPrefixKeySet () { return nsPrefixMap.keySet(); }
	
	public String getNsPrefix (String key) { return nsPrefixMap.get(key); }	
}
