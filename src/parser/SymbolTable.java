package parser;

import java.util.Collection;
import java.util.HashMap;

public class SymbolTable {
	
	HashMap<String, Symbol> identifierTable = new HashMap<String, Symbol>();
	
	public SymbolTable(){
		this.add("write", Kinds.FUNCTION, "VOID");
		this.add("read", Kinds.FUNCTION, "VOID");
	}
	
	public boolean add( String identifier, Kinds kind, String type){
		boolean answer = false;
		if(! identifierTable.containsKey(identifier)){
			Symbol symbol = new Symbol(identifier, kind, type);
			identifierTable.put(identifier, symbol);
			answer = true;
		}
		return answer;
	}
	
	public boolean isVariableName( String name){
		boolean answer = false;
		if(identifierTable.containsKey(name)){
			if( identifierTable.get(name).getKind() == Kinds.VARIABLE){
				answer = true;
			}
		}
		return answer;
	}
	
	public boolean isFunctionName( String name){
		boolean answer = false;
		if(identifierTable.containsKey(name)){
			if( identifierTable.get(name).getKind() == Kinds.FUNCTION){
				answer = true;
			}
		}
		return answer;
	}
	
	public boolean isProgramName( String name){
		boolean answer = false;
		if(identifierTable.containsKey(name)){
			if( identifierTable.get(name).getKind() == Kinds.PROGRAM){
				answer = true;
			}
		}
		return answer;
	}
	
	public boolean isArrayName( String name){
		boolean answer = false;
		if(identifierTable.containsKey(name)){
			if( identifierTable.get(name).getKind() == Kinds.ARRAY){
				answer = true;
			}
		}
		return answer;
	}
	
	public Collection<Symbol> getSymbols(){
		return identifierTable.values();
	}
}
