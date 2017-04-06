package parser;

import java.util.HashMap;

public class StackEntry {
	private String identifier = null;
	private HashMap<String, Symbol> table = null;
	
	public StackEntry( String identifier, HashMap<String, Symbol> table){
		this.identifier = identifier;
		this.table = table;
	}

	public String getIdentifier() {
		return identifier;
	}

	public HashMap<String, Symbol> getTable() {
		return table;
	}
	
	
}
