package syntaxtree;

import java.util.ArrayList;

import scanner.Keywords;

/**
 * Represents a sub program in Mini-Pascal. Similar in 
 * structure to a ProgramNode, but some sub programs can
 * have return types.
 * @author Allen Burgett
 *
 */
public class SubProgramNode extends SyntaxTreeNode{
	private String name;
	private ArrayList<VariableNode> arguments;
	private int numOfArgs;
    private DeclarationsNode variables;
    private SubProgramDeclarationsNode functions;
    private CompoundStatementNode main;
    private Keywords subType;
    private Keywords returnType = null;
    
    /**
     * Initialized a SubProgramNode.
     * @param aName, name of the sub program.
     * @param subType, type of sub program: Procedure or Function..
     */
    public SubProgramNode( String aName, Keywords subType) {
        this.name = aName;
        this.subType = subType;
    }
    
    public void setArguments( ArrayList<VariableNode> args){
    	this.arguments = args;
    	this.numOfArgs = args.size();
    }
    
    public ArrayList<VariableNode> getArguments(){
    	return arguments;
    }
    
    public int getNumOfArgs(){
    	return numOfArgs;
    }
    
    public String getName(){
    	return this.name;
    }

    public DeclarationsNode getVariables() {
        return variables;
    }

    public void setVariables(DeclarationsNode variables) {
        this.variables = variables;
    }

    public SubProgramDeclarationsNode getFunctions() {
        return functions;
    }

    public void setFunctions(SubProgramDeclarationsNode functions) {
        this.functions = functions;
    }

    public CompoundStatementNode getMain() {
        return main;
    }

    public void setMain(CompoundStatementNode main) {
        this.main = main;
    }
    
    public void setReturnType( Keywords type){
    	returnType = type;
    }
    
    public Keywords getReturnType(){
    	return returnType;
    }
    
    public Keywords getSubType(){
    	return subType;
    }
	
	@Override
    public String indentedToString( int level) {
        String answer = this.indentation( level);
        answer += "Program: " + name + "\n";
        answer += variables.indentedToString( level + 1);
        answer += functions.indentedToString( level + 1);
        answer += main.indentedToString( level + 1);
        return answer;
    }
}
