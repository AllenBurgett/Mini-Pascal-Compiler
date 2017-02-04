package scanner;

import java.util.HashMap;

public class LUT extends HashMap<String, Keywords>{
	
	public LUT(){
		this.put("and",Keywords.AND);
		this.put("array",Keywords.ARRAY);
		this.put("begin",Keywords.BEGIN);
		this.put("div",Keywords.DIV);
		this.put("do",Keywords.DO);
		this.put("else",Keywords.ELSE);
		this.put("end",Keywords.END);
		this.put("function",Keywords.FUNCTION);
		this.put("if",Keywords.IF);
		this.put("integer",Keywords.INTEGER);
		this.put("mod",Keywords.MOD);
		this.put("not",Keywords.NOT);
		this.put("of",Keywords.OF);
		this.put("or",Keywords.OR);
		this.put("procedure",Keywords.PROCEDURE);
		this.put("program",Keywords.PROGRAM);
		this.put("real",Keywords.REAL);
		this.put("then",Keywords.THEN);
		this.put("var",Keywords.VAR);
		this.put("while",Keywords.WHILE);
		this.put(";",Keywords.SEMI_COLON);
		this.put(",",Keywords.COMMA);
		this.put(".",Keywords.PERIOD);
		this.put(":",Keywords.COLON);
		this.put("[",Keywords.LEFT_SQUARE_BRACKET);
		this.put("]",Keywords.RIGHT_SQUARE_BRACKET);
		this.put("(",Keywords.LEFT_PARENTHESES);
		this.put(")",Keywords.RIGHT_PARENTHESES);
		this.put("+",Keywords.PLUS);
		this.put("-",Keywords.MINUS);
		this.put("=",Keywords.EQUALITY_OPERATOR);
		this.put("<>",Keywords.NOT_EQUAL);
		this.put("<",Keywords.LESS_THAN);
		this.put("<=",Keywords.LESS_THAN_EQUAL_TO);
		this.put(">",Keywords.GREATER_THAN);
		this.put(">=",Keywords.GREATER_THAN_EQUAL_TO);
		this.put("*",Keywords.TIMES);
		this.put("/",Keywords.DIVIDE);
		this.put(":=",Keywords.ASSIGNMENT_OPERATOR);
	}
}
