public class Generate extends AbstractGenerate{


    public Generate(){
    }

    
    public void insertTerminal( Token token ) {
        String tt = Token.getName( token.symbol );
        
        if( (token.symbol == Token.identifier) || (token.symbol == Token.numberConstant) || (token.symbol == Token.stringConstant) )
            tt += " '" + token.text + "'";

        tt += " on line " + token.lineNumber;

        System.out.println( "312TOKEN " + tt );
    } // end of method insertTerminal

    /**
    *
    * commenceNonterminal
    *
    **/

    public void commenceNonterminal( String name ) {
        System.out.println( "312BEGIN " + name );
    } // end of method commenceNonterminal

    /**
    *
    * finishNonterminal
    *
    **/

    public void finishNonterminal( String name ) {
        System.out.println( "312END " + name );
    } // end of method finishNonterminal

    /**
    *
    * reportSuccess
    *
    **/

    public void reportSuccess()
    {
        System.out.println( "312SUCCESS" );
    } // end of method reportSuccess


    /** Report an error to the user. */
    public void reportError( Token token, String explanatoryMessage ) throws CompilationException{
        System.out.println("Error caused in "+explanatoryMessage);
        //System.out.println("Encountered an error..");
        //System.out.println("Got unexpected token { "+Token.getName(token.symbol) + " } on line "+Integer.toString(token.lineNumber) );    
        //throw new CompilationException("Got unexpected token { "+Token.getName(token.symbol) + " } on line "+Integer.toString(token.lineNumber));
        throw new CompilationException(explanatoryMessage);
    }
}