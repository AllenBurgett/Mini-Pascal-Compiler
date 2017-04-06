package parser;

import java.util.HashMap;

public class ProcedureSymbol extends Symbol {
	
	private HashMap<String, Symbol> localSymbolTable = new HashMap<String, Symbol>();

	public ProcedureSymbol( String identifier){
		super( identifier);
	}
	
	public HashMap<String, Symbol> getLocalSymbolTable(){
		return this.localSymbolTable;
	}
	
	public void storeTable( HashMap<String, Symbol> table){
		this.localSymbolTable = table;
	}
	
	@Override
	public String toString(){
		return super.identifier + " PROCEDURE ";
	}
}
