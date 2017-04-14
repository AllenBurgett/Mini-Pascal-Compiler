package parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

import scanner.Keywords;

public class SymbolTable {
	
	private HashMap<String, Symbol> identifierTable = new HashMap<String, Symbol>();
	private Stack<StackEntry> symbolTableStack = new Stack<StackEntry>();
	private int stackSize = 0;
	private int varCount = 0;
	
	public SymbolTable(){
		this.pushTable( "Program", identifierTable);
		this.add("write", Kinds.PROCEDURE, null, null, null);
		this.add("read", Kinds.PROCEDURE, null, null, null);
	}
	
	public boolean add( String identifier, Kinds kind, Keywords type, Integer start, Integer end){
		boolean answer = false;
		boolean error = false;
		HashMap<String, Symbol> currentTable = symbolTableStack.peek().getTable();
		if(! currentTable.containsKey(identifier)){
			switch( kind){
				case VARIABLE:
					if( stackSize > 1){
						String dataId = "" + (4 * varCount) + "($fp)";
						currentTable.put(identifier, new VariableSymbol( identifier, dataId, type));
					}else{
						currentTable.put(identifier, new VariableSymbol( identifier, identifier, type));
					}
					varCount++;
					break;
				case ARRAY:
					if( stackSize > 1){
						String dataId = "" + (4 * varCount) + "($fp)";
						currentTable.put(identifier, new VariableSymbol( identifier, dataId, type));
					}else{
						currentTable.put(identifier, new ArraySymbol( identifier, identifier, type, start, end));
					}
					break;
				case PROCEDURE:
					ProcedureSymbol procedure = new ProcedureSymbol( identifier);
					currentTable.put(identifier, procedure);
					this.pushTable( identifier, procedure.getLocalSymbolTable());					
					break;
				case FUNCTION:
					FunctionSymbol function = new FunctionSymbol( identifier, type);
					currentTable.put(identifier, function);
					this.pushTable( identifier, function.getLocalSymbolTable());
					break;
				default:
					error = true;
					break;
			}
			if(! error){
				answer = true;
			}
		}
		return answer;
	}
	
	public int pushTable(String id, HashMap<String, Symbol> table){	
		StackEntry entry = new StackEntry( id, table);
		symbolTableStack.push( entry);
		this.stackSize++;
		this.varCount = 0;
		return this.stackSize;
	}
	
	public int popTable(){
		StackEntry entry = symbolTableStack.pop();
		((ProcedureSymbol)symbolTableStack.lastElement().getTable().get( entry.getIdentifier())).storeTable(entry.getTable());
		stackSize--;
		return stackSize;
	}
	
	public boolean isVariableName( String name){
		boolean answer = false;
		for(int i = stackSize - 1; i >= 0; i--){
			if(symbolTableStack.elementAt(i).getTable().containsKey(name)){
				if( symbolTableStack.elementAt(i).getTable().get(name) instanceof VariableSymbol){
					answer = true;
				}
			}
		}
		return answer;
	}
	
	public boolean isFunctionName( String name){
		boolean answer = false;
		for(int i = stackSize - 1; i >= 0; i--){
			if(symbolTableStack.elementAt(i).getTable().containsKey(name)){
				if( symbolTableStack.elementAt(i).getTable().get(name) instanceof FunctionSymbol){
					answer = true;
				}
			}
		}
		return answer;
	}
	
	public boolean isProgramName( String name){
		boolean answer = false;
		if(symbolTableStack.firstElement().getTable().containsKey(name)){
			if( symbolTableStack.firstElement().getTable().get(name) instanceof ProgramSymbol){
				answer = true;
			}
		}
		return answer;
	}
	
	public boolean isArrayName( String name){
		boolean answer = false;
		for(int i = stackSize - 1; i >= 0; i--){
			if(symbolTableStack.elementAt(i).getTable().containsKey(name)){
				if( symbolTableStack.elementAt(i).getTable().get(name) instanceof ArraySymbol){
					answer = true;
				}
			}
		}
		return answer;
	}
	
	public boolean isProcedureName( String name){
		boolean answer = false;
		for(int i = stackSize - 1; i >= 0; i--){
			if(symbolTableStack.elementAt(i).getTable().containsKey(name)){
				if( symbolTableStack.elementAt(i).getTable().get(name) instanceof ProcedureSymbol){
					answer = true;
				}
			}
		}
		return answer;
	}
	
	public Keywords getType( String id){
		for( int i = stackSize - 1; i >= 0; i--){
			if( symbolTableStack.elementAt(i).getTable().containsKey(id)){
				Symbol symbol = symbolTableStack.elementAt(i).getTable().get( id);
				if( symbol instanceof VariableSymbol){
					return ((VariableSymbol) symbol).getType(); 
				}else if( symbol instanceof ArraySymbol){
					return ((ArraySymbol) symbol).getType();
				}else if( symbol instanceof FunctionSymbol){
					return ((FunctionSymbol) symbol).getType();
				}
			}
		}
		return null;
	}
	
	public Collection<Symbol> getSymbols(){
		return identifierTable.values();
	}
	
	public Symbol getSymbol( String id){
		return identifierTable.get(id);
	}
}
