package parser;

import java.util.HashMap;

/**
 * Represents a symbol table that can be incorporated into a stack. This
 * key/value pair, allows identification of where the symbol table came 
 * from, so it can be placed back in the appropriate procedure symbol 
 * for storage.
 * @author Allen Burgett
 *
 */
public class StackEntry {
	private String identifier = null;
	private HashMap<String, Symbol> table = null;
	
	/**
	 * Initializes a key/value pair for stack interfacing.
	 * @param identifier, name of the associated procedure.
	 * @param table, table of symbols for the associated procedure.
	 */
	public StackEntry( String identifier, HashMap<String, Symbol> table){
		this.identifier = identifier;
		this.table = table;
	}

	/**
	 * 
	 * @return name of associated procedure.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * 
	 * @return table of symbols from associated procedure.
	 */
	public HashMap<String, Symbol> getTable() {
		return table;
	}
	
	
}
