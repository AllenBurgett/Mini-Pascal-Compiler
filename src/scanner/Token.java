package scanner;

/**
 * 
 * @author Allen Burgett
 * Token contains the original string and type as defined
 * by the LUT
 *
 */

public class Token
{
    private String contents;
    private Keywords type;
    
    public Token( String input, Keywords type)
    {
        this.contents = input;
        this.type = type;
    };
    
    public String getLexeme() { return this.contents;}
    
    public Keywords getType() { return this.type;}
    
    @Override
    public String toString() { return "Token: " + this.contents;}
}
