
package syntaxtree;

/**
 * Represents an if statement in Mini-Pascal. An if statement 
 * includes a boolean expression, a true case statement, and
 * a false case statement..
 * @author Allen Burgett, based on code from Erik Steinmetz
 */
public class IfStatementNode extends StatementNode {
    private ExpressionNode test;
    private StatementNode thenStatement;
    private StatementNode elseStatement;
    private int elseCount;
    
    /**
     * Initializes an IfStatementNode with a positional integer
     * that is used for uniqueness of else labels in code generation.
     * @param elseCount, integer used for labeling.
     */
    public IfStatementNode( int elseCount){
    	this.elseCount = elseCount;
    }
    
    /**
     * Returns the integer label.
     * @return integer for labeling.
     */
    public int getElseCount(){
    	return elseCount;
    }

    /**
     * Boolean Expression that controls the if statement.
     * @return the controlling expression.
     */
    public ExpressionNode getTest() {
        return test;
    }

    /**
     * Set a boolean expression to control the if statement.
     * @param test, an ExpressonNode to be used as a controller.
     */
    public void setTest(ExpressionNode test) {
        this.test = test;
    }

    /**
     * Statement that should be processed if true.
     * @return the true StatementNode.
     */
    public StatementNode getThenStatement() {
        return thenStatement;
    }

    /**
     * Set the statement that should be processed if true.
     * @param thenStatement, the StatmentNode for the true case.
     */
    public void setThenStatement(StatementNode thenStatement) {
        this.thenStatement = thenStatement;
    }

    /**
     * Statement that should be processed if false.
     * @return the false StatementNode.
     */
    public StatementNode getElseStatement() {
        return elseStatement;
    }

    /**
     * Set the statement that should be processed if false.
     * @param elseStatement, the StatementNode for the false case.
     */
    public void setElseStatement(StatementNode elseStatement) {
        this.elseStatement = elseStatement;
    }
    
    @Override
    public String indentedToString( int level) {
        String answer = this.indentation( level);
        answer += "IF\n";
        answer += this.test.indentedToString( level + 1);
        answer += this.indentation( level);
        answer += "THEN\n";
        answer += this.thenStatement.indentedToString( level + 1);
        answer += this.indentation( level);
        answer += "ELSE\n";
        answer += this.elseStatement.indentedToString( level + 1);
        return answer;
    }

}
