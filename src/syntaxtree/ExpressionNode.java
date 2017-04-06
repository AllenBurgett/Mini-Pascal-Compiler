package syntaxtree;

import scanner.Keywords;

/**
 * General representation of any expression.
 * @author Allen Burgett
 */
public abstract class ExpressionNode extends SyntaxTreeNode {
    protected Keywords type = null;
    
    public void setType( Keywords type){
    	this.type = type;
    }
    
    public Keywords getType(){
    	return this.type;
    }
}
