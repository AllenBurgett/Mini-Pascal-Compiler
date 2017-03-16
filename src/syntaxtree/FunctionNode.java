package syntaxtree;

import java.util.ArrayList;

/**
 * Created by Allen Burgett
 */
public class FunctionNode extends VariableNode{
    /** The name of the variable associated with this node. */
    private ArrayList<ExpressionNode> expNode;

    /**
     * Creates an ArrayNode and the parent variableNode with the given attribute.
     * @param attr The attribute for this value node.
     */
    public FunctionNode( String attr) {
        super(attr);
        expNode = null;
    }

    /**
     * Returns the name of the variable of this node.
     * @return The name of this VariableNode.
     */
    public String getName() { return( super.name);}
    //TODO add javadoc
    public ArrayList<ExpressionNode> getExpNode(){return this.expNode;}

    //TODO add javadoc
    public void setExpNode(ArrayList<ExpressionNode> input){this.expNode = input;}

    //add one
    public void addExpNode(ExpressionNode input){expNode.add(input);}

    //add all
    public void addAll(ArrayList<ExpressionNode> input){expNode.addAll(input);}
    /**
     * Returns the name of the variable as the description of this node.
     * @return The attribute String of this node.
     */
    @Override
    public String toString() {
        return( "VariableNode: " + super.name + "ExpressionNode: " + expNode);
    }

    @Override
    public String indentedToString( int level) {
        String answer = this.indentation(level);
        answer += "Name: " + super.name + "\n";
        answer += this.indentation(level);
        answer += "Arguments: \n";
        for( ExpressionNode expression : expNode) {
            answer += expression.indentedToString( level + 1);
        }
        return answer;
    }

    @Override
    public boolean equals( Object o) {
        boolean answer = false;
        if( o instanceof FunctionNode) {
            FunctionNode other = (FunctionNode)o;
            if( super.name.equals( other.getName()) && ( this.expNode.equals(other.getExpNode())))
                answer = true;
        }
        return answer;
    }
}
