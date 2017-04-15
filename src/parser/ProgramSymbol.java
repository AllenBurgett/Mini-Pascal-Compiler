package parser;

/**
 * Represents the Program in the symbol table.
 * @author Allen Burgett
 *
 */
public class ProgramSymbol extends Symbol {
	
	/**
	 * Initializes a ProgramSymbol. Invokes Symbol's Constructor
	 * to set the identifier.
	 * @param identifier, the name of the program.
	 */
	public ProgramSymbol( String identifier){
		super( identifier);
	}
	
	@Override
	public String toString(){
		return super.identifier + " PROGRAM";
	}
}
