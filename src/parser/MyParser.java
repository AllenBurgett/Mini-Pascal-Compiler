package parser;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import analysis.SemanticAnalyzer;
import scanner.MyScanner;
import scanner.Token;
import syntaxtree.ArrayNode;
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
import syntaxtree.SubProgramDeclarationsNode;
import syntaxtree.SubProgramNode;
import syntaxtree.UnaryOperationNode;
import syntaxtree.ValueNode;
import syntaxtree.VariableNode;
import syntaxtree.WhileStatementNode;
import scanner.Keywords;


/**
 * The parser recognizes whether an input string of tokens
 * is a valid Mini-Pascal program. During this process a
 * syntax tree is formed for later traversal. 
 * To use a parser, create an instance pointing at a file,
 * and then call the top-level function, <code>program()</code>.
 * If the functions returns true, the file is a valid Mini_Pascal
 * Program that is ready to be compiled.
 * @author Allen Burgett
 */
public class MyParser {
    
    ///////////////////////////////
    //    Instance Variables
    ///////////////////////////////
    
    private Token lookahead; 		//used to view the next token, without consuming it.
    
    private MyScanner scanner; 		//used to scan for tokens in the given string.
    
    public SymbolTable symbolTable; //used to hold symbols found while building the syntax tree.
    
    public ProgramNode prog;
    
    private boolean noError = true; //used to indicate a successful parsing.
    private int elseCount = 0;		//used to track else statements for code generation.
    
    ///////////////////////////////
    //       Constructors
    ///////////////////////////////
    
    /**
     * Initializes a Parser. The input can be read as a 
     * stream of text or from a file.
     * @param text, the string to be parsed.
     * @param isFilename, if the string is a file name, pass
     * true.
     */
    public MyParser( String text, boolean isFilename) {
    	//if a file is given, the file is broken down into a
    	//file input stream, then passed to the scanner.
        if( isFilename) {
	        FileInputStream fis = null;
	        try {
	            fis = new FileInputStream(text);
	        } catch (FileNotFoundException ex) {
	            error( "No file");
	        }
	        InputStreamReader isr = new InputStreamReader( fis);
	        scanner = new MyScanner( isr);
	                 
        }
        else {//otherwise, the string is parsed as is.
            scanner = new MyScanner( new StringReader( text));
	        }
        try {
            lookahead = scanner.nextToken();
        } catch (IOException ex) {
            error( "Scan error");
        }
        
        symbolTable = new SymbolTable();
    
    }
    
    ///////////////////////////////
    //       Methods
    ///////////////////////////////
    
    
    /**
     * Checks to see if the given text is a Mini-Pascal program. During this check a syntax
     * tree is built and can be used to generate assembly code.
     * @return true if the given text is valid Mini_Pascal code.
     */
    public boolean program(){
    	if( lookahead.getType() == Keywords.PROGRAM){
    		match( Keywords.PROGRAM);
    		
    		if( lookahead.getType() == Keywords.ID){
    			//adds the program name to the symbol table.
    			symbolTable.add(lookahead.getLexeme(), Kinds.PROGRAM, null, null, null);
    			//starts the program syntax tree.
    			prog = new ProgramNode(lookahead.getLexeme());
    			match( Keywords.ID);
    		}
    		else{
    			error( " expected Program name");
    		}
    		
    		match( Keywords.SEMI_COLON);
    		//checks variable declarations.
	    	prog.setVariables( declarations());
	    	//checks sub program declarations.
	    	prog.setFunctions( subprogram_declarations());
	    	//checks all statements contained in the program's 
	    	prog.setMain( compound_statement());
	    	match( Keywords.PERIOD);
    	}
    	else{
    		error( "program");
    	}
    	
    	return noError;
    }
    
    //returns a list of identifier strings (ie. variable and array names).
    //used to bulk declare VariableNodes in declaration sections. Calls
    //itself recursively until all IDs are matched.
    private ArrayList<String> identifier_list() {
    	ArrayList<String> identifierList = new ArrayList<String>();
    	identifierList.add( lookahead.getLexeme());
		match( Keywords.ID);
		
		if( lookahead.getType() == Keywords.COMMA){
			match( Keywords.COMMA);
			identifierList.addAll( identifier_list());
		}
		
		return identifierList;
	}

    //builds a list of identifiers and gives them at type, Integer or Real.
	private DeclarationsNode declarations() {
		DeclarationsNode decs = new DeclarationsNode();
    	if( lookahead.getType() == Keywords.VAR){
	    	match( Keywords.VAR);
	    	//builds a list of strings of declared variable IDs.
	    	ArrayList<String> identifierList = identifier_list();
	    	match( Keywords.COLON);
	    	
	    	//Assigns the IDs types and builds a list of VariableNodes
	    	//passes false because these are not arguments.
	    	decs.addAllVariables( type(identifierList, false));
	    	
	    	
	    	match( Keywords.SEMI_COLON);
	    	//calls itself until there are no more variables to be declared.
	    	decs.addAllVariables( declarations().getVars());
    	}
    	else{
    		//lambda case
    	}
    	
    	return decs;
		
	}

	//Assigns a type to a list of strings. Pass true to this is the strings are arguments.
	//Returns a list of VariableNodes.
	private ArrayList<VariableNode> type(ArrayList<String> identifierList, boolean isArgument) {
		Integer arrayStart = null;
		Integer arrayEnd = null;
		Kinds kind = null;
		Keywords varType = null; 		
		
		//array case. Handles declaration grammar for an array.
		if( lookahead.getType() == Keywords.ARRAY){
			
			match( Keywords.ARRAY);
			match( Keywords.LEFT_SQUARE_BRACKET);
			//start index
			if( lookahead.getType() == Keywords.NUMBER){
				arrayStart = Integer.parseInt( lookahead.getLexeme());
				match( Keywords.NUMBER);
			}
			else{
				error( " expected array index");
			}
			
			match( Keywords.COLON);
			
			//end index
			if( lookahead.getType() == Keywords.NUMBER){
				arrayEnd = Integer.parseInt( lookahead.getLexeme());
				match( Keywords.NUMBER);
			}
			else{
				error( " expected array index");
			}
			
			match( Keywords.RIGHT_SQUARE_BRACKET);
			match( Keywords.OF);
			kind = Kinds.ARRAY;
			//type Integer or Real.
			varType = standard_type();
		}
		//variable case
		else if( lookahead.getType() == Keywords.INTEGER || lookahead.getType() == Keywords.REAL){
			kind = Kinds.VARIABLE;
			//type Integer or Real.
			varType = standard_type();
		}
		else{
			error( "Expected var type");
		}
		
		//used to hold variables/arrays after type assignment.
		ArrayList<VariableNode> declaredVars = new ArrayList<VariableNode>();
		
		//iterates through the IDs and builds VariableNodes
    	for(String id : identifierList){
    		boolean success = false; //used to check if the id has been declared in this symbol table.
    		//argument case. The variable will be assigned a stack position to be used during code generation.
    		if( isArgument){
    			success = symbolTable.add(id, Kinds.ARGUMENT, varType, arrayStart, arrayEnd);
    		}else{ //normal declaration. The variable is assigned it's name during code generation.
    			success = symbolTable.add(id, kind, varType, arrayStart, arrayEnd);
    		}
    		
    		//if none of the IDs have been used before, the VariableNodes are built and added to the declaredVars
    		if( success){
    			if(kind.equals(Kinds.VARIABLE)){ //is Variable case
    				declaredVars.add(new VariableNode( id, varType));
    			}else if(kind.equals(Kinds.ARRAY)){ //is Array case
    				declaredVars.add(new ArrayNode( id, varType));
    			}
    		}else{
    			error( id + " already declared");
    		}
    	}
    	
    	return declaredVars;
	}

	//returns the keyword match of Integer or Real.
	private Keywords standard_type() {
		Keywords standardType = null;
		
		switch( lookahead.getType()){
			case INTEGER:
				standardType = Keywords.INTEGER;
				match( Keywords.INTEGER);
				break;
			case REAL:
				standardType = Keywords.REAL;
				match( Keywords.REAL);
				break;
			default:
				error( "Type expected");
				break;
				
		}
		return standardType;
		
	}

	//handles sub program declarations
	private SubProgramDeclarationsNode subprogram_declarations() {
		SubProgramDeclarationsNode subProgs = new SubProgramDeclarationsNode();
		if( lookahead.getType() == Keywords.FUNCTION || lookahead.getType() == Keywords.PROCEDURE){
			//adds the current sub program.
			subProgs.addSubProgramDeclaration( subprogram_declaration());
			//calls itself until all sub programs are accounted for.
			if( lookahead.getType() == Keywords.SEMI_COLON){
				match( Keywords.SEMI_COLON);		
				subProgs.addAllSubProgramDeclarations( subprogram_declarations().getProcs());
			}
		}
		else{
			//lambda case
		}
		
		return subProgs;		
	}

	//declares a single subprogram
	private SubProgramNode subprogram_declaration() {
		//handles the prototype declaration
		SubProgramNode sub = subprogram_head();
		//handles all variables declared inside the sub program
		sub.setVariables( declarations());
		//handles sub programs declared inside the sub program
		sub.setFunctions( subprogram_declarations());
		//handles all statements declared inside the sub program
		sub.setMain( compound_statement());
		//stores the local symbol tables back to the Procedure or Function Symbol
		symbolTable.popTable();
		//if subprogram is a function it can be used as a variable, this block assigns a type, Integer or Real.
		if( sub.getSubType() == Keywords.FUNCTION){
			((FunctionSymbol)symbolTable.getSymbol( sub.getName())).setType(sub.getReturnType());
		}
		return sub;
	}

	//checks the syntax of the prototype and builds the subprogram node.
	private SubProgramNode subprogram_head() {
		SubProgramNode sub = null;
		ArrayList<VariableNode> args = null;
		
		//function case
		if( lookahead.getType() == Keywords.FUNCTION){
			match( Keywords.FUNCTION);
			if( lookahead.getType() == Keywords.ID){
				sub = new SubProgramNode( lookahead.getLexeme(), Keywords.FUNCTION);
				match( Keywords.ID);
			}
			//adds the function to the symbol table. This also pushes a fresh symbol table on to the stack.
			symbolTable.add(sub.getName(), Kinds.FUNCTION, null, null, null);
			//builds the argument variables.
			args = arguments();
			//adds the arguments to the SubProgramNode
			sub.setArguments( args);
			match( Keywords.COLON);
			//matches the return type, Integer or Real.
			Keywords returnType = standard_type();
			sub.setReturnType( returnType);
			//Add the function name to the symbol table as a variable. This is used to return a value.
			symbolTable.addFunctionReturn(sub.getName(), returnType);
			match( Keywords.SEMI_COLON);
		}
		//procedure case 
		else if( lookahead.getType() == Keywords.PROCEDURE){
			match( Keywords.PROCEDURE);
			if( lookahead.getType() == Keywords.ID){
				sub = new SubProgramNode( lookahead.getLexeme(), Keywords.PROCEDURE);
				match( Keywords.ID);
			}
			//adds the procedure to the symbol table. This also pushes a fresh symbol table on to the stack.
			symbolTable.add(sub.getName(), Kinds.PROCEDURE, null, null, null);
			//adds arguments to the SubProgramNode
			args = arguments();
			sub.setArguments( args);
			match( Keywords.SEMI_COLON);
		}
		
		return sub;
	}

	//handles building argument variables.
	private ArrayList<VariableNode> arguments() {
		ArrayList<VariableNode> argsList = new ArrayList<VariableNode>();
		if( lookahead.getType() == Keywords.LEFT_PARENTHESES){
			match( Keywords.LEFT_PARENTHESES);
			//parameter list parses argument names and returns VariableNodes.
			//true is passed because these are arguments and require additional
			//handling for code generation.
			argsList.addAll( parameter_list( true));
			match( Keywords.RIGHT_PARENTHESES);
		}
		else{
			//lambda case
		}
		return argsList;
	}

	//parses out identifiers and builds a list of VariableNodes.
	private ArrayList<VariableNode> parameter_list( boolean isArguement) {
		ArrayList<String> identifierList = identifier_list();
		match( Keywords.COLON);
		//builds a list of VariableNodes with a given type, Integer or Real.
		ArrayList<VariableNode> argsList = type( identifierList, isArguement);
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			//calls itself until all parameter lists are converted to VariableNodes.
			argsList.addAll( parameter_list( isArguement));
		}
		return argsList;
	}

	//handles multiple statements grouped together and contained within a Begin/End block.
	private CompoundStatementNode compound_statement() {
		CompoundStatementNode compState = null;
		if( lookahead.getType() == Keywords.BEGIN){
			match( Keywords.BEGIN);
			//builds all statements and returns a CompoundStatementNode
			compState = optional_statements();
			match( Keywords.END);
		}else{
			error( "compound statement");
		}
		return compState;
	}

	//calls statement_list and adds the StatementNodes to the CompoundStatementNode
	private CompoundStatementNode optional_statements() {
		CompoundStatementNode compState = new CompoundStatementNode();
		Keywords nextType = lookahead.getType();
		if( nextType == Keywords.ID || nextType == Keywords.BEGIN || 
				nextType == Keywords.IF || nextType == Keywords.WHILE){
			//statement_list returns built StatementNodes to add to the CompoundStatementNode
			ArrayList<StatementNode> states = new ArrayList<StatementNode>( statement_list());
			for(StatementNode s : states) { compState.addStatement(s); }
		}		
		else{
			//lambda option
		}
		
		return compState;
	}

	//builds a list of StatementNodes
	private ArrayList<StatementNode> statement_list() {
		ArrayList<StatementNode> stateList = new ArrayList<StatementNode>();
		//returns a StatementNode and adds it to stateList
		stateList.add( statement());
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			//calls itself until all StatmentNodes have been built
			stateList.addAll( statement_list());
		}
		
		return stateList;
	}

	//builds a StatementNode
	private StatementNode statement() { 
		//the analyzer makes sure expression trees are structured correctly to provide 
		//mathematically correct results.
		SemanticAnalyzer analyzer = null;
		switch ( lookahead.getType()){
			case ID: //handles variable, array, function, and procedure assignments
				String identifier = lookahead.getLexeme();
				//case array or variable
				if( symbolTable.isArrayName( identifier) || symbolTable.isVariableName( identifier)){
					AssignmentStatementNode node = new AssignmentStatementNode();
					//handles VariableNode construction
					VariableNode var = variable();
					//sets VariableNode side of the AssignmentNode tree
					node.setLvalue( var);
					match(Keywords.ASSIGNMENT_OPERATOR);
					
					//analyzes expression before assigning it to the ExpressionNode side of 
					//the AssignmentNode tree.
					analyzer = new SemanticAnalyzer( expression());
					node.setExpression( analyzer.codeFolding());
					
					//check that a real is not getting assigned to an integer and vise versa.
					if( node.getLvalue().getType() != node.getExpression().getType()){
						error( "type mismatch");
					}
					
					return node;
				}
				//sub program case
				else if( symbolTable.isFunctionName( identifier) || symbolTable.isProcedureName( identifier)){
					//procedure_statement returns a ProcedureStatementNode, which handles
					//a procedure tree.
					return procedure_statement();
				}
				else{
					error( identifier + " has not been declared");
				}
				break;
			case IF: //if statement case
				//elseCount will help label the else statement in code generation
				IfStatementNode ifnode = new IfStatementNode( elseCount);
				elseCount++;
				match( Keywords.IF);
				//analyzes expression before assigning it to the ExpressionNode side of the test tree.
				analyzer = new SemanticAnalyzer( expression());
				ifnode.setTest( analyzer.codeFolding());
				match( Keywords.THEN);
				//builds and assigns then statement
				ifnode.setThenStatement( statement());
				if( lookahead.getType() == Keywords.ELSE){
					match( Keywords.ELSE);
					//builds and assigns the else statement
					ifnode.setElseStatement( statement());
				}
				return ifnode;
				
			case WHILE: //while statement case
				//elseCount is used to help label the false jump in code generation
				WhileStatementNode whilenode = new WhileStatementNode( elseCount);
				elseCount++;
				match( Keywords.WHILE);
				//analyzes expression before assigning it to the ExpressionNode side of the test tree.
				analyzer = new SemanticAnalyzer( expression());
				whilenode.setTest( analyzer.codeFolding());
				match( Keywords.DO);
				//builds and assigns then statement
				whilenode.setThenStatement( statement());
				return whilenode;
				
			case BEGIN: //compound statement case
				return compound_statement();
			default:
				error( "statment expected");
		}
		
		return null;
	}

	//Builds a VariableNode
	private VariableNode variable() {
		String id = lookahead.getLexeme();
		match( Keywords.ID);
		//variable case
		if( symbolTable.isVariableName( id)){
			VariableNode var = new VariableNode( id, symbolTable.getType(id));
			return var;
		}
		//array case
		else if( symbolTable.isArrayName( id)){
			//this is an assignment for a single element in an array. Thus it requires an index value
			if( lookahead.getType() == Keywords.LEFT_SQUARE_BRACKET){
				ArrayNode array = new ArrayNode( id, symbolTable.getType( id));
				match( Keywords.LEFT_SQUARE_BRACKET);
				//analyzes expression before assigning it to the ExpressionNode side of the ArrayNode tree.
				SemanticAnalyzer analyzer = new SemanticAnalyzer( expression());
				array.setExpression( analyzer.codeFolding());
				match( Keywords.RIGHT_SQUARE_BRACKET);
				
				return array;
			}
			else{
				error( "Expected Array index expression");
			}
		}
		
		return null;		
	}

	//handles ProceudreStatmentNode trees. This is used to build the procedure/function call 
	//during code generation.
	private ProcedureStatementNode procedure_statement() {
		ProcedureStatementNode node = new ProcedureStatementNode();
		node.setLvalue( new VariableNode( lookahead.getLexeme(), Keywords.PROCEDURE));
		match( Keywords.ID);
		match( Keywords.LEFT_PARENTHESES);
		//builds and adds all expressions being passed to the procedure.
		node.setExpressions( expression_list());
		match( Keywords.RIGHT_PARENTHESES);
		
		return node;
	}

	//handles a list of expressions for procedure calls
	private ArrayList<ExpressionNode> expression_list() {
		ArrayList<ExpressionNode> expList = new ArrayList<ExpressionNode>();
		//analyzes expression before assigning it to the ExpressionNode side of the the procedure.
		SemanticAnalyzer analyzer = new SemanticAnalyzer( expression());
		expList.add( analyzer.codeFolding());
		if( lookahead.getType() == Keywords.COMMA){
			match( Keywords.COMMA);
			//calls itself until all expressions are built.
			expList.addAll( expression_list());
		}
		
		return expList;
	}

	//Builds an ExpressionNode 
	private ExpressionNode expression() {
		ExpressionNode exp = simple_expression();
		if( isRelop( lookahead)){ //case relational operator
			OperationNode op = new OperationNode( lookahead.getType());
			op.setLeft( exp);
			relop();
			op.setRight( simple_expression());
			return op;
		}
		else{
			//lambda case
		}
		
		return exp;
	}
	
	//builds an ExpressionNode
	private ExpressionNode simple_expression() {
		//term case
		if( isTerm(lookahead)){
			//builds the expression from the term
			ExpressionNode exp = term();
			return simple_part( exp);
		}
		//unary operator case
		else if( lookahead.getType() == Keywords.MINUS || lookahead.getType() == Keywords.PLUS){
			//handles the identification of a unary sign.
			UnaryOperationNode unop = sign();
			ExpressionNode exp = term();
			unop.setExpression( simple_part( exp));
			return unop;
		}
		else{
			error( "expected simple expression");
		}
		
		return null;
	}

	//handles addop creation
	private ExpressionNode simple_part( ExpressionNode leftExp) {
		if( lookahead.getType() == Keywords.PLUS || lookahead.getType() == Keywords.MINUS){
			//builds an addop OperatiorNode
			OperationNode opnode = addop();
			opnode.setLeft( leftExp);
			ExpressionNode rightExp = term();
			opnode.setRight( simple_part( rightExp));
			return opnode;
		}
		else{
			//lambda option
		}
		
		return leftExp;
		
	}

	/**
	 * Executes the rule for the term non-terminal symbol in
	 * the expression grammar.
	 */
	private ExpressionNode term() {
	    ExpressionNode exp = factor();
	    return term_prime( exp);
	}

	/**
	 * Executes the rule for the term&prime; non-terminal symbol in
	 * the expression grammar.
	 */
	private ExpressionNode term_prime( ExpressionNode leftExp) {
	    if( isMulop( lookahead) ) {
	    	//builds a mulop OperationNode
	        OperationNode opnode = mulop();
	        opnode.setLeft( leftExp);
	        ExpressionNode rightExp = factor();
	        opnode.setRight( term_prime( rightExp));
	        return opnode;
	    }
	    else{
	        // lambda option
	    }
	    
	    return leftExp;
	}

	/**
	 * Executes the rule for the factor non-terminal symbol in
	 * the expression grammar.
	 */
	private ExpressionNode factor() {
		
		ExpressionNode exp = null;
		
	    switch ( lookahead.getType()) {
	    	//expression in parentheses case
	        case LEFT_PARENTHESES:
	            match( Keywords.LEFT_PARENTHESES);
	            exp = expression();
	            match( Keywords.RIGHT_PARENTHESES);
	            break;
	        //straight up number case
	        case NUMBER:
	        	//real number case
	        	if( lookahead.getLexeme().contains(".")){
	        		exp = new ValueNode( lookahead.getLexeme(), Keywords.REAL);
	        	//integer case
	        	}else{
	        		exp = new ValueNode( lookahead.getLexeme(), Keywords.INTEGER);
	        	}
	        	
	            match( Keywords.NUMBER);
	            break;
	        //variable, array, or function case
	        case ID: 
	        	String identifier = lookahead.getLexeme();
	        	
	        	//variable or array case
				if( symbolTable.isArrayName( identifier) || symbolTable.isVariableName( identifier)){
					return variable();
				}
				//function case
				else if( symbolTable.isFunctionName( identifier)){
					//builds FunctionNode from the information in the SymbolTable
					FunctionNode fnode = new FunctionNode( identifier, symbolTable.getType(identifier));
					match( Keywords.ID);
					match( Keywords.LEFT_PARENTHESES);
					//handles function arguments
					fnode.setExpNode( expression_list());
					match( Keywords.RIGHT_PARENTHESES);
					return fnode;
				}
				else{
					error( identifier + " has not been declared");
				}
	        	break;
	        //Not operator case
	        case NOT:
	        	UnaryOperationNode opnode = new UnaryOperationNode( Keywords.NOT);
	        	match( Keywords.NOT);
	        	opnode.setExpression( factor());
	        	return opnode;
	        default:
	            error("Factor");
	            break;
	    }
	    
	    return exp;
	}

	//handles an expression with a sign
	private UnaryOperationNode sign() {
		Keywords type = lookahead.getType();
		
		switch( type){
			case MINUS:
				match( Keywords.MINUS);
				break;
			case PLUS:
				match( Keywords.PLUS);
				break;
			default:
				break;
		}
		
		return new UnaryOperationNode( type);
		
	}

	//tests if the token is a term
	private boolean isTerm( Token token) {
		boolean answer = false;
		Keywords nextType = token.getType();
		if( nextType == Keywords.ID || nextType == Keywords.NUMBER ||
				nextType == Keywords.NOT || nextType == Keywords.LEFT_PARENTHESES){
			answer = true;
		}
		return answer;
	}

	//builds a relational OperationNode
	private OperationNode relop() {
		Keywords type = lookahead.getType();
		
		switch( type){
			case EQUALITY_OPERATOR:
				match( Keywords.EQUALITY_OPERATOR);
				break;
			case NOT_EQUAL:
				match( Keywords.NOT_EQUAL);
				break;
			case LESS_THAN:
				match( Keywords.LESS_THAN);
				break;
			case LESS_THAN_EQUAL_TO:
				match( Keywords.LESS_THAN_EQUAL_TO);
				break;
			case GREATER_THAN_EQUAL_TO:
				match( Keywords.GREATER_THAN_EQUAL_TO);
				break;
			case GREATER_THAN:
				match( Keywords.LESS_THAN);
				break;
			default:
				error( "expected relop");
		}
		
		return new OperationNode( type);
		
	}

	//tests if token is a relational operator
	private boolean isRelop( Token token){
		boolean answer = false;
		Keywords nextType = token.getType();
		if( nextType == Keywords.EQUALITY_OPERATOR || nextType == Keywords.NOT_EQUAL || nextType == Keywords.LESS_THAN || 
				nextType == Keywords.LESS_THAN_EQUAL_TO || nextType == Keywords.GREATER_THAN_EQUAL_TO || 
				nextType == Keywords.GREATER_THAN){
			answer = true;
		}
		return answer;
	}
    
    /**
     * Executes the rule for the addop non-terminal symbol in
     * the expression grammar.
     * @return addop OperationNode of the given type (Plus or Minus).
     */
    private OperationNode addop() {
    	Keywords type = lookahead.getType();
    	
        if( type == Keywords.PLUS) {
            match( Keywords.PLUS);
        }
        else if( type == Keywords.MINUS) {
            match( Keywords.MINUS);
        }
        else {
            error( "Addop");
        }
        
        return new OperationNode( type);
    }
    
    /**
     * Determines whether or not the given token is
     * a mulop token.
     * @param token The token to check.
     * @return true if the token is a mulop, false otherwise
     */
    private boolean isMulop( Token token) {
        boolean answer = false;
        switch( token.getType()){
        	case TIMES:
        	case DIVIDE:
        	case AND:
        	case MOD:
        	case DIV:
        		answer = true;
        	default:
        		break;
        }
        return answer;
    }
    
    /**
     * Executes the rule for the mulop non-terminal symbol in
     * the expression grammar.
     * @return mulop OperationNode of given type (Multiply, divide, and, modulus, integer division)
     */
    private OperationNode mulop() {
    	Keywords type = lookahead.getType();
    	
    	switch( type){
    		case TIMES:
	            match( Keywords.TIMES);
	            break;
    		case DIVIDE:
	            match( Keywords.DIVIDE);
	            break;
    		case AND:
    			match( Keywords.AND);
    			break;
    		case MOD:
    			match( Keywords.MOD);
    			break;
    		case DIV:
    			match( Keywords.DIV);
    			break;
	        default:
	            error( "Mulop");
    	}
    	
    	return new OperationNode( type);
    }
    
    /**
     * Matches the expected token.
     * If the current token in the input stream from the scanner
     * matches the token that is expected, the current token is
     * consumed and the scanner will move on to the next token
     * in the input.
     * The null at the end of the file returned by the
     * scanner is replaced with a fake token containing no
     * type.
     * @param expected The expected token type.
     */
    private void match( Keywords expected) {
        //System.out.println("match( " + expected + ")");
        if( this.lookahead.getType() == expected) {
            try {
                this.lookahead = scanner.nextToken();
                if( this.lookahead == null) {
                    this.lookahead = new Token( "End of File", null);
                }
            } catch (IOException ex) {
                error( "Scanner exception");
            }
        }
        else {
            error("Match of " + expected + " found " + this.lookahead.getType()
                    + " instead.");
        }
    }
    
    /**
     * Errors out of the parser.
     * Prints an error message and then exits the program.
     * @param message The error message to print.
     */
    private void error( String message) {
    	noError = false;
        System.out.println( "Error " + message + " at line " + 
                this.scanner.getLine() + " column " + 
                this.scanner.getColumn());
        //System.exit( 1);
    }
}