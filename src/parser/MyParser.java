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
 * is an expression.
 * To use a parser, create an instance pointing at a file,
 * and then call the top-level function, <code>exp()</code>.
 * If the functions returns without an error, the file
 * contains an acceptable expression.
 * @based on code from Erik Steinmetz
 */
public class MyParser {
    
    ///////////////////////////////
    //    Instance Variables
    ///////////////////////////////
    
    private Token lookahead;
    
    private MyScanner scanner;
    
    public SymbolTable symbolTable;
    
    public ProgramNode prog;    
    public DeclarationsNode decs = new DeclarationsNode();
    
    private boolean noError = true;
    
    ///////////////////////////////
    //       Constructors
    ///////////////////////////////
    
    public MyParser( String text, boolean isFilename) {
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
        else {
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
    
    
    
    public boolean program(){
    	if( lookahead.getType() == Keywords.PROGRAM){
    		match( Keywords.PROGRAM);
    		
    		if( lookahead.getType() == Keywords.ID){
    			symbolTable.add(lookahead.getLexeme(), Kinds.PROGRAM, null, null, null);
    			prog = new ProgramNode(lookahead.getLexeme());
    			match( Keywords.ID);
    		}
    		else{
    			error( " expected Program name");
    		}
    		
    		match( Keywords.SEMI_COLON);
	    	prog.setVariables( declarations());
	    	prog.setFunctions( subprogram_declarations());
	    	prog.setMain( compound_statement());
	    	match( Keywords.PERIOD);
    	}
    	else{
    		error( "program");
    	}
    	
    	return noError;
    }
    
    protected ArrayList<String> identifier_list() {
    	ArrayList<String> identifierList = new ArrayList<String>();
    	identifierList.add( lookahead.getLexeme());
		match( Keywords.ID);
		
		if( lookahead.getType() == Keywords.COMMA){
			match( Keywords.COMMA);
			identifierList.addAll( identifier_list());
		}
		
		return identifierList;
	}

	protected DeclarationsNode declarations() {		
    	if( lookahead.getType() == Keywords.VAR){
    		
	    	match( Keywords.VAR);
	    	ArrayList<String> identifierList = identifier_list();
	    	match( Keywords.COLON);
	    	
	    	ArrayList<VariableNode> vars = type(identifierList);
	    	for(VariableNode var : vars){ decs.addVariable( var);}
	    		    	
	    	match( Keywords.SEMI_COLON);
	    	declarations();
    	}
    	else{
    		//lambda case
    	}
    	
    	return decs;
		
	}

	protected ArrayList<VariableNode> type(ArrayList<String> identifierList) {
		Integer arrayStart = null;
		Integer arrayEnd = null;
		Kinds kind = null;
		Keywords varType = null; 		
		
		if( lookahead.getType() == Keywords.ARRAY){
			
			match( Keywords.ARRAY);
			match( Keywords.LEFT_SQUARE_BRACKET);
			if( lookahead.getType() == Keywords.NUMBER){
				arrayStart = Integer.parseInt( lookahead.getLexeme());
				match( Keywords.NUMBER);
			}
			else{
				error( " expected array index");
			}
			
			match( Keywords.COLON);
			
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
			varType = standard_type();
		}
		else if( lookahead.getType() == Keywords.INTEGER || lookahead.getType() == Keywords.REAL){
			kind = Kinds.VARIABLE;
			varType = standard_type();
		}
		else{
			error( "Expected var type");
		}
		
		ArrayList<VariableNode> declaredVars = new ArrayList<VariableNode>();
		
    	for(String id : identifierList){
    		boolean success = symbolTable.add(id, kind, varType, arrayStart, arrayEnd);
    		
    		if( success){
    			if(kind.equals(Kinds.VARIABLE)){
    				declaredVars.add(new VariableNode( id, varType));
    			}else if(kind.equals(Kinds.ARRAY)){
    				declaredVars.add(new ArrayNode( id, varType));
    			}
    		}else{
    			error( id + " already declared");
    		}
    	}
    	
    	return declaredVars;
	}

	protected Keywords standard_type() {
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

	protected SubProgramDeclarationsNode subprogram_declarations() {
		SubProgramDeclarationsNode subProgs = new SubProgramDeclarationsNode();
		if( lookahead.getType() == Keywords.FUNCTION || lookahead.getType() == Keywords.PROCEDURE){
			subProgs.addSubProgramDeclaration( subprogram_declaration());
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

	protected SubProgramNode subprogram_declaration() {
		SubProgramNode sub = subprogram_head();
		sub.setVariables( declarations());
		sub.setFunctions( subprogram_declarations());
		sub.setMain( compound_statement());
		symbolTable.popTable();
		return sub;
	}

	protected SubProgramNode subprogram_head() {
		SubProgramNode sub = null;
		
		if( lookahead.getType() == Keywords.FUNCTION){
			match( Keywords.FUNCTION);
			if( lookahead.getType() == Keywords.ID){
				sub = new SubProgramNode( lookahead.getLexeme());
				match( Keywords.ID);
			}			
			arguments();
			match( Keywords.COLON);
			symbolTable.add(sub.getName(), Kinds.FUNCTION, standard_type(), null, null);
			match( Keywords.SEMI_COLON);
		}
		else if( lookahead.getType() == Keywords.PROCEDURE){
			match( Keywords.PROCEDURE);
			if( lookahead.getType() == Keywords.ID){
				sub = new SubProgramNode( lookahead.getLexeme());
				match( Keywords.ID);
			}
			arguments();
			symbolTable.add(sub.getName(), Kinds.PROCEDURE, null, null, null);
			match( Keywords.SEMI_COLON);
		}
		
		return sub;
	}

	protected void arguments() {
		if( lookahead.getType() == Keywords.LEFT_PARENTHESES){
			match( Keywords.LEFT_PARENTHESES);
			parameter_list();
			match( Keywords.RIGHT_PARENTHESES);
		}
		else{
			//lambda case
		}
		
	}

	protected void parameter_list() {
		ArrayList<String> identifierList = identifier_list();
		match( Keywords.COLON);
		type( identifierList);
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			parameter_list();
		}
		
	}

	protected CompoundStatementNode compound_statement() {
		CompoundStatementNode compState = null;
		if( lookahead.getType() == Keywords.BEGIN){
			match( Keywords.BEGIN);
			compState = optional_statements();
			match( Keywords.END);
		}else{
			error( "compound statement");
		}
		return compState;
	}

	protected CompoundStatementNode optional_statements() {
		CompoundStatementNode compState = new CompoundStatementNode();
		Keywords nextType = lookahead.getType();
		if( nextType == Keywords.ID || nextType == Keywords.BEGIN || nextType == Keywords.IF || nextType == Keywords.WHILE){
			ArrayList<StatementNode> states = new ArrayList<StatementNode>( statement_list());
			for(StatementNode s : states) { compState.addStatement(s); }
		}		
		else{
			//lambda option
		}
		
		return compState;
	}

	protected ArrayList<StatementNode> statement_list() {
		ArrayList<StatementNode> stateList = new ArrayList<StatementNode>();
		stateList.add( statement());
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			stateList.addAll( statement_list());
		}
		
		return stateList;
	}

	protected StatementNode statement() {
		SemanticAnalyzer analyzer = null;
		switch ( lookahead.getType()){
			case ID:
				String identifier = lookahead.getLexeme();
				if( symbolTable.isArrayName( identifier) || symbolTable.isVariableName( identifier)){
					AssignmentStatementNode node = new AssignmentStatementNode();
					VariableNode var = variable();
					node.setLvalue( var);
					match(Keywords.ASSIGNMENT_OPERATOR);
					
					analyzer = new SemanticAnalyzer( expression());
					node.setExpression( analyzer.codeFolding());
					
					if( node.getLvalue().getType() != node.getExpression().getType()){
						error( "type mismatch");
					}
					
					return node;
				}					
				else if( symbolTable.isFunctionName( identifier) || symbolTable.isProcedureName( identifier)){
					return procedure_statement();
				}
				else{
					error( identifier + " has not been declared");
				}
				break;
			case IF:
				IfStatementNode ifnode = new IfStatementNode();
				match( Keywords.IF);
				analyzer = new SemanticAnalyzer( expression());
				ifnode.setTest( analyzer.codeFolding());
				match( Keywords.THEN);
				ifnode.setThenStatement( statement());
				if( lookahead.getType() == Keywords.ELSE){
					match( Keywords.ELSE);
					ifnode.setElseStatement( statement());
				}
				return ifnode;
				
			case WHILE:
				WhileStatementNode whilenode = new WhileStatementNode();
				match( Keywords.WHILE);
				analyzer = new SemanticAnalyzer( expression());
				whilenode.setTest( analyzer.codeFolding());
				match( Keywords.DO);
				whilenode.setThenStatement( statement());
				return whilenode;
			case BEGIN:
				return compound_statement();
			default:
				error( "statment expected");
		}
		
		return null;
	}

	protected VariableNode variable() {
		String id = lookahead.getLexeme();
		match( Keywords.ID);
		if( symbolTable.isVariableName( id)){
			VariableNode var = new VariableNode( id, symbolTable.getType(id));
			return var;
		}
		else if( symbolTable.isArrayName( id)){
			if( lookahead.getType() == Keywords.LEFT_SQUARE_BRACKET){
				ArrayNode array = new ArrayNode( id, symbolTable.getType( id));
				match( Keywords.LEFT_SQUARE_BRACKET);
				array.setExpression( expression());
				match( Keywords.RIGHT_SQUARE_BRACKET);
				
				return array;
			}
			else{
				error( "Expected Array index expression");
			}
		}
		
		return null;		
	}

	protected ProcedureStatementNode procedure_statement() {
		ProcedureStatementNode node = new ProcedureStatementNode();
		node.setLvalue( new VariableNode( lookahead.getLexeme(), Keywords.PROCEDURE));
		match( Keywords.ID);
		match( Keywords.LEFT_PARENTHESES);
		node.setExpressions( expression_list());
		match( Keywords.RIGHT_PARENTHESES);
		
		return node;
	}

	protected ArrayList<ExpressionNode> expression_list() {
		ArrayList<ExpressionNode> expList = new ArrayList<ExpressionNode>();
		System.out.println("hit");
		expList.add( expression());
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			expList.addAll( expression_list());
		}
		
		return expList;
	}

	protected ExpressionNode expression() {
		ExpressionNode exp = simple_expression();
		if( isRelop( lookahead)){
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
	
	protected ExpressionNode simple_expression() {
		if( isTerm(lookahead)){
			ExpressionNode exp = term();
			return simple_part( exp);
		}
		else if( lookahead.getType() == Keywords.MINUS || lookahead.getType() == Keywords.PLUS){
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

	protected ExpressionNode simple_part( ExpressionNode leftExp) {
		if( lookahead.getType() == Keywords.PLUS || lookahead.getType() == Keywords.MINUS){
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
	protected ExpressionNode term() {
	    ExpressionNode exp = factor();
	    return term_prime( exp);
	}

	/**
	 * Executes the rule for the term&prime; non-terminal symbol in
	 * the expression grammar.
	 */
	protected ExpressionNode term_prime( ExpressionNode leftExp) {
	    if( isMulop( lookahead) ) {
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
	protected ExpressionNode factor() {
		
		ExpressionNode exp = null;
		
	    switch ( lookahead.getType()) {
	        case LEFT_PARENTHESES:
	            match( Keywords.LEFT_PARENTHESES);
	            exp = expression();
	            match( Keywords.RIGHT_PARENTHESES);
	            break;
	        case NUMBER:
	        	if( lookahead.getLexeme().contains(".")){
	        		exp = new ValueNode( lookahead.getLexeme(), Keywords.REAL);
	        	}else{
	        		exp = new ValueNode( lookahead.getLexeme(), Keywords.INTEGER);
	        	}
	        	
	            match( Keywords.NUMBER);
	            break;
	        case ID: 
	        	String identifier = lookahead.getLexeme();
	        	
				if( symbolTable.isArrayName( identifier) || symbolTable.isVariableName( identifier)){
					return variable();
				}					
				else if( symbolTable.isFunctionName( identifier)){
					FunctionNode fnode = new FunctionNode( identifier, Keywords.FUNCTION);
					match( Keywords.ID);
					match( Keywords.LEFT_PARENTHESES);
					fnode.setExpNode( expression_list());
					match( Keywords.RIGHT_PARENTHESES);
					return fnode;
				}
				else{
					error( identifier + " has not been declared");
				}
	        	break;
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

	protected UnaryOperationNode sign() {
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

	protected boolean isTerm( Token token) {
		boolean answer = false;
		Keywords nextType = token.getType();
		if( nextType == Keywords.ID || nextType == Keywords.NUMBER ||
				nextType == Keywords.NOT || nextType == Keywords.LEFT_PARENTHESES){
			answer = true;
		}
		return answer;
	}

	protected OperationNode relop() {
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

	protected boolean isRelop( Token token){
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
     * Executes the rule for the exp non-terminal symbol in
     * the expression grammar.
     */
    protected void exp() {
        term();
        exp_prime();
    }
    
    /**
     * Executes the rule for the exp&prime; non-terminal symbol in
     * the expression grammar.
     */
    protected void exp_prime() {
        if( lookahead.getType() == Keywords.PLUS || 
                lookahead.getType() == Keywords.MINUS ) {
            addop();
            term();
            exp_prime();
        }
        else{
            // lambda option
        }
    }
    
    /**
     * Executes the rule for the addop non-terminal symbol in
     * the expression grammar.
     */
    protected OperationNode addop() {
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
    protected boolean isMulop( Token token) {
        boolean answer = false;
        if( token.getType() == Keywords.TIMES || 
                token.getType() == Keywords.DIVIDE ) {
            answer = true;
        }
        return answer;
    }
    
    /**
     * Executes the rule for the mulop non-terminal symbol in
     * the expression grammar.
     */
    protected OperationNode mulop() {
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
    protected void match( Keywords expected) {
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
    protected void error( String message) {
    	noError = false;
        System.out.println( "Error " + message + " at line " + 
                this.scanner.getLine() + " column " + 
                this.scanner.getColumn());
        //System.exit( 1);
    }
}