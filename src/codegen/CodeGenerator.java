package codegen;

import java.util.ArrayList;
import java.util.HashMap;

import parser.ArgumentSymbol;
import parser.FunctionSymbol;
import parser.ProcedureSymbol;
import parser.Symbol;
import parser.SymbolTable;
import parser.VariableSymbol;
import scanner.Keywords;
import syntaxtree.AssignmentStatementNode;
import syntaxtree.CompoundStatementNode;
import syntaxtree.DeclarationsNode;
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

public class CodeGenerator {
	
	private ProgramNode root;
	private SymbolTable globalTable;
	private int elseCount = 0;
	private ArrayList<String> output = new ArrayList<String>();
	
	public CodeGenerator( ProgramNode program, SymbolTable table){
		this.root = program;
		this.globalTable = table;
	}
	
	public boolean compile(){
		boolean isSuccess = true;
		output.add("#Generated MIPS code for " + root.getName() + ".pas\n\n");
		output.add(".data\n__newLine: .asciiz \"\\n\"\n");
		
		for(Symbol symbol : globalTable.getSymbols()){
			if( symbol instanceof VariableSymbol){
				output.add(symbol.getIdentifier() + ": .word 0\n");
			}
		}
		
		output.add("\n.text\n");
		output.add("write:\naddi $sp, $sp, -4\nsw $ra, 0($sp)\nli $v0, 1\nsyscall\n"
				+ "li $v0, 4\nla $a0, __newLine\nsyscall\nlw $ra, 0($sp)\naddi $sp, $sp, 4\njr $ra\n");
		
		for(SubProgramNode sub : root.getFunctions().getProcs()){
			isSuccess = subProgramGenerator( sub);
		}
		
		output.add("main:\naddi $sp, $sp, -4\nsw $ra, 0($sp)\n");
		
		for(StatementNode statement : root.getMain().getStatements()){
			isSuccess = statementGenerator( statement);
		}
		
		output.add("lw $ra, 0($sp)\naddi $sp, $sp, 4\njr $ra\n");
		
		return isSuccess;
	}
	
	private boolean subProgramGenerator( SubProgramNode subProgram){
		boolean isSuccess = true;
		String returnLocation = null;
		String subId = subProgram.getName();
		ProcedureSymbol progSymbol = ((ProcedureSymbol)globalTable.getSymbol( subId));		
		HashMap<String, Symbol> localTable = progSymbol.getLocalSymbolTable();
		globalTable.pushTable(subId, localTable);
		output.add(subId + ":\n");
		
		for(Symbol symbol : globalTable.getSymbols()){
			System.out.println(symbol.toString());
		}
		
		int varCount = 0;
		int numOfArgs = subProgram.getNumOfArgs();
		for(Symbol symbol : globalTable.getSymbols()){ if(symbol instanceof VariableSymbol) varCount++;}
		output.add("addi $fp, $sp, " + (varCount * -4) + "\n");
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
		
		output.add("addi $sp, $fp, -4\nsw $ra, 0($sp)\n");
		
		for(StatementNode statement : subProgram.getMain().getStatements()){
			isSuccess = statementGenerator( statement);
		}
		
		if( subProgram.getSubType() == Keywords.FUNCTION){
			output.add("lw $t0, " + returnLocation + "\nadd $v0, $t0, $zero\n");
		}
		
		output.add("lw $ra, 0($sp)\naddi $sp, $sp, " + ((varCount + 1) * 4) + "\njr $ra\n");
		
		return isSuccess;
	}
	
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
	
	private boolean assignmentGenerator( AssignmentStatementNode assignment){
		boolean isSuccess = true;
		int t_val = 0;
		String varName = assignment.getLvalue().getName();
		String dataString = ((VariableSymbol) globalTable.getSymbol( varName)).getDataIdentifier();
		
		if( assignment.getExpression() instanceof ValueNode){			
			String val = ((ValueNode)assignment.getExpression()).getAttribute();
			output.add("li $t0, " + val + "\nsw $t0, " + dataString + "\n"); 
		}else{
			expressionGenerator( assignment.getExpression(), t_val);
			output.add("sw $t" + t_val + ", " + dataString + "\n");
		}	
		
		return isSuccess;
	}
	
	private int expressionGenerator( ExpressionNode expression, int t_val){
		
		if( expression instanceof OperationNode){
			t_val = operationGenerator( (OperationNode) expression, t_val);
		}
		
		if( expression instanceof FunctionNode){
			t_val = functionCallGeneratior( (FunctionNode) expression);
		}else if( expression instanceof VariableNode){
			String varId = ((VariableNode) expression).getName();
			String varName = ((VariableSymbol) globalTable.getSymbol( varId)).getDataIdentifier();
			output.add("lw $t" + t_val + ", " + varName + "\n");
		}
		
		if( expression instanceof ValueNode){
			output.add("li $t" + t_val + ", " + ((ValueNode) expression).getAttribute() + "\n");
		}
		
		return t_val;
	}
	
	private int operationGenerator( OperationNode operation, int t_val){
		int t_left = 0;
		int t_right = 0;
		
		if( operation.getLeft() instanceof OperationNode){
			t_left = operationGenerator( (OperationNode) operation.getLeft(), t_val);
		}else{
			t_left = expressionGenerator( operation.getLeft(), t_val);
		}
		
		if( operation.getRight() instanceof OperationNode){
			t_right = operationGenerator( (OperationNode) operation.getRight(), (t_val + 1));
		}else{
			t_right = expressionGenerator( operation.getRight(), (t_val + 1));
		}

		Keywords opType = operation.getOperation();
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
	
	private boolean ifGenerator( IfStatementNode statement){
		boolean isSuccess = true;
		int t_val = 0;
		
		elseCount = statement.getElseCount();
		if( statement.getTest() instanceof ValueNode || statement.getTest() instanceof VariableNode){
			t_val = expressionGenerator( statement.getTest(), t_val);
			output.add("blt $zero, $t" + t_val + ", Else" + elseCount + "\n");
		}else if( statement.getTest() instanceof OperationNode){
			t_val = operationGenerator( (OperationNode) statement.getTest(), t_val);
		}
		
		isSuccess = statementGenerator( statement.getThenStatement());
		output.add("jal End_IF" + elseCount + "\nElse" + elseCount + ":\n");
		isSuccess = statementGenerator( statement.getElseStatement());
		output.add("End_IF" + elseCount + ":\n");
		
		
		return isSuccess;
	}
	
	private boolean procedureCallGenerator( ProcedureStatementNode procedure){
		boolean isSuccess = true;
		int t_val = 0;
		int a_val = 0;
		
		for( ExpressionNode expression : procedure.getExpressions()){
			t_val = expressionGenerator( expression, t_val);
			output.add("add $a" + a_val + ", $zero, $t" + t_val + "\n");
			a_val++;
		}
		
		output.add("jal " + procedure.getLvalue().getName() + "\n");
		
		return isSuccess;
	}
	
	private int functionCallGeneratior( FunctionNode function){
		int t_val = 0;
		int a_val = 0;
		
		for( ExpressionNode expression : function.getExpNode()){
			t_val = expressionGenerator( expression, t_val);
			output.add("add $a" + a_val + ", $zero, $t" + t_val + "\n");
			a_val++;
		}
		
		output.add("jal " + function.getName() + "\nadd $t" + t_val + ", $v0, $zero\n");
		return t_val;
	}
	
	private boolean whileGenerator( WhileStatementNode statement){
		boolean isSuccess = true;
		int t_val = 0;
		
		elseCount = statement.getElseCount();
		output.add("Start_While" + elseCount + ":\n");
		if( statement.getTest() instanceof ValueNode || statement.getTest() instanceof VariableNode){
			t_val = expressionGenerator( statement.getTest(), t_val);
			output.add("blt $zero, $t" + t_val + ", Else" + elseCount + "\n");
		}else if( statement.getTest() instanceof OperationNode){
			t_val = operationGenerator( (OperationNode) statement.getTest(), t_val);
		}
		
		isSuccess = statementGenerator( statement.getThenStatement());
		output.add("jal Start_While" + elseCount + "\nElse" + elseCount + ":\n");
		
		return isSuccess;
	}
	
	public ArrayList<String> getOutput(){
		return output;
	}
}
