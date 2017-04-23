package compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import codegen.CodeGenerator;
import parser.MyParser;
import parser.Symbol;

/**
 * Takes in a file, verifies that it is a properly structured Mini-Pascal
 * program and outputs the result as a MIPS representation of the the 
 * given program. The output file is named the program label as found in 
 * the first line of a Mini-Pascal program, Program [Program Name].
 * @author Allen Burgett
 *
 */
public class CompilerMain {
	
	/**
	 * Takes in one argument, the path of the Mini-Pascal code.
	 * @param args, file path from the command line point.
	 * @throws FileNotFoundException description
	 */
	public static void main(String[] args) throws FileNotFoundException{
		long startTime = System.currentTimeMillis();
		File currentDir = new File(System.getProperty("user.dir"));
	    File newFile = new File(currentDir,args[0]);
	    MyParser parser = new MyParser(newFile.toString(), true);
		boolean isProgram = parser.program();

//		//prints the contents of the global symbol table.
//		for(Symbol symbol : parser.symbolTable.getSymbols()){
//			System.out.println( symbol.toString());
//		}
		
//		//prints the tree built by the parser.
//		System.out.print(parser.prog.indentedToString(0));
		
		//case is a properly structured Mini_Pascal program.
		if( isProgram){
			//pass the Program Tree and Symbol Table for code generation
			CodeGenerator codeGen = new CodeGenerator( parser.prog, parser.symbolTable);
			//build an output string of MIPS code
			boolean successfulCompile = codeGen.compile();
			//case there were no problems with the code compilation.
			if( successfulCompile){
				//create buffer to write the MIPS code to
				PrintWriter writer = null;
				if( args.length > 1){
					File outDir = new File( args[1]);

					// if the directory does not exist, create it
					if (!outDir.exists()) {
					    System.out.println("Creating directory: " + outDir.getName());
					    boolean result = false;

					    try{
					        outDir.mkdir();
					        result = true;
					    } 
					    catch(SecurityException se){
					    	System.out.println("You do not have permission to create this directory.");
					    }        
					    if(result) {    
					        System.out.println("Directory created");  
					    }
					}
					
					writer = new PrintWriter( args[1] + parser.prog.getName() + ".asm");
				}else{
					writer = new PrintWriter( parser.prog.getName() + ".asm");
				}
				
				ArrayList<String> output = codeGen.getOutput();
				
				//write the output to the buffer.
				for(int i = 0; i < output.size(); i++){
					writer.print(output.get(i));
				}
				//close the buffer and save the output to the output file.
				writer.close();
				System.out.println("Compile Successful!");
				long endTime = System.currentTimeMillis();
				System.out.println("Complie took " + (endTime - startTime) + "ms to complete.");
			//case there was a problem on compile.
			}else{
				System.out.println("Compile time error.");
			}
		//case not a valid Mini-Pascal program.
		}else{
			System.out.println("Invalid Mini-Pascal Program.");
		}
	}
}
