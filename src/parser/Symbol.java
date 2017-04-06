package parser;

public class Symbol {
	protected String identifier;
	
	public Symbol(String identifier){
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public String toString(){
		return identifier; 
	}
	
	
}
