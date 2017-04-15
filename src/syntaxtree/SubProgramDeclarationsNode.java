
package syntaxtree;

import java.util.ArrayList;

/**
 * Represents a collection of subprogram declarations
 * @author Allen Burgett, based on code from Erik Steinmetz
 */
public class SubProgramDeclarationsNode extends SyntaxTreeNode {
    
    private ArrayList<SubProgramNode> procs = new ArrayList<SubProgramNode>();
    
    //single add
    public void addSubProgramDeclaration( SubProgramNode aSubProgram) {
        procs.add( aSubProgram);
    }
    
    //add all
    public void addAllSubProgramDeclarations( ArrayList<SubProgramNode> subs){
    	procs.addAll( subs);
    }
    
    //getter
    public ArrayList<SubProgramNode> getProcs(){
    	return this.procs;
    }
    
    public String indentedToString( int level) {
        String answer = this.indentation( level);
        answer += "SubProgramDeclarations\n";
        for( SubProgramNode subProg : procs) {
            answer += subProg.indentedToString( level + 1);
        }
        return answer;
    }
    
}
