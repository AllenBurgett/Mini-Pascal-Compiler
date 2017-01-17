package scanner;

public class Token
{
    private String contents;
    private String type;
    
    public Token( String input)
    {
        this.contents = input;
    };
    
    public String getLexeme() { return this.contents;}
    
    public void setType(String type) { this.type = type;}
    
    public String getType() { return this.type;}
    
    @Override
    public String toString() { return "Token: " + this.contents;}
}
