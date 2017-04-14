
package syntaxtree;

/**
 * Represents an if statement in Pascal.
 * An if statement includes a boolean expression, and two statements.
 * @author Allen Burgett, based on code from Erik Steinmetz
 */
public class IfStatementNode extends StatementNode {
    private ExpressionNode test;
    private StatementNode thenStatement;
    private StatementNode elseStatement;
    private int elseCount = 0;
    
    public IfStatementNode( int elseCount){
    	this.elseCount = elseCount;
    }
    
    public int getElseCount(){
    	return elseCount;
    }

    public ExpressionNode getTest() {
        return test;
    }

    public void setTest(ExpressionNode test) {
        this.test = test;
    }

    public StatementNode getThenStatement() {
        return thenStatement;
    }

    public void setThenStatement(StatementNode thenStatement) {
        this.thenStatement = thenStatement;
    }

    public StatementNode getElseStatement() {
        return elseStatement;
    }

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
