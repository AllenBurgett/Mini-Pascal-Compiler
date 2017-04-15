package codegen;

import java.util.ArrayList;
import java.util.HashMap;

import parser.ArgumentSymbol;
import parser.ProcedureSymbol;
import parser.Symbol;
import parser.SymbolTable;
import parser.VariableSymbol;
import scanner.Keywords;
import syntaxtree.AssignmentStatementNode;
import syntaxtree.CompoundStatementNode;
import syntaxtree.ExpressionNode;
import syntaxtree.FunctionNode;
import syntaxtree.IfStatementNode;
import syntaxtree.OperationNode;
import syntaxtree.ProcedureStatementNode;
import syntaxtree.ProgramNode;
import syntaxtree.StatementNode;
import syntaxtree.SubProgramNode;
import syntaxtree.ValueNode;
import syntaxtree.VariableNode;
import syntaxtree.WhileStatementNode;

/**
 * This class handles the conversion of the 
 * Program tree in to MIPS assembly.
 * @author Allen Burgett
 *
 */
public class CodeGenerator {
	
	private ProgramNode root;
	private SymbolTable globalTable;
	private int elseCount = 0;
	//This is the output that will be printed to the .asm file.
	private ArrayList<String> output = new ArrayList<String>();
	/**
	 * Takes in a ProgramNode as the root of the tree. The Generator
	 * references the symbol table directly for information about the
	 * declarations.
	 * @param program, a program represented as a tree.
	 * @param table, the global symbol table, containing all declared symbols.
	 */
	public CodeGenerator( ProgramNode program, SymbolTable table){
		this.root = program;
		this.globalTable = table;
	}
	
	/**
	 * Attempts to compile the tree to MIPS.
	 * @return true if the compile was successful.
	 */
	public boolean compile(){
		boolean isSuccess = true;
		output.add("#Generated MIPS code for " + root.getName() + ".pas\n\n");
		//start the data section.
		//This is where global variables are declared.
		//__newline is declared to assist with the write procedure.
		output.add(".data\n__newLine: .asciiz \"\\n\"\n");
		
		//At this point, the only table on the stack is the global table.
		//Thus, we can declare these variables as globals and they can be 
		//used by all functions and procedures.
		for(Symbol symbol : globalTable.getSymbols()){
			if( symbol instanceof VariableSymbol){
				output.add(symbol.getIdentifier() + ": .word 0\n");
			}
		}
		
		//start of the program logic.
		output.add("\n.text\n");
		//declare a write procedure that takes a single argument and prints
		//it to the console followed by a new line, then returns to the point
		//at which it was called.
		output.add("write:\naddi $sp, $sp, -4\nsw $ra, 0($sp)\nli $v0, 1\nsyscall\n"
				+ "li $v0, 4\nla $a0, __newLine\nsyscall\nlw $ra, 0($sp)\naddi $sp, $sp, 4\njr $ra\n");
		
		//processes the sub program code for all declared subs.
		for(SubProgramNode sub : root.getFunctions().getProcs()){
			isSuccess = subProgramGenerator( sub);
		}
		
		//start of the main function.
		output.add("main:\naddi $sp, $sp, -4\nsw $ra, 0($sp)\n");
		
		//processes all the statements found in main.
		for(StatementNode statement : root.getMain().getStatements()){
			isSuccess = statementGenerator( statement);
		}
		
		//end of the main function.
		output.add("lw $ra, 0($sp)\naddi $sp, $sp, 4\njr $ra\n");
		
		return isSuccess;
	}
	
	//generates code for a single sub program.
	private boolean subProgramGenerator( SubProgramNode subProgram){
		boolean isSuccess = true;
		String returnLocation = null; //used to keep track of the stack address of the return value.
		String subId = subProgram.getName();
		//creates an instance of the procedure to use to get information about the procedure.
		ProcedureSymbol progSymbol = ((ProcedureSymbol)globalTable.getSymbol( subId));		
		HashMap<String, Symbol> localTable = progSymbol.getLocalSymbolTable();
		//push the current table on to the stack.
		globalTable.pushTable(subId, localTable);
		//sub program label.
		output.add(subId + ":\n");
		
		//checks the number of declared vars in this sub program, so the correct offset can
		//be used with the frame pointer.
		int varCount = 0;
		for(Symbol symbol : globalTable.getSymbols()){ if(symbol instanceof VariableSymbol) varCount++;}
		//adds offsets the frame pointer from the stack, to make room for the local variables.
		output.add("addi $fp, $sp, " + (varCount * -4) + "\n");
		//Initializes the arguments and local variables to their place in the stack.
		for(Symbol symbol : globalTable.getSymbols()){
			if( symbol instanceof ArgumentSymbol){
				int argNum = ((ArgumentSymbol)symbol).getArgNum();
				String dataString = ((ArgumentSymbol)symbol).getDataIdentifier();
				output.add("sw $a" + argNum + ", " + dataString + "\n");
			}else if( symbol instanceof VariableSymbol){
				String dataString = ((VariableSymbol) symbol).getDataIdentifier();
				output.add("li $t0, 0\nsw $t0, " + dataString + "\n");
				if( symbol.getIdentifier().equals(subId)){
					returnLocation = dataString;
				}
			}
		}
		
		//Offsets the stack one value over the frame pointer and stores the return address
		//to the top of the stack.
		output.add("addi $sp, $fp, -4\nsw $ra, 0($sp)\n");
		
		//generates the code for all statements in the sub.
		for(StatementNode statement : subProgram.getMain().getStatements()){
			isSuccess = statementGenerator( statement);
		}
		
		//if the sub is a function, then the data in the return address is stored in $v0.
		if( subProgram.getSubType() == Keywords.FUNCTION){
			output.add("lw $t0, " + returnLocation + "\nadd $v0, $t0, $zero\n");
		}
		
		//loads the return address and moves the stack pointer back to it's pre-call 
		//position. Then jumps back to point the sub was called from.
		output.add("lw $ra, 0($sp)\naddi $sp, $sp, " + ((varCount + 1) * 4) + "\njr $ra\n");
		
		return isSuccess;
	}
	
	//handles all routing for statement generation.
	private boolean statementGenerator( StatementNode statement){
		boolean isSuccess = true;
		
		if(statement instanceof AssignmentStatementNode){
			isSuccess = assignmentGenerator( (AssignmentStatementNode) statement);
		}else if( statement instanceof CompoundStatementNode){
			for( StatementNode currentStatement : ((CompoundStatementNode) statement).getStatements()){
				isSuccess = statementGenerator( currentStatement);
			}
		}else if( statement instanceof IfStatementNode){
			isSuccess = ifGenerator( (IfStatementNode) statement);
		}else if( statement instanceof ProcedureStatementNode){
			isSuccess = procedureCallGenerator( (ProcedureStatementNode) statement);
		}else if( statement instanceof WhileStatementNode){
			isSuccess = whileGenerator( (WhileStatementNode) statement);
		}
		
		return isSuccess;
	}
	
	//handles assignment generation
	private boolean assignmentGenerator( AssignmentStatementNode assignment){
		boolean isSuccess = true;
		int t_val = 0; //the assignment starts at the first t register.
		String varName = assignment.getLvalue().getName();
		//location of the variable that is being assigned.
		String dataString = ((VariableSymbol) globalTable.getSymbol( varName)).getDataIdentifier();
		
		//case the expression is already a value.
		if( assignment.getExpression() instanceof ValueNode){			
			String val = ((ValueNode)assignment.getExpression()).getAttribute();
			//assign the value to the variable.
			output.add("li $t0, " + val + "\nsw $t0, " + dataString + "\n");
		//case the value needs to be evaluated.
		}else{
			//generates the code to evaluate the expression.
			expressionGenerator( assignment.getExpression(), t_val);
			//assigns the result to the variable.
			output.add("sw $t" + t_val + ", " + dataString + "\n");
		}	
		
		return isSuccess;
	}
	
	//handles expression evaluation code generation.
	private int expressionGenerator( ExpressionNode expression, int t_val){
		//operation and function return a t_val where their result was stored.
		
		//case the expression is an operation
		if( expression instanceof OperationNode){
			t_val = operationGenerator( (OperationNode) expression, t_val);
		}
		
		//case the expression is a function
		if( expression instanceof FunctionNode){
			t_val = functionCallGeneratior( (FunctionNode) expression);
		//case the expression is a Variable.
		}else if( expression instanceof VariableNode){
			String varId = ((VariableNode) expression).getName();
			String varName = ((VariableSymbol) globalTable.getSymbol( varId)).getDataIdentifier();
			//loads the current value of the variable from memory or the stack.
			output.add("lw $t" + t_val + ", " + varName + "\n");
		}
		
		//case the expression is a value.
		if( expression instanceof ValueNode){
			//sets the current t register to the expressed value.
			output.add("li $t" + t_val + ", " + ((ValueNode) expression).getAttribute() + "\n");
		}
		
		return t_val;
	}
	
	//handles code generation of operations.
	private int operationGenerator( OperationNode operation, int t_val){
		int t_left = 0; //t register that the result of the left expression will be put in.
		int t_right = 0;//t register that the result of the right expression will be put in.
		
		//case the left expression is an operation.
		//the t register starts at the passed value of t_val
		if( operation.getLeft() instanceof OperationNode){
			t_left = operationGenerator( (OperationNode) operation.getLeft(), t_val);
		}else{
			t_left = expressionGenerator( operation.getLeft(), t_val);
		}
		
		//case the right expression is an operation.
		//the t register starts at the passed value of t_val + 1.
		if( operation.getRight() instanceof OperationNode){
			t_right = operationGenerator( (OperationNode) operation.getRight(), (t_val + 1));
		}else{
			t_right = expressionGenerator( operation.getRight(), (t_val + 1));
		}

		//builds the appropriate assembly code to evaluate the expression.
		//note that the relational operation use the opposite pseudo commands.
		Keywords opType = operation.getOperation();
		//case is a mulop. These are outliers in the fact that their results need to be moved
		//from a low register. 
		if( opType == Keywords.DIVIDE || opType == Keywords.DIV || opType == Keywords.TIMES){
			if( opType == Keywords.DIVIDE || opType == Keywords.DIV){
				output.add("div $t" + t_left + ", $t" + t_right + "\n");
			}else if( opType == Keywords.TIMES){
				output.add("mult $t" + t_left + ", $t" + t_right + "\n");
			}			
			output.add("mflo $t" + t_val + "\n");
		}else if( opType == Keywords.PLUS){
			output.add("add $t" + t_val + ", $t" + t_left + ", $t" + t_right + "\n");
		}else if( opType == Keywords.MINUS){
			output.add("sub $t" + t_val + ", $t" + t_left + ", $t" + t_right + "\n");
		//case modulus, the result is stored in the high register after division.
		}else if( opType == Keywords.MOD){
			output.add("div $t" + t_left + ", $t" + t_right + "\nmfhi $t" + (t_right + 1) + "\n");
		}else if( opType == Keywords.LESS_THAN){
			output.add("bge $t" + t_left + ", $t" + t_right + ", Else" + elseCount + "\n");
		}else if( opType == Keywords.EQUALITY_OPERATOR){
			output.add("bne $t" + t_left + ", $t" + t_right + ", Else" + elseCount + "\n");
		}else if( opType == Keywords.NOT_EQUAL){
			output.add("beq $t" + t_left + ", $t" + t_right + ", Else" + elseCount + "\n");
		}else if( opType == Keywords.LESS_THAN_EQUAL_TO){
			output.add("bgt $t" + t_left + ", $t" + t_right + ", Else" + elseCount + "\n");
		}else if( opType == Keywords.GREATER_THAN){
			output.add("ble $t" + t_left + ", $t" + t_right + ", Else" + elseCount + "\n");
		}else if( opType == Keywords.GREATER_THAN_EQUAL_TO){
			output.add("blt $t" + t_left + ", $t" + t_right + ", Else" + elseCount + "\n");
		}
		
		return t_val;
	}
	
	//handles code generation of if statements.
	private boolean ifGenerator( IfStatementNode statement){
		boolean isSuccess = true;
		int t_val = 0;
		
		elseCount = statement.getElseCount(); //used to label the jump on a false evaluation.
		//In Mini-Pascal, a value less than 0 is false
		if( statement.getTest() instanceof ValueNode || statement.getTest() instanceof VariableNode){
			t_val = expressionGenerator( statement.getTest(), t_val);
			output.add("blt $zero, $t" + t_val + ", Else" + elseCount + "\n");
		//case operation for branching.
		}else if( statement.getTest() instanceof OperationNode){
			t_val = operationGenerator( (OperationNode) statement.getTest(), t_val);
		}
		
		//generate the true statement
		isSuccess = statementGenerator( statement.getThenStatement());
		//jump past the else case
		output.add("jal End_IF" + elseCount + "\nElse" + elseCount + ":\n");
		//generate the false statement
		isSuccess = statementGenerator( statement.getElseStatement());
		//label for the end of the if-block.
		output.add("End_IF" + elseCount + ":\n");
		
		
		return isSuccess;
	}
	
	//handles the code generation for a procedure call.
	private boolean procedureCallGenerator( ProcedureStatementNode procedure){
		boolean isSuccess = true;
		int t_val = 0;
		int a_val = 0; //holds the a register label.
		
		//generates code to pass the value of each argument into the a registers.
		for( ExpressionNode expression : procedure.getExpressions()){
			t_val = expressionGenerator( expression, t_val);
			output.add("add $a" + a_val + ", $zero, $t" + t_val + "\n");
			a_val++;
		}
		
		//jump to the procedure label.
		output.add("jal " + procedure.getLvalue().getName() + "\n");
		
		return isSuccess;
	}
	
	//handles the function call code generation and returns the t register that the result was stored in.
	private int functionCallGeneratior( FunctionNode function){
		int t_val = 0;
		int a_val = 0; //holds the a register label.
		
		//generates code to pass the value of each argument into the a registers.
		for( ExpressionNode expression : function.getExpNode()){
			t_val = expressionGenerator( expression, t_val);
			output.add("add $a" + a_val + ", $zero, $t" + t_val + "\n");
			a_val++;
		}
		
		//jump to the function label and place the result of the function in the specified t register.
		output.add("jal " + function.getName() + "\nadd $t" + t_val + ", $v0, $zero\n");
		return t_val;
	}
	
	//handles the code generation for a while loop.
	private boolean whileGenerator( WhileStatementNode statement){
		boolean isSuccess = true;
		int t_val = 0;
		
		//similarly to the if statement, the else count is used to label the jump on false.
		elseCount = statement.getElseCount();
		//while start label, used to jump back to the beginning of the loop.
		output.add("Start_While" + elseCount + ":\n");
		//In Mini-Pascal, a value less than 0 is false
		if( statement.getTest() instanceof ValueNode || statement.getTest() instanceof VariableNode){
			t_val = expressionGenerator( statement.getTest(), t_val);
			output.add("blt $zero, $t" + t_val + ", Else" + elseCount + "\n");
		//case branching operation.
		}else if( statement.getTest() instanceof OperationNode){
			t_val = operationGenerator( (OperationNode) statement.getTest(), t_val);
		}
		
		//the statement that will be done until the while test evaluates false.
		isSuccess = statementGenerator( statement.getThenStatement());
		//jump to the start of the loop and label the end of the while loop.
		output.add("jal Start_While" + elseCount + "\nElse" + elseCount + ":\n");
		
		return isSuccess;
	}
	
	/**
	 * Output contains a list of strings, with \n already placed. This can be
	 * printed as is and will represent the MIPS representation of the Program
	 * that was passed.
	 * @return an ArrayList of strings that represent the MIPS code for the original program.
	 */
	public ArrayList<String> getOutput(){
		return output;
	}
}
