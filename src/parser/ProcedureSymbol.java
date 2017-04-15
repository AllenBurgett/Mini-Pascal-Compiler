package parser;

import java.util.HashMap;

/**
 * Represents a procedure in the symbol table.
 * @author Allen Burgett
 *
 */
public class ProcedureSymbol extends Symbol {
	
	private HashMap<String, Symbol> localSymbolTable = new HashMap<String, Symbol>();

	/**
	 * Initializes a ProcedureSymbol. Invokes Symbol's Constructor to set the identifier.
	 * @param identifier, name of the procedure.
	 */
	public ProcedureSymbol( String identifier){
		super( identifier);
	}
	
	/**
	 * 
	 * @return a table of all symbols contained in this procedure.
	 */
	public HashMap<String, Symbol> getLocalSymbolTable(){
		return this.localSymbolTable;
	}
	
	/**
	 * Associate a HashMap of Strings and Symbols with this procedure.
	 * @param table, a table containg the symbols associated with this procedure.
	 */
	public void storeTable( HashMap<String, Symbol> table){
		this.localSymbolTable = table;
	}
	
	@Override
	public String toString(){
		return super.identifier + " PROCEDURE ";
	}
}
