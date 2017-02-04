package parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public void program(){
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
    }
    
    private void declarations() {
    	if( lookahead.getType() == Keywords.VAR){
	    	match( Keywords.VAR);
	    	identifier_list();
	    	match( Keywords.COLON);
	    	type();
	    	match( Keywords.SEMI_COLON);
	    	declarations();
    	}
    	else{
    		//lambda case
    	}
		
	}

	private void type() {
		if( lookahead.getType() == Keywords.ARRAY){
			match( Keywords.ARRAY);
			match( Keywords.LEFT_SQUARE_BRACKET);
			match( Keywords.NUMBER);
			match( Keywords.COLON);
			match( Keywords.NUMBER);
			match( Keywords.OF);
			standard_type();
		}
		else if( lookahead.getType() == Keywords.INTEGER || lookahead.getType() == Keywords.REAL){
			standard_type();
		}
		else{
			error( "Expected var type");
		}
		
	}

	private void standard_type() {
		switch( lookahead.getType()){
			case INTEGER:
				match( Keywords.INTEGER);
				break;
			case REAL:
				match( Keywords.REAL);
				break;
			default:
				break;
		}
		
	}

	private void identifier_list() {
		match( Keywords.ID);
		if( lookahead.getType() == Keywords.COMMA){
			match( Keywords.COMMA);
			identifier_list();
		}
		
	}

	private void compound_statement() {
		if( lookahead.getType() == Keywords.BEGIN){
			match( Keywords.BEGIN);
			optional_statements();
			match( Keywords.END);
		}else{
			error( "compound statement");
		}
		
	}

	private void optional_statements() {
		Keywords nextType = lookahead.getType();
		if( nextType == Keywords.ID || nextType == Keywords.BEGIN || nextType == Keywords.IF || nextType == Keywords.WHILE){
			statement_list();
		}		
		else{
			//lambda option
		}
	}

	private void statement_list() {
		statement();
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			statement_list();
		}
		
	}

	private void statement() {
		switch ( lookahead.getType()){
			case ID:
				match( Keywords.ID);
				if( lookahead.getType() == Keywords.LEFT_SQUARE_BRACKET){
					variable();
				}
				else if( lookahead.getType() == Keywords.LEFT_PARENTHESES){
					procedure_statement();
				}
				else if( lookahead.getType() == Keywords.ASSIGNMENT_OPERATOR){
					match(Keywords.ASSIGNMENT_OPERATOR);
					expression();
				}
				else{
					error( "invalid statement");
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
			default:
				error( "statment expected");
		}
		
		
	}

	private void procedure_statement() {
		match( Keywords.LEFT_PARENTHESES);
		expression_list();
		match( Keywords.RIGHT_PARENTHESES);
		
	}

	private void expression_list() {
		expression();
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			expression_list();
		}
		
	}

	private void expression() {
		simple_expression();
		if( isRelop(lookahead)){
			relop();
			simple_expression();
		}
		else{
			//lambda case
		}
		
	}
	
	private void simple_expression() {
		if( isTerm(lookahead)){
			term();
			simple_part();
		}
		else if( lookahead.getType() == Keywords.MINUS || lookahead.getType() == Keywords.PLUS){
			sign();
			term();
			simple_part();
		}
		
	}

	private void sign() {
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

	private void simple_part() {
		if( lookahead.getType() == Keywords.PLUS || lookahead.getType() == Keywords.MINUS){
			addop();
			term();
			simple_part();
		}
		else{
			//lambda option
		}
		
	}

	private boolean isTerm( Token token) {
		boolean answer = false;
		Keywords nextType = token.getType();
		if( nextType == Keywords.ID || nextType == Keywords.NUMBER || nextType == Keywords.NOT){
			answer = true;
		}
		return answer;
	}

	private void relop() {
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

	private void variable() {
		expression();
		match( Keywords.RIGHT_SQUARE_BRACKET);
		
	}

	private void subprogram_declarations() {
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

	private void subprogram_declaration() {
		subprogram_head();
		declarations();
		subprogram_declarations();
		compound_statement();
	}

	private void subprogram_head() {
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

	private void arguments() {
		if( lookahead.getType() == Keywords.LEFT_PARENTHESES){
			match( Keywords.LEFT_PARENTHESES);
			parameter_list();
			match( Keywords.RIGHT_PARENTHESES);
		}
		else{
			//lambda case
		}
		
	}

	private void parameter_list() {
		identifier_list();
		match( Keywords.SEMI_COLON);
		type();
		if( lookahead.getType() == Keywords.SEMI_COLON){
			match( Keywords.SEMI_COLON);
			parameter_list();
		}
		
	}

	/**
     * Executes the rule for the exp non-terminal symbol in
     * the expression grammar.
     */
    public void exp() {
        term();
        exp_prime();
    }
    
    /**
     * Executes the rule for the exp&prime; non-terminal symbol in
     * the expression grammar.
     */
    public void exp_prime() {
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
    public void addop() {
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
     * Executes the rule for the term non-terminal symbol in
     * the expression grammar.
     */
    public void term() {
        factor();
        term_prime();
    }
    
    /**
     * Executes the rule for the term&prime; non-terminal symbol in
     * the expression grammar.
     */
    public void term_prime() {
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
     * Determines whether or not the given token is
     * a mulop token.
     * @param token The token to check.
     * @return true if the token is a mulop, false otherwise
     */
    private boolean isMulop( Token token) {
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
    public void mulop() {
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
     * Executes the rule for the factor non-terminal symbol in
     * the expression grammar.
     */
    public void factor() {
        // Executed this decision as a switch instead of an
        // if-else chain. Either way is acceptable.
        switch (lookahead.getType()) {
            case LEFT_PARENTHESES:
                match( Keywords.LEFT_PARENTHESES);
                exp();
                match( Keywords.RIGHT_PARENTHESES);
                break;
            case NUMBER:
                match( Keywords.NUMBER);
                break;
            case ID:
            	match( Keywords.ID);
            	break;
            default:
                error("Factor");
                break;
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
    public void match( Keywords expected) {
        System.out.println("match( " + expected + ")");
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
    public void error( String message) {
        System.out.println( "Error " + message + " at line " + 
                this.scanner.getLine() + " column " + 
                this.scanner.getColumn());
        //System.exit( 1);
    }
}