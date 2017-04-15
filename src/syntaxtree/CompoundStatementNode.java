
package syntaxtree;

import java.util.ArrayList;

/**
 * Represents a compound statement in Pascal.
 * A compound statement is a block of zero or more
 * statements to be run sequentially.
 * @author ErikSteinmetz
 */
public class CompoundStatementNode extends StatementNode {
    
    private ArrayList<StatementNode> statements = new ArrayList<StatementNode>();
    
    /**
     * Add a single statement to the list of statements.
     * @param state, statement to add to the compound statement.
     */
    public void addStatement( StatementNode state) {
        this.statements.add( state);
    }
    
    /**
     * Return the list of statements.
     * @return a list of statements associated with the compound statement.
     */
    public ArrayList<StatementNode> getStatements(){
    	return this.statements;
    }
    
    public String indentedToString( int level) {
        String answer = this.indentation( level);
        answer += "Compound Statement\n";
        for( StatementNode state : statements) {
            answer += state.indentedToString( level + 1);
        }
        return answer;
    }
}
