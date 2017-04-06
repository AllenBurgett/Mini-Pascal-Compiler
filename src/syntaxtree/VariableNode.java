
package syntaxtree;

import scanner.Keywords;

/**
 * Represents a variable in the syntax tree.
 * @author Allen Burgett based on code by Erik Steinmetz
 */
public class VariableNode extends ExpressionNode {
    
    /** The name of the variable associated with this node. */
    protected String name;
    
    /**
     * Creates a ValueNode with the given attribute.
     * @param attr The attribute for this value node.
     */
    public VariableNode( String attr, Keywords type) {
        this.name = attr;
        super.type = type;
    }
    
    /** 
     * Returns the name of the variable of this node.
     * @return The name of this VariableNode.
     */
    public String getName() { return( this.name);}
    
    /**
     * Returns the name of the variable as the description of this node.
     * @return The attribute String of this node.
     */
    @Override
    public String toString() {
        return( name + " of type " + super.type);
    }
    
    @Override
    public String indentedToString( int level) {
        String answer = this.indentation(level);
        answer += "Name: " + this.name + " of type " + super.type.toString() + "\n";
        return answer;
    }

    @Override
    public boolean equals( Object o) {
        boolean answer = false;
        if( o instanceof VariableNode) {
            VariableNode other = (VariableNode)o;
            if( this.name.equals( other.name)) answer = true;
        }
        return answer;
    }    
    
}
