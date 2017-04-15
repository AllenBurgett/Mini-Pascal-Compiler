package syntaxtree;

import scanner.Keywords;

/**
 * This node handles the tree for an element in an array.
 * @author Allen Burgett
 *
 */
public class ArrayNode extends VariableNode {
	private ExpressionNode expression;
	
	/**
	 * Initializes an ArrayNode.
	 * @param name, name of the array.
	 * @param type, Integer or Real.
	 */
	public ArrayNode(String name, Keywords type){
		super(name, type);
		this.expression = null;
	}

	/**
	 * Returns the ExpressionNode that represents the index of the element in the array.
	 * @return ExpressionNode of the index of the element in the array.
	 */
	public ExpressionNode getExpression() {
		return expression;
	}

	/**
	 * Sets the ExpressionNode that represents the index of the array element.
	 * @param expression, ExpressionNode to set the element in the array.
	 */
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
