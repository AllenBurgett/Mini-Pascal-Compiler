package compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import codegen.CodeGenerator;
import parser.MyParser;
import parser.Symbol;

public class CompilerMain {
	
	public static void main(String[] args) throws FileNotFoundException{
		File currentDir = new File(System.getProperty("user.dir"));
	    File newFile = new File(currentDir,args[0]);
	    MyParser parser = new MyParser(newFile.toString(), true);
		boolean isProgram = parser.program();
		
		if( isProgram){
			System.out.println("Yes");
		}else{
			System.out.println("No");
		}
		
		for(Symbol symbol : parser.symbolTable.getSymbols()){
			System.out.println( symbol.toString());
		}
		
//		System.out.print(parser.prog.indentedToString(0));
//		
//		if( isProgram){
//			CodeGenerator codeGen = new CodeGenerator( parser.prog, parser.symbolTable);
//			boolean successfulCompile = codeGen.compile();
//			if( successfulCompile){
//				PrintWriter writer = new PrintWriter( parser.prog.getName() + ".asm");
//				ArrayList<String> output = codeGen.getOutput();
//				
//				for(int i = 0; i < output.size(); i++){
//					writer.print(output.get(i));
//				}
//				writer.close();
//			}else{
//				System.out.println("Compile time error.");
//			}
//		}
	}
}
