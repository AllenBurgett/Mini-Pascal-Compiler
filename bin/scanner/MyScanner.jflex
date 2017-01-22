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

other         = .
letter        = [A-Za-z]
word          = {letter}+
digit         = [0-9]
number        = {digit}+
symbol        = [;,.\[\]\(\)+\-*/]
comparator    = [<>]
colon         = [:]
equal         = [=]
syntax        = {symbol}|{comparator}|{colon}|{equal}|{colon}{equal}
whitespace    = [ \r\n\t]

%%
/* Lexical Rules */

{word}     {
             /** Build and output word Token */
             
             if(lookUpTable.isToken(yytext())){
                Token currentToken = lookUpTable.getToken(yytext());
                currentToken.setType("word");
                return currentToken;
             }else{
                Token currentToken = new Token( yytext());
                currentToken.setType("ID");
                return currentToken;
             }
            }
            
{number}    {
             /** Build and output number Token */
             
             Token currentToken = new Token( yytext());
             currentToken.setType("number");
             return currentToken;
            }
            
{syntax}    {
             /** Build and output syntax Token */
             
             
                Token currentToken = new Token(";");
                currentToken.setType("syntax");
                return currentToken;
             
            }
            
{whitespace}  {  /* Ignore Whitespace */ 
            
              }

{other}    { 
             
           }