package parser;

import scanner.Keywords;

public class VariableSymbol extends Symbol {
	
	protected Keywords type;
	
	public VariableSymbol( String identifier, Keywords type){
		super( identifier);
		this.type = type;
	}
	
	public Keywords getType(){
		return this.type;
	}
	
	@Override
	public String toString(){
		return super.identifier + " VARIABLE " + this.type.toString();
	}
}
