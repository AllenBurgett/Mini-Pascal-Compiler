
package syntaxtree;

import java.util.ArrayList;

/**
 * Represents a set of declarations in a Pascal program.
 * @author Allen Burgett based on code by Erik Steinmetz
 */
public class DeclarationsNode extends SyntaxTreeNode {
    
    private ArrayList<VariableNode> vars = new ArrayList<VariableNode>();
    
    /**
     * Add a single VariableNode to the declarations.
     * @param aVariable, a single declared ValueNode.
     */
    public void addVariable( VariableNode aVariable) {
        vars.add( aVariable);
    }
    
    /**
     * Add a list of VariableNodes to the current Declarations List.
     * @param vars, a list of declared VariableNodes.
     */
    public void addAllVariables( ArrayList<VariableNode> vars){
    	this.vars.addAll( vars);
    }
    
    /**
     * Get all declared VariableNodes associated with this Declaration.
     * @return a list of VariableNodes.
     */
    public ArrayList<VariableNode> getVars(){
    	return vars;
    }
    
    public String indentedToString( int level) {
    	String answer = this.indentation( level);
        answer += "Declarations\n";
        for( VariableNode variable : vars) {
            answer += variable.indentedToString( level + 1);
        }
        return answer;
    }
}
