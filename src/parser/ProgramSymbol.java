package parser;

public class ProgramSymbol extends Symbol {
	
	public ProgramSymbol( String identifier){
		super( identifier);
	}
	
	@Override
	public String toString(){
		return super.identifier + " PROGRAM";
	}
}
