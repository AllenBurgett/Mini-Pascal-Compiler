package syntaxtree;

/**
 * Represents a while statement in Mini-Pascal
 * Contains a test and statements to do while the condition is true.
 * Similar to the IfStatementNode in structure, but without a false
 * statement. It contains an else count integer for labeling during 
 * code generation.
 * @author Allen Burgett
 *
 */
public class WhileStatementNode extends StatementNode {
    private ExpressionNode test;
    private StatementNode thenStatement;
    private int elseCount = 0;

    public WhileStatementNode( int elseCount){
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
    
    @Override
    public String indentedToString( int level) {
        String answer = this.indentation( level);
        answer += "WHILEt\n";
        answer += this.test.indentedToString( level + 1);
        answer += this.indentation( level);
        answer += "THEN";
        answer += this.thenStatement.indentedToString( level + 1);
        return answer;
    }

}