package parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import scanner.MyScanner;
import scanner.Token;
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
    
    public SymbolTable symbolTable = new SymbolTable();
    
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
    
    }
    
    ///////////////////////////////
    //       Methods
    ///////////////////////////////
    
    public boolean program(){
    	if( lookahead.getType() == Keywords.PROGRAM){
    		match( Keywords.PROGRAM);
    		match( Keywords.ID);
    		match( Keywords.SEMI_COLON);
	    	declarations();
	    	subprogram_declarations();
	    	compound_statement();
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

	protected void declarations() {
    	if( lookahead.getType() == Keywords.VAR){
    		
	    	match( Keywords.VAR);
	    	ArrayList<String> identifierList = identifier_list();
	    	match( Keywords.COLON);
	    	
	    	SimpleEntry<String, Kinds> typeKind = type();
	    	
	    	for(String id : identifierList){
	    		boolean success = symbolTable.add(id, typeKind.getValue(), typeKind.getKey());
	    		
	    		if(! success){
	    			error( id + " already declared");
	    		}
	    	}
	    	
	    	match( Keywords.SEMI_COLON);
	    	declarations();
    	}
    	else{
    		//lambda case
    	}
		
	}

	protected SimpleEntry<String, Kinds> type() {
		SimpleEntry<String, Kinds> typeKind = null;
		
		if( lookahead.getType() == Keywords.ARRAY){
			
			match( Keywords.ARRAY);
			match( Keywords.LEFT_SQUARE_BRACKET);
			match( Keywords.NUMBER);
			match( Keywords.COLON);
			match( Keywords.NUMBER);
			match( Keywords.RIGHT_SQUARE_BRACKET);
			match( Keywords.OF);
			typeKind = new SimpleEntry<String, Kinds>(standard_type(), Kinds.ARRAY);
		}
		else if( lookahead.getType() == Keywords.INTEGER || lookahead.getType() == Keywords.REAL){
			typeKind = new SimpleEntry<String, Kinds>(standard_type(), Kinds.VARIABLE);
		}
		else{
			error( "Expected var type");
		}
		
		return typeKind;		
	}

	protected String standard_type() {
		String standardType = null;
		
		switch( lookahead.getType()){
			case INTEGER:
				standardType = "INTEGER";
				match( Keywords.INTEGER);
				break;
			case REAL:
				standardType = "REAL";
				match( Keywords.REAL);
				break;
			default:
				error( "Type expected");
				break;
				
		}
		return standardType;
		
	}

	protected void subprogram_declarations() {
		if( lookahead.getType() == Keywords.FUNCTION || lookahead.getType() == Keywords.PROCEDURE){
			subprogram_declaration();
			if( lookahead.getType() == Keywords.SEMI_COLON){
				match( Keywords.SEMI_COLON);		
				subprogram_declarations();
			}
		}
		else{
			//lambda case
		}
		
	}

	protected void subprogram_declaration() {
		subprogram_head();
		declarations();
		subprogram_declarations();
		compound_statement();
	}

	protected void subprogram_head() {
		if( lookahead.getType() == Keywords.FUNCTION){
			match( Keywords.FUNCTION);
			match( Keywords.ID);
			arguments();
			match( Keywords.COLON);
			standard_type();
			match( Keywords.SEMI_COLON);
		}
		else if( lookahead.getType() == Keywords.PROCEDURE){
			match( Keywords.PROCEDURE);
			match( Keywords.ID);
			arguments();
			match( Keywords.SEMI_COLON);
		}
		
		
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
		identifier_list();
		match( Keywords.COLON);
		type();
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			parameter_list();
		}
		
	}

	protected void compound_statement() {
		if( lookahead.getType() == Keywords.BEGIN){
			match( Keywords.BEGIN);
			optional_statements();
			match( Keywords.END);
		}else{
			error( "compound statement");
		}
		
	}

	protected void optional_statements() {
		Keywords nextType = lookahead.getType();
		if( nextType == Keywords.ID || nextType == Keywords.BEGIN || nextType == Keywords.IF || nextType == Keywords.WHILE){
			statement_list();
		}		
		else{
			//lambda option
		}
	}

	protected void statement_list() {
		statement();
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			statement_list();
		}
		
	}

	protected void statement() {
		switch ( lookahead.getType()){
			case ID:
				String identifier = lookahead.getLexeme();
				if( symbolTable.isArrayName( identifier) || symbolTable.isVariableName( identifier)){
					variable();
					match(Keywords.ASSIGNMENT_OPERATOR);
					expression();
				}					
				else if( symbolTable.isFunctionName( identifier)){
					procedure_statement();
				}
				else{
					error( identifier + " has not been declared");
				}
				break;
			case IF:
				match( Keywords.IF);
				expression();
				match( Keywords.THEN);
				statement();
				if( lookahead.getType() == Keywords.ELSE){
					match( Keywords.ELSE);
					statement();
				}
				break;
			case WHILE:
				match( Keywords.WHILE);
				expression();
				match( Keywords.DO);
				statement();
				break;
			case BEGIN:
				compound_statement();
				break;
			default:
				error( "statment expected");
		}
		
		
	}

	protected void variable() {
		match( Keywords.ID);
		if( lookahead.getType() == Keywords.LEFT_SQUARE_BRACKET){
			match( Keywords.LEFT_SQUARE_BRACKET);
			expression();
			match( Keywords.RIGHT_SQUARE_BRACKET);
		}
		
	}

	protected void procedure_statement() {
		match( Keywords.ID);
		match( Keywords.LEFT_PARENTHESES);
		expression_list();
		match( Keywords.RIGHT_PARENTHESES);
		
	}

	protected void expression_list() {
		expression();
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			expression_list();
		}
		
	}

	protected void expression() {
		simple_expression();
		if( isRelop( lookahead)){
			relop();
			simple_expression();
		}
		else{
			//lambda case
		}
		
	}
	
	protected void simple_expression() {
		if( isTerm(lookahead)){
			term();
			simple_part();
		}
		else if( lookahead.getType() == Keywords.MINUS || lookahead.getType() == Keywords.PLUS){
			sign();
			term();
			simple_part();
		}
		else{
			error( "expected simple expression");
		}
		
	}

	protected void simple_part() {
		if( lookahead.getType() == Keywords.PLUS || lookahead.getType() == Keywords.MINUS){
			addop();
			term();
			simple_part();
		}
		else{
			//lambda option
		}
		
	}

	/**
	 * Executes the rule for the term non-terminal symbol in
	 * the expression grammar.
	 */
	protected void term() {
	    factor();
	    term_prime();
	}

	/**
	 * Executes the rule for the term&prime; non-terminal symbol in
	 * the expression grammar.
	 */
	protected void term_prime() {
	    if( isMulop( lookahead) ) {
	        mulop();
	        factor();
	        term_prime();
	    }
	    else{
	        // lambda option
	    }
	}

	/**
	 * Executes the rule for the factor non-terminal symbol in
	 * the expression grammar.
	 */
	protected void factor() {
	    // Executed this decision as a switch instead of an
	    // if-else chain. Either way is acceptable.
	    switch ( lookahead.getType()) {
	        case LEFT_PARENTHESES:
	            match( Keywords.LEFT_PARENTHESES);
	            expression();
	            match( Keywords.RIGHT_PARENTHESES);
	            break;
	        case NUMBER:
	            match( Keywords.NUMBER);
	            break;
	        case ID:
	        	String identifier = lookahead.getLexeme();
				if( symbolTable.isArrayName( identifier) || symbolTable.isVariableName( identifier)){
					variable();
				}					
				else if( symbolTable.isFunctionName( identifier)){
					procedure_statement();
				}
				else{
					error( identifier + " has not been declared");
				}
	        	break;
	        case NOT:
	        	match( Keywords.NOT);
	        	factor();
	        	break;
	        default:
	            error("Factor");
	            break;
	    }
	}

	protected void sign() {
		switch( lookahead.getType()){
			case MINUS:
				match( Keywords.MINUS);
				break;
			case PLUS:
				match( Keywords.PLUS);
				break;
			default:
				break;
		}
		
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

	protected void relop() {
		switch( lookahead.getType()){
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
    protected void addop() {
        if( lookahead.getType() == Keywords.PLUS) {
            match( Keywords.PLUS);
        }
        else if( lookahead.getType() == Keywords.MINUS) {
            match( Keywords.MINUS);
        }
        else {
            error( "Addop");
        }
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
    protected void mulop() {
        if( lookahead.getType() == Keywords.TIMES) {
            match( Keywords.TIMES);
        }
        else if( lookahead.getType() == Keywords.DIVIDE) {
            match( Keywords.DIVIDE);
        }
        else {
            error( "Mulop");
        }
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
