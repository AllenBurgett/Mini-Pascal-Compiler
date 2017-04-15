package parser;

import scanner.Keywords;

/**
 * Represents a single procedure or function's passed argument.
 * Stores the position that the argument exists in the arguments
 * declarations. This is used by the code generator to store the
 * appropriate argument in to the stack upon a jump. 
 * @author Allen Burgett
 *
 */
public class ArgumentSymbol extends VariableSymbol {
	private int argNum = 0;
	
	/**
	 * Initializes an ArgumentSymbol. This invokes the VariableSymbol Constructor to set the 
	 * identifier, dataIdentifier, and type.
	 * @param identifier, name as written in the source
	 * @param dataIdentifier, name to be written during code generation (stack location or data location)
	 * @param type, Integer or Real
	 * @param argNum, the position this argument sits in the procedure declaration 
	 */
	public ArgumentSymbol(String identifier, String dataIdentifier, Keywords type, int argNum){
		super(identifier, dataIdentifier, type);
		this.argNum = argNum;
	}
	
	/**
	 * 
	 * @return the position that this argument is declared in the procedure declaration.
	 */
	public int getArgNum(){
		return argNum;
	}
	
	@Override
	public String toString(){
		return super.identifier + " ARGUMENT " + this.argNum + " " + super.type.toString();
	}
}
