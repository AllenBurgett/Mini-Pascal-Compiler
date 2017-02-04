package scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class MyScannerTest {
	
	FileInputStream fis;
	Reader fileReader;
	ArrayList<Keywords> expecteds;

	@Before
	public void setUp() throws Exception {
		File currentDir = new File(System.getProperty("user.dir"));
	    File newFile = new File(currentDir,"/res/simple.pas");
		fis = new FileInputStream(newFile);
		fileReader = new InputStreamReader(fis, "UTF-8");
		expecteds = buildTest();
	}

	@Test
	public void testNextToken() throws IOException {
		MyScanner instance = new MyScanner(fileReader);
		for(Keywords expected : expecteds){
			System.out.println(expected);
			Keywords actual = instance.nextToken().getType();
			assertEquals(expected, actual);
		}
	}
	
	public ArrayList<Keywords> buildTest(){
		ArrayList<Keywords> expecteds = new ArrayList<Keywords>();
		expecteds.add(Keywords.PROGRAM);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.SEMI_COLON);
		expecteds.add(Keywords.VAR);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.COMMA);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.COMMA);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.COMMA);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.COLON);
		expecteds.add(Keywords.INTEGER);
		expecteds.add(Keywords.SEMI_COLON);
		expecteds.add(Keywords.BEGIN);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.ASSIGNMENT_OPERATOR);
		expecteds.add(Keywords.NUMBER);
		expecteds.add(Keywords.SEMI_COLON);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.ASSIGNMENT_OPERATOR);
		expecteds.add(Keywords.NUMBER);
		expecteds.add(Keywords.SEMI_COLON);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.ASSIGNMENT_OPERATOR);
		expecteds.add(Keywords.NUMBER);
		expecteds.add(Keywords.TIMES);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.PLUS);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.SEMI_COLON);
		expecteds.add(Keywords.IF);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.LESS_THAN);
		expecteds.add(Keywords.NUMBER);
		expecteds.add(Keywords.THEN);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.ASSIGNMENT_OPERATOR);
		expecteds.add(Keywords.NUMBER);
		expecteds.add(Keywords.ELSE);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.ASSIGNMENT_OPERATOR);
		expecteds.add(Keywords.NUMBER);
		expecteds.add(Keywords.SEMI_COLON);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.LEFT_PARENTHESES);
		expecteds.add(Keywords.ID);
		expecteds.add(Keywords.RIGHT_PARENTHESES);
		expecteds.add(Keywords.END);
		expecteds.add(Keywords.PERIOD);
		
		return expecteds;
	}

}
