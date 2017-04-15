package parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

import scanner.Keywords;

/**
 * Contains all symbols defined in the program and consolidates the logic to a single class.
 * @author meltd
 *
 */
public class SymbolTable {
	
	private HashMap<String, Symbol> identifierTable = new HashMap<String, Symbol>();
	private Stack<StackEntry> symbolTableStack = new Stack<StackEntry>();
	private int stackSize = 0;
	private int varCount = 0;
	private int argCount = 0;
	
	/**
	 * Initializes a SymbolTable. Upon initialization, a "global table" is pushed on to the stack.
	 * Adds the pre-defined procedures write and read to the global table.
	 */
	public SymbolTable(){
		this.pushTable( "Program", identifierTable);
		this.add("write", Kinds.PROCEDURE, null, null, null);
		this.popTable();
		this.add("read", Kinds.PROCEDURE, null, null, null);
		this.popTable();
	}
	
	/**
	 * Allows for the addition of any symbol to the current table. Note, this will add to the table
	 * at the top of the stack.
	 * @param identifier, name of the symbol being added.
	 * @param kind, kind of symbol as defined by the Kinds enum.
	 * @param type, Integer or Real.
	 * @param start, start index of an array symbol.
	 * @param end, end indec of an array symbol.
	 * @return whether the symbol was added. If false, the symbol already exists in the current table.
	 */
	public boolean add( String identifier, Kinds kind, Keywords type, Integer start, Integer end){
		boolean answer = false;
		boolean error = false;
		//current table is the table at the top of the stack.
		HashMap<String, Symbol> currentTable = symbolTableStack.peek().getTable();
		//If the identifier does not already exist in the current table.
		if(! currentTable.containsKey(identifier)){
			switch( kind){
				case VARIABLE:
					//if this is not the global table, the dataIdentifier will be set to a unique point on the stack.
					if( stackSize > 1){
						String dataId = "" + (4 * varCount) + "($fp)";
						currentTable.put(identifier, new VariableSymbol( identifier, dataId, type));
						varCount++;
					}else{//if it is the global table, the dataIdentifer is the name of the variable.
						currentTable.put(identifier, new VariableSymbol( identifier, identifier, type));
					}
					break;
				case ARGUMENT:
					//if this is not the global table, the dataIdentifier will be set to a unique point on the stack.
					if( stackSize > 1){
						String dataId = "" + (4 * varCount) + "($fp)";
						currentTable.put(identifier, new ArgumentSymbol( identifier, dataId, type, argCount));
						varCount++;
						argCount++;
					}else{//if this is the global table, this has been invoked in error.
						error = true;
					}
					break;
				case ARRAY:
					//if this is not the global table, the dataIdentifier will be set to a unique point on the stack.
					if( stackSize > 1){
						String dataId = "" + (4 * varCount) + "($fp)";
						currentTable.put(identifier, new VariableSymbol( identifier, dataId, type));
					}else{//if it is the global table, the dataIdentifer is the name of the array.
						currentTable.put(identifier, new ArraySymbol( identifier, identifier, type, start, end));
					}
					break;
				case PROCEDURE:
					ProcedureSymbol procedure = new ProcedureSymbol( identifier);
					currentTable.put(identifier, procedure);
					//pushes the procedure's table on to the stack.
					this.pushTable( identifier, procedure.getLocalSymbolTable());
					argCount = 0;
					varCount = 0;
					break;
				case FUNCTION:
					FunctionSymbol function = new FunctionSymbol( identifier);
					currentTable.put(identifier, function);
					//pushes the functions's table on to the stack.
					this.pushTable( identifier, function.getLocalSymbolTable());
					argCount = 0;
					varCount = 0;
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
	
	/**
	 * Sets a return type to the specified function.
	 * @param identifier, name of function.
	 * @param type, Integer or Real.
	 * @return returns true if current symbol table already contains the identifier.
	 */
	public boolean addFunctionReturn( String identifier, Keywords type){
		boolean answer = false;
		HashMap<String, Symbol> currentTable = symbolTableStack.peek().getTable();
		if(! currentTable.containsKey(identifier)){
			this.add(identifier, Kinds.VARIABLE, type, null, null);
		}else{
			answer = true;
		}
		
		return answer;
	}
	
	/**
	 * Used to push a procedure's table on to the stack.
	 * @param id, name of the procedure.
	 * @param table, procedure's associated table.
	 * @return the size of the stack after the push.
	 */
	public int pushTable(String id, HashMap<String, Symbol> table){	
		StackEntry entry = new StackEntry( id, table);
		symbolTableStack.push( entry);
		this.stackSize++;
		this.varCount = 0;
		return this.stackSize;
	}
	
	/**
	 * Pops the top table off of the stack.
	 * @return the size of the stack after the pop.
	 */
	public int popTable(){
		//Only pops if the stack has more than just the global table.
		if( stackSize > 1){
			StackEntry entry = symbolTableStack.pop();
			String subId = entry.getIdentifier();
			//store the popped table to the appropriate procedure.
			((ProcedureSymbol)symbolTableStack.peek().getTable().get( subId)).storeTable( entry.getTable());
			stackSize--;
		}
		return stackSize;
	}
	
	/**
	 * Checks if the variable exists anywhere in the stack and is a variable.
	 * @param name, variable name.
	 * @return true if the variable has been declared.
	 */
	public boolean isVariableName( String name){
		boolean answer = false;
		//iterates down the stack.
		for(int i = stackSize - 1; i >= 0; i--){
			if(symbolTableStack.elementAt(i).getTable().containsKey(name)){
				if( symbolTableStack.elementAt(i).getTable().get(name) instanceof VariableSymbol){
					answer = true;
				}
			}
		}
		return answer;
	}
	
	/**
	 * Checks if the function exists anywhere in the stack and is a function.
	 * @param name, function name.
	 * @return true if the function has been declared.
	 */
	public boolean isFunctionName( String name){
		boolean answer = false;
		//iterates down the stack.
		for(int i = stackSize - 1; i >= 0; i--){
			if(symbolTableStack.elementAt(i).getTable().containsKey(name)){
				if( symbolTableStack.elementAt(i).getTable().get(name) instanceof FunctionSymbol){
					answer = true;
				}
			}
		}
		return answer;
	}
	
	/**
	 * Checks if the id is the name of the program.
	 * @param name, program name.
	 * @return true if it is the name of the program.
	 */
	public boolean isProgramName( String name){
		boolean answer = false;
		//checks only the global table, where program would be declared.
		if(symbolTableStack.firstElement().getTable().containsKey(name)){
			if( symbolTableStack.firstElement().getTable().get(name) instanceof ProgramSymbol){
				answer = true;
			}
		}
		return answer;
	}
	
	/**
	 * Checks if the array exists anywhere in the stack and is a array.
	 * @param name, array name.
	 * @return true if the array has been declared.
	 */
	public boolean isArrayName( String name){
		boolean answer = false;
		//iterates down the stack.
		for(int i = stackSize - 1; i >= 0; i--){
			if(symbolTableStack.elementAt(i).getTable().containsKey(name)){
				if( symbolTableStack.elementAt(i).getTable().get(name) instanceof ArraySymbol){
					answer = true;
				}
			}
		}
		return answer;
	}
	
	/**
	 * Checks if the procedure exists anywhere in the stack and is a procedure.
	 * @param name, procedure name.
	 * @return true if the procedure has been declared.
	 */
	public boolean isProcedureName( String name){
		boolean answer = false;
		//iterates down the stack.
		for(int i = stackSize - 1; i >= 0; i--){
			if(symbolTableStack.elementAt(i).getTable().containsKey(name)){
				if( symbolTableStack.elementAt(i).getTable().get(name) instanceof ProcedureSymbol){
					answer = true;
				}
			}
		}
		return answer;
	}
	
	/**
	 * Get the type of the most local symbol of this id.
	 * @param id, name of symbol.
	 * @return the type of symbol or null if the symbol does not exist.
	 */
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
	
	/**
	 * Get a collection of the symbols defined in the current table.
	 * @return a collect of symbols from the current table.
	 */
	public Collection<Symbol> getSymbols(){
		return symbolTableStack.peek().getTable().values();
	}
	
	/**
	 * Get a symbol from the first table it's declared in on the stack.
	 * @param id, name of symbol.
	 * @return the most local symbol of that name or null if the symbol has not been declared.
	 */
	public Symbol getSymbol( String id){
		for( int i = stackSize - 1; i >= 0; i--){
			HashMap<String, Symbol> currentTable = symbolTableStack.elementAt(i).getTable();
			if( currentTable.containsKey( id)){
				return currentTable.get( id);
			}
		}
		return null;
	}
}
