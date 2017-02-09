package parser;

public class Symbol {
	private String identifier;
	private Kinds kind;
	private String type;
	
	public Symbol(String identifier, Kinds kind, String type){
		this.identifier = identifier;
		this.kind = kind;
		this.type = type;
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
	
}
