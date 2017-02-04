package parser;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class MyParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testProgram() {
		File currentDir = new File(System.getProperty("user.dir"));
	    File newFile = new File(currentDir,"/res/simple.pas");
	    MyParser parser = new MyParser(newFile.toString(), true);
	    parser.program();
	}

}
