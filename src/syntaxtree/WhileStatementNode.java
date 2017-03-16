package syntaxtree;

public class WhileStatementNode extends StatementNode {
    private ExpressionNode test;
    private StatementNode thenStatement;

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
        answer += "Assignment\n";
        answer += this.test.indentedToString( level + 1);
        answer += this.thenStatement.indentedToString( level + 1);
        return answer;
    }

}