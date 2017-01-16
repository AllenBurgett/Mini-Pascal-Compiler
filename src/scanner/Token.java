package scanner;

public class Token
{
    private String contents;
    
    public Token( String input)
    {
        this.contents = input;
    };
    
    public String getLexeme() { return this.contents;}
    
    @Override
    public String toString() { return "Token: " + this.contents;}
}
