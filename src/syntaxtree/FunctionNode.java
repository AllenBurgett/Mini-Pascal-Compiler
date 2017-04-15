package syntaxtree;

import java.util.ArrayList;

import scanner.Keywords;

/**
 * Created by Allen Burgett
 */
public class FunctionNode extends VariableNode{
    /** The name of the variable associated with this node. */
    private ArrayList<ExpressionNode> expNodes;

    /**
     * Initializes a FunctionNode. Invokes the VariableNode
     * Constructor to declare the name and type.
     * @param attr, the name of this function.
     * @param type, Integer or Real.
     */
    public FunctionNode( String attr, Keywords type) {
        super(attr, type);
        expNodes = new ArrayList<ExpressionNode>();
    }

    /**
     * Returns the name of the function.
     * @return The name of this FunxtionNode.
     */
    public String getName() { return( super.getName());}
    
    /**
     * A list ExpressionNodes that represent the specified arguments 
     * passed to the FunctionNode.
     * @return a list of ExpressionNodes..
     */
    public ArrayList<ExpressionNode> getExpNode(){return this.expNodes;}

    /**
     * Set the list of arguments that are being passed to the FunctionNode. 
     * @param input, a list of arguments represented as ExpressionNodes.
     */
    public void setExpNode(ArrayList<ExpressionNode> input){this.expNodes = input;}

    /**
     * Add a single argument to the end of the list.
     * @param input, a single ExpreesionNode.
     */
    public void addExpNode(ExpressionNode input){expNodes.add(input);}

    /**
     * Add a list of arguments to the end of the list.
     * @param input, a list of ExpressionNodes.
     */
    public void addAll(ArrayList<ExpressionNode> input){expNodes.addAll(input);}
    /**
     * Returns the name of the variable as the description of this node.
     * @return The attribute String of this node.
     */
    @Override
    public String toString() {
        return( "VariableNode: " + super.name + " of type " + super.type.toString() +  " ExpressionNode: " + expNodes);
    }

    @Override
    public String indentedToString( int level) {
        String answer = this.indentation(level);
        answer += "Name: " + super.name + "\n";
        answer += this.indentation(level);
        answer += "Arguments: \n";
        for( ExpressionNode expression : expNodes) {
            answer += expression.indentedToString( level + 1);
        }
        return answer;
    }

    @Override
    public boolean equals( Object o) {
        boolean answer = false;
        if( o instanceof FunctionNode) {
            FunctionNode other = (FunctionNode)o;
            if( super.name.equals( other.getName()) && ( this.expNodes.equals(other.getExpNode())))
                answer = true;
        }
        return answer;
    }
}
