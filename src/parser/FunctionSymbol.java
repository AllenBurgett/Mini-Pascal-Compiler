package parser;

import scanner.Keywords;

public class FunctionSymbol extends ProcedureSymbol {
	
	private Keywords type;
	
	public FunctionSymbol( String identifier){
		super( identifier);
	}
	
	public Keywords getType(){
		return this.type;
	}
	
	public void setType( Keywords type){
		this.type = type;
	}
	
	@Override
	public String toString(){
		return super.identifier + " FUNCTION " + this.type.toString();
	}
}
