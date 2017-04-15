package parser;

import scanner.Keywords;

/**
 * Represents an array variable in the symbol table.
 * @author Allen Burgett
 *
 */
public class ArraySymbol extends VariableSymbol {

	private Integer arrayStart;
	private Integer arrayEnd;
	
	/**
	 * Initializes an ArraySymbol. Invokes VariableSymbol's constructor to set
	 * identifier, dataIdentifier, and type.
	 * @param identifier, name of the array as written in the source code.
	 * @param dataIdentifier, name of the array as used by the code generatior
	 * @param type, Integer or Real
	 * @param start, start index of the array.
	 * @param end, end index of the array.
	 */
	public ArraySymbol( String identifier, String dataIdentifier, Keywords type, Integer start, Integer end){
		super( identifier, dataIdentifier, type);
		this.arrayStart = start;
		this.arrayEnd = end;
	}
	
	/**
	 * 
	 * @return the starting index of the array.
	 */
	public int getArrayStart() {
		return arrayStart;
	}

	/**
	 * 
	 * @return the ending index of the array.
	 */
	public int getArrayEnd() {
		return arrayEnd;
	}
	
	@Override
	public String toString(){
		return super.identifier + " Array " + super.type.toString() + " [" + this.arrayStart + ":" + this.arrayEnd + "]";
	}
}
