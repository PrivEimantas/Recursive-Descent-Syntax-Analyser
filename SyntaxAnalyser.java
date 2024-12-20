import java.io.* ;


public class SyntaxAnalyser extends AbstractSyntaxAnalyser{

	/*
	 * @author Eimantas Lavickas
	 * Implementation for the syntax analyser
	 */

    /** The lexical analyser to process input using. */
	LexicalAnalyser lex ;
	/** A cache of the token to be processed next. */
	Token nextToken ;
	/** A code generator, descendant of AbstractGenerate. */
	Generate myGenerate = null;

	Boolean breakImmedietly=false;
    public String fileName;

    public SyntaxAnalyser(String fileName) throws IOException { //Constructor method for when the syntax analyser is first called
        this.fileName = fileName; 
		this.lex = new LexicalAnalyser(fileName); //Will need the lexical analyser to look at tokens so we construct this as well
	}


	public void _factor_() throws IOException, CompilationException{ //Every factor can contain either a terminal of identifier, numberconstant
		try {
			myGenerate.commenceNonterminal("Factor"); //Or it can contain a non-terminal 'expression' which must be contained by parentheses
		switch (nextToken.symbol) { //Start every method with a commenceNonTerminal method as above, with the argument being the non-terminal name
			case Token.identifier: //Every factor follows a grammar structure based on coursework spec
				acceptTerminal(Token.identifier);  //If a different symbol is found than the one we expect, an error would be thrown
				break;
			case Token.numberConstant:
				acceptTerminal(Token.numberConstant);
				break;
			case Token.leftParenthesis:
				acceptTerminal(Token.leftParenthesis);
				_expression_(); //For each non-terminal we call a method handler to do this for us
				acceptTerminal(Token.rightParenthesis);
				break;
			default: //Every factor must contain these terminals to start off another non-terminal, otherwise is incorrect
				String msg = "Got unexpected token { "+Token.getName(nextToken.symbol) + " } on line "+Integer.toString(nextToken.lineNumber);
				myGenerate.reportError(nextToken,msg); //call generate to report an error found
				
			
		}
		myGenerate.finishNonterminal("Factor"); //end factor so output this
		} catch (CompilationException e) { //catch inside a try catch file if any methods above thrown an error, builds a recursive stack
			
			throw new CompilationException("error in factor in "+fileName,e);
			// TODO: handle exception
		}
		
		
	}

	public void _term_() throws IOException, CompilationException{ //Every term always contains a factor which can be on its own
		try {
			myGenerate.commenceNonterminal("Term"); //Or it can be divided or multiplied by another term so we call another term
		_factor_();
		switch (nextToken.symbol) { //Must be either of these or is just factor on its own as per grammar structure
			case Token.timesSymbol:
				acceptTerminal(Token.timesSymbol); //Similarly check again if the symbol is one we expect, if so it will continue on
				_term_(); //then call the non-terminal method
				
				break;
			case Token.divideSymbol:
				acceptTerminal(Token.divideSymbol);
				_term_();
				break;
			
		}
		myGenerate.finishNonterminal("Term"); //end term non-terminal so output this
		} catch (CompilationException e) { //if any methods above have an issue, throw and catch here which builds a recursive stack
			throw new CompilationException("error in term in "+fileName,e);
			// TODO: handle exception
		}
		
	}

	public void _expression_() throws IOException, CompilationException{ //Expressions follow the same structure which is non-terminals
		try {
			myGenerate.commenceNonterminal("Expression"); //every expression always contains a term non-terminal
		_term_(); //hence we always call this first

		switch (nextToken.symbol) { //after a term, it may or may not contain another symbol so we perform checks
			case Token.plusSymbol:
				
				acceptTerminal(Token.plusSymbol);
				_expression_();
				
				break;
			case Token.minusSymbol:
				//_expression_();
				acceptTerminal(Token.minusSymbol);
				_expression_();
				
				break;
			
		}
		myGenerate.finishNonterminal("Expression"); //end expression so output this
		} catch (CompilationException e) { //if any methods above thrown an error  then catch and handle here
			throw new CompilationException("error in expression in "+fileName,e);
			// TODO: handle exception
		}
		
	}

	public void _assignmentStatement_()throws IOException, CompilationException{
		try {
			myGenerate.commenceNonterminal("AssignmentStatement"); //Assignment statements follow same structure
		acceptTerminal(Token.identifier); //identifier followed by becomes symbol to signify assignment of a variable
		acceptTerminal(Token.becomesSymbol);
		switch (nextToken.symbol) { // It can be a simple string or an expression which is a non-terminal
			case Token.stringConstant:
				acceptTerminal(Token.stringConstant); //If we get a different symbol than we expect an error will be thrown
				break;
		
			default:
				_expression_();
				break;
		}
		myGenerate.finishNonterminal("AssignmentStatement"); //end assignment statement so output this
		} catch (CompilationException e) {
			throw new CompilationException("error in assingment statement in "+fileName,e);
			// TODO: handle exception
		}
		
	}

	public void _ifStatement_()throws IOException, CompilationException{ //Every if statement follows same structure, an 'if' followed by condition by 'then'
		try {
			myGenerate.commenceNonterminal("IfStatement");// followed by a statement list, HOWEVER splits afterwards which can contain an if statement or otherwise an 'end if' token
		acceptTerminal(Token.ifSymbol);
		_condition_();
		acceptTerminal(Token.thenSymbol);
		_statementList_();
		switch (nextToken.symbol) { //Use switch cases which is faster than if statement as per coursework pec and lectures
			case Token.elseSymbol:
				_statementList_(); //which then as per grammar structure follows this routine of non-terminals and terminals
				acceptTerminal(Token.endSymbol);
				acceptTerminal(Token.ifSymbol);
				break;

			case Token.endSymbol: //similarly here, if ends if an end it must be 'end if' terminals
				acceptTerminal(Token.endSymbol);
				acceptTerminal(Token.ifSymbol);
		
			default://an if statement must end with these non-terminals mentioned above, otherwise it is incorrect and so throw this error
				String msg = "Got unexpected token {"+Token.getName(nextToken.symbol) + " } on line "+Integer.toString(nextToken.lineNumber);
				myGenerate.reportError(nextToken,msg);
				//throw new CompilationException(fileName,new CompilationException(msg)); 
				
		}

		myGenerate.commenceNonterminal("IfStatement"); //end if statement so output this
		} catch (CompilationException e) {
			// TODO: handle exception
			throw new CompilationException("Error caused by if statement in "+fileName,e);
			
		}
		
	}

	public void _whileStatement_() throws IOException, CompilationException{ //while statements always follow the same structure 
		try {
			myGenerate.commenceNonterminal("WhileStatement"); //so called the terminals of 'while','loop','end','loop'
		acceptTerminal(Token.whileSymbol); //we call the non-terminal condition
		_condition_();
		acceptTerminal(Token.loopSymbol);
		_statementList_();
		acceptTerminal(Token.endSymbol);
		acceptTerminal(Token.loopSymbol);		
		myGenerate.finishNonterminal("WhileStatement"); //end while statement so output this
		} catch (CompilationException e) {
			throw new CompilationException("error in while statement in "+fileName,e);
			// TODO: handle exception
		}
		
	}

	public void _argumentList_() throws IOException, CompilationException{//followed by an identifier which if we have more than one
		try {
			myGenerate.commenceNonterminal("ArgumentList"); // it will be followed by a comma and another identifier
		acceptTerminal(Token.identifier);

		switch (nextToken.symbol) { //check if we need to continue checking for more identifiers
			case Token.commaSymbol:
				acceptTerminal(Token.commaSymbol);
				_argumentList_();
				break;
		
		}

		myGenerate.finishNonterminal("ArgumentList"); //end argument list so output this
		} catch (CompilationException e) {
			throw new CompilationException("error in argument list in "+fileName,e);
			// TODO: handle exception
		}
		
	}

	public void _procedureStatement_()throws IOException, CompilationException{ //follows the same structure of a call symbol, by identifier, and then in parenthesis the actual arguments
		try {
			myGenerate.commenceNonterminal("ProcedureStatement");
		acceptTerminal(Token.callSymbol);
		acceptTerminal(Token.identifier);
		acceptTerminal(Token.leftParenthesis);
		_argumentList_();
		acceptTerminal(Token.rightParenthesis);
		myGenerate.finishNonterminal("ProcedureStatement"); //end procedure so output this
		} catch (CompilationException e) { //catch and handle here if error thrown
			throw new CompilationException("Error in procedure statement in "+fileName,e);
			// TODO: handle exception
		}
		
	}

	
	public void _untilStatement_() throws IOException, CompilationException{ //Every until statement follows same order
		try {
			myGenerate.commenceNonterminal("UntilStatement"); //which is 'do', statementList, 'until' by condition and then end
		acceptTerminal(Token.doSymbol);
		_statementList_();
		acceptTerminal(Token.untilSymbol);
		_condition_();
		myGenerate.commenceNonterminal("UntilStatement"); //end the non-terminal so we call this
		} catch (CompilationException e) {
			throw new CompilationException("Error in until statement in "+fileName,e);
			// TODO: handle exception
		}
		
	}

	public void _conditionalOperator_() throws IOException, CompilationException{
		try {
			myGenerate.commenceNonterminal("ConditionalOperator"); //conditions can only be > | >= | = | /= | < | <=
		switch (nextToken.symbol) {// so if is anything else it must be incorrect
			case Token.greaterThanSymbol:
				acceptTerminal(Token.greaterThanSymbol);
				break;
			case Token.greaterEqualSymbol:
				acceptTerminal(Token.greaterEqualSymbol);
				break;
			case Token.equalSymbol:
				acceptTerminal(Token.equalSymbol);
				break;
			case Token.notEqualSymbol:
				acceptTerminal(Token.notEqualSymbol);
				break;
			case Token.lessThanSymbol:
				acceptTerminal(Token.lessThanSymbol);
				break;
			case Token.lessEqualSymbol:
				acceptTerminal(Token.lessEqualSymbol);
				break;
			default:
				// if none of the other equations, must be an error so throw this out
				String msg = "Got unexpected token { "+Token.getName(nextToken.symbol) + " } on line "+Integer.toString(nextToken.lineNumber);
				myGenerate.reportError(nextToken,msg);
				//throw new CompilationException(fileName,new CompilationException(msg)); 
				
		}
		myGenerate.finishNonterminal("ConditionalOperator"); //end conditional so output this
		} catch (CompilationException e) {
			// TODO: handle exception
			throw new CompilationException("Error caused by conditional operator in "+fileName,e);
		}
		
	}

	public void _condition_() throws IOException, CompilationException{ //Conditions follow an identifier followed by condition, then its either 
		try {
			myGenerate.commenceNonterminal("Condition");// an identifier, number constant or a string constant
		acceptTerminal(Token.identifier);
		_conditionalOperator_();
		switch (nextToken.symbol) { //switch case as declared in lectures and coursework spec
			case Token.identifier:
				acceptTerminal(Token.identifier);
				break;
			case Token.numberConstant:
				acceptTerminal(Token.numberConstant);
				break;
			case Token.stringConstant:
				acceptTerminal(Token.stringConstant);
				break;
			default:
				String msg = "Got unexpected token { "+Token.getName(nextToken.symbol) + " } on line "+Integer.toString(nextToken.lineNumber);
				myGenerate.reportError(nextToken,msg);
				//throw new CompilationException(fileName,new CompilationException(msg)); 
				//break;
		}

		myGenerate.finishNonterminal("Condition"); //end condition checks
		} catch (CompilationException e) {
			throw new CompilationException("Error in condition in "+fileName,e);
			// TODO: handle exception
		}
		
		
	}

	public void _forStatement_() throws IOException, CompilationException{ //every 'for' statement follows a linear structure of methods to other calls (non-terminals) and terminals
		try {
		myGenerate.commenceNonterminal("ForStatement");
		acceptTerminal(Token.forSymbol);
		acceptTerminal(Token.leftParenthesis);
		_assignmentStatement_();
		acceptTerminal(Token.semicolonSymbol);
		_condition_();
		acceptTerminal(Token.semicolonSymbol); 
		_assignmentStatement_(); 
		acceptTerminal(Token.rightParenthesis);
		acceptTerminal(Token.doSymbol);
		_statementList_();
		acceptTerminal(Token.endSymbol);
		acceptTerminal(Token.loopSymbol);
		myGenerate.finishNonterminal("ForStatement"); //end 'for' statement
		} catch (CompilationException e) { //if an error is thrown above in any methods, would be caught and handled here
			throw new CompilationException("Exception caused in 'for statement' in "+fileName,e);
			// TODO: handle exception
		}
		
	}

	public void _statement_() throws IOException, CompilationException{ //Statement rules, can only be an assignment,if,while,procedure,until,for
		
		try {
			myGenerate.commenceNonterminal("Statement");
			switch (nextToken.symbol) {
				case Token.identifier: //must be an assignment statement
					
					_assignmentStatement_();
					
					break;
				case Token.ifSymbol: //must be an if statement
					
					_ifStatement_();
					
					break;
				case Token.whileSymbol: //must be a while loop
					
					_whileStatement_();
					
					break;
				case Token.callSymbol: // procedure call
					
					_procedureStatement_();
					
					break;
				case Token.doSymbol: // until statement
					
					_untilStatement_();
					
					break;
				case Token.forSymbol: // for loop
					
					_forStatement_();
					
					break;
				default: // Not all result in having an error, for example an if statement must have certain things if otherwise not found but on default for others just continue especially if its a procedure call to another
					//If not a call for any of the above functions, must be incorrect
					String msg = "Got unexpected token {"+Token.getName(nextToken.symbol) + "} on line "+Integer.toString(nextToken.lineNumber);
					myGenerate.reportError(nextToken,msg); 
					
			}
			myGenerate.finishNonterminal("Statement"); //end statement so output this	
		} catch (CompilationException e) {
			throw new CompilationException("Exception caused by statement in "+fileName,e);
			// TODO: handle exception
		}
		
	}

	public void _statementList_() throws IOException, CompilationException{ //Statement list always follows a statement followed by ';' if followed by another statement
		try {
			myGenerate.commenceNonterminal("StatementList"); // Otherwise is final statement of statement list
			_statement_(); //every statement list has a statement, it may or may not be the final statement
		
		switch (nextToken.symbol) { //Check for semicolons as per instructions above
			case Token.semicolonSymbol: 
				//_statementList_();
				acceptTerminal(Token.semicolonSymbol);
				_statementList_();
				break;
			
		}
		myGenerate.finishNonterminal("StatementList"); //end statement list so output this
		} catch (CompilationException e) { //catch any compilation errors thrown by methods above
			throw new CompilationException("Error in statement list in "+fileName,e);
			// TODO: handle exception
		}
		
	}

	/** Begin processing the first (top level) token.*/
	public void _statementPart_() throws IOException, CompilationException{
		try {
			// So when we start we check the first token, in statement part it is 'begin' symbol so if first thing is not this then report error
		myGenerate.commenceNonterminal("StatementPart");
		acceptTerminal(Token.beginSymbol); 
		_statementList_();
		acceptTerminal(Token.endSymbol);
		myGenerate.finishNonterminal("StatementPart"); //end statement part so output this
		} catch (CompilationException e) { //similarly if any errors reported from within methods above, this here would catch this and be handled
			throw new CompilationException("error in statement part in "+fileName,e);
			// TODO: handle exception
		}
		
    }



	/** Accept a token based on context.  Requires implementation. */
	public void acceptTerminal(int symbol) throws IOException, CompilationException{

		//symbol is 't' which is what we expect to find and nextSymbol is what is actually there
		try {
			if(nextToken.symbol==symbol){
				myGenerate.insertTerminal(nextToken);
				nextToken=lex.getNextToken();
			}
			else{ //If do not find what we needed then throw and error and report this
				String msg = "Got unexpected token { "+Token.getName(nextToken.symbol) + " } on line "+Integer.toString(nextToken.lineNumber) + " expected token: " + Token.getName(symbol);
				
				myGenerate.reportError(nextToken,msg);
				//throw new CompilationException(fileName,new CompilationException(msg));
			}
		} catch (CompilationException e) { //if an error is found inside the accept terminal method, it would throw an error from reportError inside generate and catches here
			
			throw new CompilationException("error in accept terminal in "+fileName,e);
			
			// TODO: handle exception
		}
		
    }

	/** Parses the given PrintStream with this instance's LexicalAnalyser.
		
	  @param ps The PrintStream object to read tokens from.
	  @throws IOException in the event that the PrintStream object can no longer read.
	*/
	public void parse( PrintStream ps ) throws IOException
	{
		
		myGenerate = new Generate();
		try {
			nextToken = lex.getNextToken();
			
			_statementPart_() ;
			acceptTerminal(Token.eofSymbol) ;
			//myGenerate.finishNonterminal("StatementPart");
			myGenerate.reportSuccess() ;
			//throw new CompilationException(fileName);
		}
		catch( CompilationException ex )
		{
			ps.println( "Compilation Exception" );
			ps.println( ex.toTraceString() );
		}
	} // end of method parse

}