package syntaxtree;

import scanner.Keywords;

/**
 * Created by Allen Burgett
 */
public class UnaryOperationNode extends ExpressionNode {

    /** The right operator of this operation. */
    private ExpressionNode expression;

    /** The kind of operation. */
    private Keywords operation;

    /**
     * Creates an operation node given an operation token.
     * @param op The token representing this node's math operation.
     */
    public UnaryOperationNode ( Keywords op) {
        this.operation = op;
    }


    public ExpressionNode getExpression() { return( this.expression);}
    public Keywords getOperation() { return( this.operation);}

    public void setExpression( ExpressionNode node) {
        // Check for left and remove from child list, if left present.
        this.expression = node;
    }
    public void setOperation( Keywords op) { this.operation = op;}

    /**
     * Returns the operation token as a String.
     * @return The String version of the operation token.
     */
    @Override
    public String toString() {
        return operation.toString();
    }

    @Override
    public String indentedToString( int level) {
        String answer = this.indentation(level);
        answer += "Operation: " + this.operation + "\n";
        answer += expression.indentedToString(level + 1);
        return( answer);
    }

    @Override
    public boolean equals( Object o) {
        boolean answer = false;
        if( o instanceof syntaxtree.OperationNode) {
            syntaxtree.UnaryOperationNode other = (syntaxtree.UnaryOperationNode)o;
            if( (this.operation == other.operation) && (this.expression.equals( other.expression)))
                     answer = true;
        }
        return answer;
    }
}
