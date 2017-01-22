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
	ArrayList<String> expecteds;

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
		for(String expected : expecteds){
			System.out.println(expected);
			String actual = instance.nextToken().getType();
			assertEquals(expected, actual);
		}
	}
	
	public ArrayList<String> buildTest(){
		ArrayList<String> expecteds = new ArrayList<String>();
		expecteds.add("word");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("word");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("word");
		expecteds.add("syntax");
		expecteds.add("word");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("number");
		expecteds.add("syntax");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("number");
		expecteds.add("syntax");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("number");
		expecteds.add("syntax");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("word");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("number");
		expecteds.add("word");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("number");
		expecteds.add("word");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("number");
		expecteds.add("syntax");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("ID");
		expecteds.add("syntax");
		expecteds.add("word");
		expecteds.add("syntax");
		
		return expecteds;
	}

}
