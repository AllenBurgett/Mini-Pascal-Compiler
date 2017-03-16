package compiler;

import java.io.File;

import parser.MyParser;
import parser.Symbol;

public class CompilerMain {
	
	public static void main(String[] args){
		File currentDir = new File(System.getProperty("user.dir"));
	    File newFile = new File(currentDir,args[0]);
	    MyParser parser = new MyParser(newFile.toString(), true);
		boolean isProgram = parser.program();
		
		/*
		if( isProgram){
			System.out.println("Yes");
		}else{
			System.out.println("No");
		}
		
		for(Symbol symbol : parser.symbolTable.getSymbols()){
			System.out.println( symbol.toString());
		}
		*/
		
		System.out.print( parser.prog.indentedToString(0));
	}
}
