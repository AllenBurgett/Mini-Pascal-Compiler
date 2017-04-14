package parser;

import scanner.Keywords;

public class ArgumentSymbol extends VariableSymbol {
	private int argNum = 0;
	
	public ArgumentSymbol(String identifier, String dataIdentifier, Keywords type, int argNum){
		super(identifier, dataIdentifier, type);
		this.argNum = argNum;
	}
	
	public int getArgNum(){
		return argNum;
	}
	
	@Override
	public String toString(){
		return super.identifier + " ARGUMENT " + this.argNum + " " + super.type.toString();
	}
}
