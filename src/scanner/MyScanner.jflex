package scanner;

/**
 * Pascal Scanner Spec
 */

/* Declarations */


%%

%standalone         /* The produced java file has a main */
%class  MyScanner   /* Names the produced java file */
%function nextToken /* Renames the yylex() function */
%type   String      /* Defines the return type of the scanning function */
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
                return currentToken.toString();
             }else{
                System.out.println("Word is not a Token: " + yytext());
             }
            }
            
{number}    {
             /** Build and output number Token */
             
             Token currentToken = new Token( yytext());
             currentToken.setType("number");
             return( currentToken.toString());
            }
            
{syntax}    {
             /** Build and output syntax Token */
             
             if(lookUpTable.isToken(yytext())){
                Token currentToken = lookUpTable.getToken(yytext());
                currentToken.setType("syntax");
                return currentToken.toString();
             }else{
                System.out.println("Syntax is not a Token: " + yytext());
             }
            }
            
{whitespace}  {  /* Ignore Whitespace */ 
                 return "";
              }

{other}    { 
             System.out.println("Illegal char: '" + yytext() + "' found.");
             return "";
           }