package parser;

import scanner.Keywords;

public class VariableSymbol extends Symbol {
	
	protected Keywords type;
	protected String dataIdentifier;
	
	public VariableSymbol( String identifier, String dataIdentifier, Keywords type){
		super( identifier);
		this.type = type;
		this.dataIdentifier = dataIdentifier;
	}
	
	public Keywords getType(){
		return this.type;
	}
	
	public String getDataIdentifier(){
		return dataIdentifier;
	}
	
	@Override
	public String toString(){
		return super.identifier + " VARIABLE " + this.type.toString();
	}
}
