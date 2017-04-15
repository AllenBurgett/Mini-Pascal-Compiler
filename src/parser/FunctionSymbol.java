package parser;

import scanner.Keywords;

/**
 * Represents a Function in the symbol table.
 * @author Allen Burgett
 *
 */
public class FunctionSymbol extends ProcedureSymbol {
	
	private Keywords type;
	
	/**
	 * Initializes a FunctionSymbol. Invokes ProcedureSymbol's
	 * Constructor to set identifier.
	 * @param identifier, name of the function.
	 */
	public FunctionSymbol( String identifier){
		super( identifier);
	}
	
	/**
	 * 
	 * @return Integer or Real
	 */
	public Keywords getType(){
		return this.type;
	}
	
	/**
	 * Sets the type of the function's return.
	 * @param type, Integer or Real
	 */
	public void setType( Keywords type){
		this.type = type;
	}
	
	@Override
	public String toString(){
		return super.identifier + " FUNCTION " + this.type.toString();
	}
}
