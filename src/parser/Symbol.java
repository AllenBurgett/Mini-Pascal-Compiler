package parser;

/**
 * Represents a generic symbol in the symbol table.
 * @author Allen Burgett
 *
 */
public class Symbol {
	protected String identifier;
	
	/**
	 * Initializes a Symbol.
	 * @param identifier, name of symbol.
	 */
	public Symbol(String identifier){
		this.identifier = identifier;
	}
	
	/**
	 * 
	 * @return name of symbol.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Convert symbol to string.
	 */
	public String toString(){
		return identifier; 
	}
	
	
}
