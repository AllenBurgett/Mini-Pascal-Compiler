package parser;

import scanner.Keywords;

public class FunctionSymbol extends ProcedureSymbol {
	
	private Keywords type;
	
	public FunctionSymbol( String identifier, Keywords type){
		super( identifier);
		this.type = type;
	}
	
	public Keywords getType(){
		return this.type;
	}
	
	@Override
	public String toString(){
		return super.identifier + " FUNCTION " + this.type.toString();
	}
}
