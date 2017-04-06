package syntaxtree;

import scanner.Keywords;

public class ArrayNode extends VariableNode {
	private ExpressionNode expression;
	
	public ArrayNode(String name, Keywords type){
		super(name, type);
		this.expression = null;
	}

	public ExpressionNode getExpression() {
		return expression;
	}

	public void setExpression(ExpressionNode expression) {
		this.expression = expression;
	}
	
	/**
     * Returns the name of the array and the index expression as the description of this node.
     * @return The attribute String of this node.
     */
    @Override
    public String toString() {
        return( "VariableNode: " + super.name + " of type " + super.type.toString() +  " ExpressionNode: " + expression);
    }
    
    @Override 
    public String indentedToString( int level) {
        String answer = this.indentation(level);
        answer += "Name: " + this.name + "\n";
        answer += this.indentation(level);
        answer += "Position: \n" + expression.indentedToString(level + 1);
        return answer;
    }
	@Override
    public boolean equals( Object o) {
        boolean answer = false;
        if( o instanceof ArrayNode) {
            ArrayNode other = (ArrayNode)o;
            if( super.name.equals( other.getName()) && ( this.expression.equals( other.getExpression()))){
            	answer = true;
            }
        }
        return answer;
    }   
}
