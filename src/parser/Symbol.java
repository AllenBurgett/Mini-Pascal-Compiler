package parser;

public class Symbol {
	private String identifier;
	private Kinds kind;
	private String type;
	private Integer arrayStart;
	private Integer arrayEnd;
	
	public Symbol(String identifier, Kinds kind, String type, Integer start, Integer end){
		this.identifier = identifier;
		this.kind = kind;
		this.type = type;
		this.arrayStart = start;
		this.arrayEnd = end;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Kinds getKind() {
		return kind;
	}

	public String getType() {
		return type;
	}	
	
	public int getArrayStart() {
		return arrayStart;
	}

	public int getArrayEnd() {
		return arrayEnd;
	}

	public String toString(){
		return identifier + " " + kind.toString() + " " + type; 
	}
	
	
}
