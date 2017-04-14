package parser;

import scanner.Keywords;

public class ArraySymbol extends VariableSymbol {

	private Integer arrayStart;
	private Integer arrayEnd;
	
	public ArraySymbol( String identifier, String dataIdentifier, Keywords type, Integer start, Integer end){
		super( identifier, dataIdentifier, type);
		this.arrayStart = start;
		this.arrayEnd = end;
	}
	
	public int getArrayStart() {
		return arrayStart;
	}

	public int getArrayEnd() {
		return arrayEnd;
	}
	
	@Override
	public String toString(){
		return super.identifier + " Array " + super.type.toString() + " [" + this.arrayStart + ":" + this.arrayEnd + "]";
	}
}
