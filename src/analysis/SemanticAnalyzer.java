package analysis;

import scanner.Keywords;
import syntaxtree.ExpressionNode;
import syntaxtree.OperationNode;
import syntaxtree.ValueNode;

/**
 * Semantic Analyzer of the Expression Compiler.
 * This class can perform the code folding analysis on an expression tree.
 * @author Allen Burgett, based on code from Erik Steinmetz 
 */
public class SemanticAnalyzer {
    
    /** Root node of the tree to analyze. */
    private ExpressionNode root;
    
    /**
     * Creates a SemanticAnalyzer with the given expression.
     * @param tree An expression tree to analyze.
     */
    public SemanticAnalyzer( ExpressionNode tree) {
        this.root = tree;
    }
    
    /**
     * Performs code folding on the expression tree.
     * For demonstration purposes, we only fold addition nodes.
     * @return The root node of the mutated tree.
     */
    public ExpressionNode codeFolding() {
        if( this.root instanceof OperationNode) {
            ExpressionNode rootier = codeFolding( (OperationNode)this.root);
            return rootier;
        }
        else {
            return (ExpressionNode) this.root;
        }
    }
    
    /**
     * Folds code for the given node.
     * We only fold if both children are value nodes, and the node itself
     * is a PLUS node.
     * @param node The node to check for possible efficiency improvements.
     * @return The folded node or the original node if nothing 
     */
    private ExpressionNode codeFolding( OperationNode node) {
    	if( node.getLeft() instanceof OperationNode) {
            node.setLeft( codeFolding( (OperationNode)node.getLeft()));
        }
        if( node.getRight() instanceof OperationNode) {
            node.setRight( codeFolding( (OperationNode)node.getRight()));
        }
        if( node.getLeft() instanceof ValueNode &&
                node.getRight() instanceof ValueNode){
    		ValueNode leftNode = ((ValueNode)node.getLeft());
    		ValueNode rightNode = ((ValueNode)node.getRight());
    		double leftValue = Double.parseDouble(leftNode.getAttribute());
    		double rightValue = Double.parseDouble(rightNode.getAttribute());
    		double val = 0.0;
        	switch( node.getOperation()){
        		case PLUS:
        			val = leftValue + rightValue;
        			break;
        		case MINUS:
        			val = leftValue - rightValue;
        			break;
        		case DIVIDE:
        			val = leftValue / rightValue;
        			break;
        		case TIMES:
        			val = leftValue * rightValue;
        			break;
        		case MOD:
        			val = leftValue % rightValue;
        			break;
        		case EQUALITY_OPERATOR:
    				val = ( leftValue == rightValue) ? 1 : 0;
    				break;
    			case NOT_EQUAL:
    				val = ( leftValue != rightValue) ? 1 : 0;
    				break;
    			case LESS_THAN:
    				val = ( leftValue < rightValue) ? 1 : 0;
    				break;
    			case LESS_THAN_EQUAL_TO:
    				val = ( leftValue <= rightValue) ? 1 : 0;
    				break;
    			case GREATER_THAN_EQUAL_TO:
    				val = ( leftValue >= rightValue) ? 1 : 0;
    				break;
    			case GREATER_THAN:
    				val = ( leftValue > rightValue) ? 1 : 0;
    				break;
        		default:
        			return node;
        	}
        	
        	ValueNode vn = null;
        	
        	if( leftNode.getType() == Keywords.INTEGER && rightNode.getType() == Keywords.INTEGER){
        		int int_val = (int) val;
        		vn = new ValueNode("" + int_val, Keywords.INTEGER);
        		vn.setType(Keywords.INTEGER);        		
        	}else{
        		vn = new ValueNode("" + val, Keywords.REAL);
        		vn.setType(Keywords.REAL);        		
        	}
        	
        	return vn;
        }
        else {
        	if(node.getLeft().getType() == Keywords.REAL || node.getRight().getType() == Keywords.REAL){
        		node.setType( Keywords.REAL);
        	}else{
        		node.setType( Keywords.INTEGER);
        	}
            return node;
        }
    }
}