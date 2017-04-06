package syntaxtree;

import java.util.ArrayList;

/**
 * Represents a single procedure statement.
 * @author Allen Burgett
 */
public class ProcedureStatementNode extends StatementNode {

    private VariableNode lvalue;
    private ArrayList<ExpressionNode> expressions = new ArrayList<ExpressionNode>();

    public void setLvalue(VariableNode input){
    	this.lvalue = input;
    }
    
    public void setExpressions(ArrayList<ExpressionNode> input){
    	this.expressions = input;
    }

    public void addExpression(ExpressionNode input){
    	expressions.add(input);
    }
    
    public void addAllExpNode(ArrayList<ExpressionNode> input){
    	expressions.addAll(input);
    }

    public VariableNode getLvalue(){
    	return this.lvalue;
    }
    
    public ArrayList<ExpressionNode> getExpressions(){
    	return this.expressions;
    }

    @Override
    public String indentedToString( int level) {
        String answer = this.indentation( level);
        answer += "ProcedureName: " + this.lvalue + "\n";
        for( ExpressionNode exp : expressions) {
            answer += exp.indentedToString( level + 1);
        }
        return answer;
    }
}
