package scanner;

/**
 * Pascal Scanner Spec
 */

/* Declarations */


%%

%standalone         /* The produced java file has a main */
%class  MyScanner   /* Names the produced java file */
%function nextToken /* Renames the yylex() function */
%type   Token      /* Defines the return type of the scanning function */
%{
    LUT lookUpTable = new LUT();
%}
%eofval{
  return null;
%eofval}
/* Patterns */
other             = .
letter            = [A-Za-z]
digit             = [0-9]
whitespace        = [ \n\t\r]|(([\{])([^\{])*([\}]))
symbol            = [;,.:\[\]()+-=<>\*/] 
id                = ({letter}+)({letter}|{digit})*
symbols           = {symbol}|:=|<=|>=|<>
digits            = ({digit})({digit}*)
optional_fraction = ([.])({digits})
optional_exponent = ([E]([+]|[-])?{digits})
number            = {digits}{optional_fraction}?{optional_exponent}?{optional_fraction}?

%%
/* Lexical Rules */

{id}     {
             /** Build and output word Token */
             
             Keywords key = lookUpTable.get(yytext());
             if(key != null){
                return new Token( yytext(), key);
             }else{
                return new Token( yytext(), Keywords.ID);
             }
            }
            
{number}    {
             /** Build and output number Token */
             
             return new Token(yytext(), Keywords.NUMBER);
            }
            
{symbols}    {
             /** Build and output syntax Token */             
             
                Keywords key = lookUpTable.get(yytext());
                return new Token( yytext(), key);
             
            }
            
{whitespace}  {  /* Ignore Whitespace */ 
                if(( yytext().charAt(0) == '{') && (yytext().charAt( yytext().length() - 1) == '}')){
                    System.out.println("Comment: " + yytext());
                }
              }

{other}    { 
             System.out.println("Invalid Symbol: " + yytext() + " found.");
           }