package scanner;

import java.util.HashMap;

@SuppressWarnings({ "rawtypes", "serial" })
public class LUT extends HashMap{
	
	private HashMap<String, Token> lookup = new HashMap<String, Token>();
	
	public LUT(){
		lookup.put("and", new Token("and"));
		lookup.put("array", new Token("array"));
		lookup.put("begin", new Token("begin"));
		lookup.put("div", new Token("div"));
		lookup.put("do", new Token("do"));
		lookup.put("else", new Token("else"));
		lookup.put("end", new Token("end"));
		lookup.put("function", new Token("function"));
		lookup.put("if", new Token("if"));
		lookup.put("integer", new Token("integer"));
		lookup.put("mod", new Token("mod"));
		lookup.put("not", new Token("not"));
		lookup.put("of", new Token("of"));
		lookup.put("or", new Token("or"));
		lookup.put("procedure", new Token("procedure"));
		lookup.put("program", new Token("program"));
		lookup.put("real", new Token("real"));
		lookup.put("then", new Token("then"));
		lookup.put("var", new Token("var"));
		lookup.put("while", new Token("while"));
		lookup.put(";", new Token(";"));
		lookup.put(",", new Token(","));
		lookup.put(".", new Token("."));
		lookup.put(":", new Token(":"));
		lookup.put("[", new Token("["));
		lookup.put("]", new Token("]"));
		lookup.put("(", new Token("("));
		lookup.put(")", new Token(")"));
		lookup.put("+", new Token("+"));
		lookup.put("-", new Token("-"));
		lookup.put("=", new Token("="));
		lookup.put("<>", new Token("<>"));
		lookup.put("<", new Token("<"));
		lookup.put("<=", new Token("<="));
		lookup.put(">", new Token(">"));
		lookup.put(">=", new Token(">="));
		lookup.put("*", new Token("*"));
		lookup.put("/", new Token("/"));
		lookup.put(":=", new Token(":="));
	}
	
	public boolean isToken(String word){
		if(lookup.containsKey(word)){
			return true;
		}else{
			return false;
		}
	}
	
	public Token getToken(String word){
		return lookup.get(word);
	}
}
