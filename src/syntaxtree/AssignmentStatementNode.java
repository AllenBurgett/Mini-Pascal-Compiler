
package syntaxtree;

/**
 * Represents a single assignment statement in Mini-PAscal.
 * An assignment statement is a statement where a left-side 
 * variable, as represented by a VariableNode or an ArrayNode,
 * is assigned a value based on a given right-side expression.
 * @author Erik Steinmetz
 */
public class AssignmentStatementNode extends StatementNode {
    
    private VariableNode lvalue;
    private ExpressionNode expression;

    /**
     * Returns the VariableNode that is being assigned..
     * @return VariableNode being assigned.
     */
    public VariableNode getLvalue() {
        return lvalue;
    }

    /**
     * Sets the VariableNode that will be assigned.
     * @param lvalue, the Variable that will be set.
     */
    public void setLvalue(VariableNode lvalue) {
        this.lvalue = lvalue;
    }

    /**
     * Returns the Expression that is being assigned.
     * @return the ExpressionNode that's being assigned to the variable.
     */
    public ExpressionNode getExpression() {
        return expression;
    }

    /**
     * Sets the Expression that will be assigned to the variable.
     * @param expression, the ExpressionNode this is being assigned to the variable.
     */
    public void setExpression(ExpressionNode expression) {
        this.expression = expression;
    }
    

    
    @Override
    public String indentedToString( int level) {
        String answer = this.indentation( level);
        answer += "Assignment\n";
        answer += this.lvalue.indentedToString( level + 1);
        answer += this.expression.indentedToString( level + 1);
        return answer;
    }
}
