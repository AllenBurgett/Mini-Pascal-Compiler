package parser;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class MyParserTest {

	@Test
	public void testProgram() {
		File currentDir = new File(System.getProperty("user.dir"));
	    File newFile = new File(currentDir,"/res/simple.pas");
	    MyParser parser = new MyParser(newFile.toString(), true);
	    parser.program();
	    System.out.println("Program Passed!");
	}

	@Test
	public void testDeclarations() {
		MyParser parser = new MyParser("var foo: real;", false);
		parser.declarations();
		System.out.println("Declarations Passed!");
	}

	@Test
	public void testSubprogram_declaration() {
		String test = "function test( bar:integer ) :integer;\n" + 
				"fig: array[3:3] of integer;\n" + "begin\n\nend\n\n.";
		MyParser parser = new MyParser(test, false);
		parser.subprogram_declaration();
		System.out.println("Subprogram Declaration Passed!");
		
		test = "procedure test( foo:real );\n"+"\tvar foo,fee,fii: array[3:3] of real;\n" 
				+ "\tbegin\n\n\tend\n.";		
		parser = new MyParser(test, false);
		parser.subprogram_declaration();
		System.out.println("Subprogram (procedure) Declaration Passed!");
	}

	@Test
	public void testOptional_statements() {
		String test = "poo := -232";
		MyParser parser = new MyParser(test, false);
		parser.optional_statements();
		System.out.println("Statement " + test + "Passed!");
		
		test = "if boots <= 9\nthen\n\tboots := 9\nelse\n\tboots := -1\n";
		parser = new MyParser(test, false);
		parser.optional_statements();
		System.out.println("Statement " + test + "Passed!");
		
		test = "begin\n\thearts := 3\nend\n";
		parser = new MyParser(test, false);
		parser.optional_statements();
		System.out.println("Statement " + test + "Passed!");
		
		test = "while something < 5\ndo\nbegin\n\tsomething := something + poo\nend\n";
		parser = new MyParser(test, false);
		parser.optional_statements();
		System.out.println("Statement " + test + "Passed!");
	}

	@Test
	public void testSimple_expression() {
		String test = "5 - 33 / 10 * 5";
		MyParser parser = new MyParser(test, false);
		parser.simple_expression();
		System.out.println("Statement " + test + " Passed!");
		
		test = "(-1) * love - 25 / 6 + 3";
		parser = new MyParser(test, false);
		parser.simple_expression();
		System.out.println("Statement " + test + " Passed!");
	}

	@Test
	public void testFactor() {
		String test = "number";
		MyParser parser = new MyParser(test, false);
		parser.simple_expression();
		System.out.println("Statement " + test + " Passed!");
		
		test = "number[dat - this + 10]";
		parser = new MyParser(test, false);
		parser.simple_expression();
		System.out.println("Statement " + test + " Passed!");
		
		test = "number(dat - this + 10)";
		parser = new MyParser(test, false);
		parser.simple_expression();
		System.out.println("Statement " + test + " Passed!");
		
		test = "3";
		parser = new MyParser(test, false);
		parser.simple_expression();
		System.out.println("Statement " + test + " Passed!");
		
		test = "(things - otherthings)";
		parser = new MyParser(test, false);
		parser.simple_expression();
		System.out.println("Statement " + test + " Passed!");
		
		test = "not something(nics + nacks)";
		parser = new MyParser(test, false);
		parser.simple_expression();
		System.out.println("Statement " + test + " Passed!");
	}

}
