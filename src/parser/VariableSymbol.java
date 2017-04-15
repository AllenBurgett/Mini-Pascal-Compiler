package parser;

import scanner.Keywords;

/**
 * Represents a variable in the symbol table.
 * @author Allen Burgett
 *
 */
public class VariableSymbol extends Symbol {
	
	protected Keywords type;
	protected String dataIdentifier;
	
	/**
	 * Initializes a VariableSymbol. Invokes the Symbol Constructor to set the identifier.
	 * @param identifier, name of the variable as written in the source code.
	 * @param dataIdentifier, name of the variable as used by the code generator (stack location or data location).
	 * @param type, Integer or Real.
	 */
	public VariableSymbol( String identifier, String dataIdentifier, Keywords type){
		super( identifier);
		this.type = type;
		this.dataIdentifier = dataIdentifier;
	}
	
	/**
	 * 
	 * @return Integer or Real.
	 */
	public Keywords getType(){
		return this.type;
	}
	
	/**
	 * 
	 * @return a String that the code generator can use to find the location of the variable's data.
	 */
	public String getDataIdentifier(){
		return dataIdentifier;
	}
	
	@Override
	public String toString(){
		return super.identifier + " VARIABLE " + this.type.toString();
	}
}
