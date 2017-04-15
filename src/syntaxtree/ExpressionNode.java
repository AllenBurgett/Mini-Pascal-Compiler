package syntaxtree;

import scanner.Keywords;

/**
 * General representation of any expression.
 * @author Allen Burgett
 */
public abstract class ExpressionNode extends SyntaxTreeNode {
    protected Keywords type = null;
    
    /**
     * Sets the type for the entire expression.
     * @param type, Integer or Real
     */
    public void setType( Keywords type){
    	this.type = type;
    }
    
    /**
     * Returns the type of the entire expression.
     * @return, Keywords Integer or Real.
     */
    public Keywords getType(){
    	return this.type;
    }
}
