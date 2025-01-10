package pro;

class Parser{
    private Token curHanTok;
    private Lexer lexer;

    public Parser(Lexer lexer){
        this.lexer = lexer;
        this.curHanTok = lexer.getNextToken();
    }
    public void parse() {
        // begin parsing
    	dayehDecl();
        // to check if the last Token is exit to ensure the whole input has parsed correctly 
        if (curHanTok.getType() != Token.TokenType.EXIT) {
            System.out.println("Unexpected tokens at the end of input.");
            System.exit(0);
        }
    }
    private void matchHanToken(Token.TokenType tokenType){
        if (curHanTok.getType() == tokenType){
            curHanTok = lexer.getNextToken();
        } else {
            errorFun("Syntax error: Expected " + tokenType + " but found " + curHanTok.getType());
        }
    }
    private void errorFun(String message) {
      System.out.println("Error on line " + curHanTok.getLine() + ": " + message + ". Token: " + curHanTok);
      System.exit(0);
  }


    private void dayehDecl() {
        hanStart();
        declarations();
        functionDecl();  
        block();
        name();
    	   }

    private void hanStart() {
        while (curHanTok.getType() == Token.TokenType.HASH) {
        	matchHanToken(Token.TokenType.HASH);
        	matchHanToken(Token.TokenType.INCLUDE);
        	matchHanToken(Token.TokenType.LESS_THAN);
            name();
            matchHanToken(Token.TokenType.GREATER_THAN);
            matchHanToken(Token.TokenType.SEMICOLON);
        }
    }

    private void name() {
    	if (curHanTok.getType() == Token.TokenType.EXIT) {
            exitStmt(); 
            return;
        }
    	if (curHanTok.getType() != Token.TokenType.IDENTIFIER) {
            errorFun("Expected an identifier but found " + curHanTok.getValue());
        }
    	
        String tokenValue = curHanTok.getValue();
        // check if the token matche the pattern for a name following to the grammar rule
        if (tokenValue.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
        	matchHanToken(Token.TokenType.IDENTIFIER); 
        } else {
            // if the value does not match the pattern throw a error.
            errorFun("Name does not match the required pattern. Expected an identifier starting with a letter followed by letters or digits."+
            "Invalid identifier '" + tokenValue + "' on line " + curHanTok.getLine());        
        }
    }
    
    private void declarations() {
        while (curHanTok.getType() == Token.TokenType.CONST || curHanTok.getType() == Token.TokenType.VAR) {
            if (curHanTok.getType() == Token.TokenType.CONST) {
                constDecl();
            } else if (curHanTok.getType() == Token.TokenType.VAR) {
                varDecl();
            }
        }
    }

    private void constDecl(){
        if (curHanTok.getType() == Token.TokenType.CONST){
            constList();
        } else if (isTokenInFollowSetOfConstDecl(curHanTok.getType())){
            // if the token is in the FOLLOW set of const-decl we can do nothing because it can be (lambda)
        }else {
            // if the token is not CONST and not in the FOLLOW set of constdecl throw a error
            errorFun("Error: unexpected token " + curHanTok.getValue() + " in constDecl.");
         }
    }

    private boolean isTokenInFollowSetOfConstDecl(Token.TokenType tokenType) {
        return tokenType == Token.TokenType.VAR;
    }

    private void constList() {
    	while (curHanTok.getType() == Token.TokenType.CONST){
    		matchHanToken(Token.TokenType.CONST); 
            dataType(); // data tupe should be (int, float, char)
            name(); 
            matchHanToken(Token.TokenType.EQUAL);
            value(); 
            matchHanToken(Token.TokenType.SEMICOLON);
            }
    }

    private void value(){
        if (curHanTok.getType() == Token.TokenType.INTEGER_VALUE){
            integerValue();
        } else if(curHanTok.getType() == Token.TokenType.FLOAT_VALUE){
        	floatValue();
        } else{
            errorFun("Error: Expected integer or float value.");
              }
    }
    
    private void floatValue(){
    	matchHanToken(Token.TokenType.FLOAT_VALUE);
    }
    
    private void integerValue(){
    	matchHanToken(Token.TokenType.INTEGER_VALUE);
    }

    private void nameList() {
        name();
        while (curHanTok.getType() == Token.TokenType.COMMA) {
            matchHanToken(Token.TokenType.COMMA);
            name();
        }
    }
    
    private void varDecl(){
        if (curHanTok.getType() == Token.TokenType.VAR) {
            matchHanToken(Token.TokenType.VAR);
            dataType(); 
            nameList();
            matchHanToken(Token.TokenType.SEMICOLON);
        } else {
            errorFun("Error: unexpected token " + curHanTok.getValue() + " in varDecl.");
        }
    }

    private void dataType() {
        if (curHanTok.getType() == Token.TokenType.INT){
            matchHanToken(Token.TokenType.INT);
        } else if (curHanTok.getType() == Token.TokenType.FLOAT){
            matchHanToken(Token.TokenType.FLOAT);
        } else if (curHanTok.getType() == Token.TokenType.CHAR){
            matchHanToken(Token.TokenType.CHAR);
          } else{
            errorFun("Error: expected data type but found " + curHanTok.getValue());
            }
    }
    
    private void functionHeading() {
        matchHanToken(Token.TokenType.FUNCTION);
        name();
        matchHanToken(Token.TokenType.SEMICOLON);
    }
    
    private void block() {
        matchHanToken(Token.TokenType.NEWB);
        stmtList();
        matchHanToken(Token.TokenType.ENDB);
    }
    
    private void functionDecl(){
        while (curHanTok.getType() == Token.TokenType.FUNCTION) {
            functionHeading(); // parse function header
            declarations();    // parse declarations within the function
            block();           // parse function block
            matchHanToken(Token.TokenType.SEMICOLON);
        }
    }

    private boolean isStatementStart(Token.TokenType tokenType) {
        return tokenType == Token.TokenType.IF ||
                tokenType == Token.TokenType.WHILE ||
                tokenType == Token.TokenType.REPEAT ||
                tokenType == Token.TokenType.CALL ||
                tokenType == Token.TokenType.CIN ||
                tokenType == Token.TokenType.COUT ||
                tokenType == Token.TokenType.IDENTIFIER ||
                tokenType == Token.TokenType.NEWB||
                tokenType == Token.TokenType.EXIT;
    }
    
    private void stmtList() {
        while (curHanTok.getType() != Token.TokenType.ENDB && curHanTok.getType() != Token.TokenType.UNTIL){            
            if (curHanTok.getType() == Token.TokenType.EXIT) {
                exitStmt(); 
                return; // exit the final statement exit the stmtList
            }
            // parse current statement
            statement();
            // if a semicolon follows
            if (curHanTok.getType() == Token.TokenType.SEMICOLON){
                matchHanToken(Token.TokenType.SEMICOLON);
            } else if(curHanTok.getType() != Token.TokenType.ENDB && curHanTok.getType() != Token.TokenType.UNTIL){
                // if the next token is not ENDB or UNTIL ensure it's a valid statement start
                if(!isStatementStart(curHanTok.getType())) {
                    errorFun("Expected SEMICOLON or ENDB but found " + curHanTok.getType());
                     }
              }
         }
   }    
    
    private void assStmt(){
        name();
        matchHanToken(Token.TokenType.ASSIGN);
        exp();
    }
    
    private void statement(){
        // check if lambda
        if (isTokenInFollowSetOfStatement(curHanTok.getType())){
            return;
        }
        switch (curHanTok.getType()){
        	case EXIT:
        		exitStmt();
            	break;
            case IDENTIFIER:
                assStmt();
                break;
            case NEWB:
                block();
                break;
            case IF:
                ifStmt();
                break;
            case CIN:
                cinStmt(); 
                break;
            case COUT:
                coutStmt();
                break;
            case WHILE:
                whileStmt();
                break;
            case CALL:
                callStmt();
                break;
            case REPEAT:
                repeatStmt();
                break;
            default:
                errorFun("Error: unexpected statement " + curHanTok.getValue() + " in statement.");
                break;
           }
       }

    private boolean isTokenInFollowSetOfStatement(Token.TokenType tokenType) {
        return tokenType == Token.TokenType.SEMICOLON || tokenType == Token.TokenType.ENDB ||
                tokenType == Token.TokenType.ELSE || tokenType == Token.TokenType.UNTIL;
    }
    
    private void cinStmt(){
        matchHanToken(Token.TokenType.CIN); 
        do{
            matchHanToken(Token.TokenType.EXTRACTION);
            name(); 
        } while(curHanTok.getType() == Token.TokenType.EXTRACTION);
          matchHanToken(Token.TokenType.SEMICOLON); 
      }
    
    private void coutStmt(){
        matchHanToken(Token.TokenType.COUT);
        do{
            matchHanToken(Token.TokenType.INSERTION);
            nameValue();
        } while(curHanTok.getType() == Token.TokenType.INSERTION);       
        if(curHanTok.getType() == Token.TokenType.SEMICOLON){
            matchHanToken(Token.TokenType.SEMICOLON);
        } else if (curHanTok.getType() != Token.TokenType.ELSE && curHanTok.getType() != Token.TokenType.ENDB 
                   && !isStatementStart(curHanTok.getType())){
            errorFun("Expected SEMICOLON or valid continuation but found " + curHanTok.getType());
            }
      }
    
    private void term() {
        factor();
        while (curHanTok.getType() == Token.TokenType.MULTIPLY || curHanTok.getType() == Token.TokenType.DIVIDE || curHanTok.getType() == Token.TokenType.MOD 
        		|| curHanTok.getType() == Token.TokenType.DIV) {
            mulOper();
            factor();
        }
    }
    
    private void exp(){
        term();
        while (curHanTok.getType() == Token.TokenType.PLUS || curHanTok.getType() == Token.TokenType.MINUS) {
            addOper();
            term();
        }
    }

    private void factor(){
        if (curHanTok.getType() == Token.TokenType.OPEN_PAREN){
            matchHanToken(Token.TokenType.OPEN_PAREN);
            exp();
            matchHanToken(Token.TokenType.CLOSE_PAREN);
        } else if(curHanTok.getType() == Token.TokenType.IDENTIFIER ||
                curHanTok.getType() == Token.TokenType.INTEGER_VALUE ||
                curHanTok.getType() == Token.TokenType.FLOAT_VALUE) {
            if (curHanTok.getType() == Token.TokenType.IDENTIFIER){
                name();
            } else{
                value();
            }
        } else{
            errorFun("Error: Expected factor but found " + curHanTok.getValue());
          }
      }

    private void mulOper(){
        switch (curHanTok.getType()){            
            case DIVIDE:
                matchHanToken(Token.TokenType.DIVIDE);
                break;
            case DIV:
                matchHanToken(Token.TokenType.DIV);
                break;
            case MOD:
                matchHanToken(Token.TokenType.MOD);
                break;            
            case MULTIPLY:
                matchHanToken(Token.TokenType.MULTIPLY);
                break;
            default:
                errorFun("Error: Expected multiplication operator but found " + curHanTok.getValue());
                break;
              }
         }
    
    private void addOper(){
        if (curHanTok.getType() == Token.TokenType.PLUS){
            matchHanToken(Token.TokenType.PLUS);
        } else if (curHanTok.getType() == Token.TokenType.MINUS){
            matchHanToken(Token.TokenType.MINUS);
        } else{
            errorFun("Error: Expected addition operator but found " + curHanTok.getValue());
          }
     }
    
    private void ifStmt(){
    	    matchHanToken(Token.TokenType.IF);
    	    matchHanToken(Token.TokenType.OPEN_PAREN);
    	    condition();
    	    matchHanToken(Token.TokenType.CLOSE_PAREN);
    	    statement();
    	    if (curHanTok.getType() == Token.TokenType.ELSE){
    	        elseHan();
    	    }												
    }

    private void elseHan() {
        if (curHanTok.getType() == Token.TokenType.ELSE) {
            matchHanToken(Token.TokenType.ELSE);
            stmtList();
        } else if (isTokenInFollowSetOfElsePart(curHanTok.getType())) {

        } else {
            errorFun("Error: unexpected token " + curHanTok.getValue() + " in elsePart.");
        }
    }

    private boolean isTokenInFollowSetOfElsePart(Token.TokenType tokenType) {
        return tokenType == Token.TokenType.ENDB 
        		|| tokenType == Token.TokenType.SEMICOLON;
    }

    private void whileStmt() {    	
        matchHanToken(Token.TokenType.WHILE);
        matchHanToken(Token.TokenType.OPEN_PAREN);
        condition();
        matchHanToken(Token.TokenType.CLOSE_PAREN);
        block();
            }

    private void condition() {
        // parse left operand
        nameValue();
        // parse relational operator
        if (isRelationalOperator(curHanTok.getType())){
            relationalOper();
        } else{
            errorFun("Expected a relational operator but found " + curHanTok.getValue());
           }
        // parse right operand
        nameValue();
     }
    
    private void relationalOper() {
    	if (isRelationalOperator(curHanTok.getType())) {
            matchHanToken(curHanTok.getType()); // match the relational operator
        } else {
            errorFun("Expected a relational operator but found " + curHanTok.getValue());
        }
    }
    
    private boolean isRelationalOperator(Token.TokenType tokenType) {
        return tokenType == Token.TokenType.GREATER_THAN || tokenType == Token.TokenType.GREATER_THAN_EQUAL || tokenType == Token.TokenType.LESS_THAN 
        		|| tokenType == Token.TokenType.LESS_THAN_EQUAL ||
               tokenType == Token.TokenType.EQUAL || tokenType == Token.TokenType.NOT_EQUAL;
    }
    
    private void nameValue() {
    	if (curHanTok.getType() == Token.TokenType.OPEN_PAREN){
            matchHanToken(Token.TokenType.OPEN_PAREN); 
            condition(); 
            matchHanToken(Token.TokenType.CLOSE_PAREN);
    	}else if (curHanTok.getType() == Token.TokenType.IDENTIFIER){
            name();                      
        } else if(curHanTok.getType() == Token.TokenType.INTEGER_VALUE ||
                curHanTok.getType() == Token.TokenType.FLOAT_VALUE){
            value();
        } else{
            errorFun("Error: Expected a name or value but found " + curHanTok.getValue());
           }
  }

    private void callStmt(){
        matchHanToken(Token.TokenType.CALL);
        name();
    }

    private void repeatStmt() {
        matchHanToken(Token.TokenType.REPEAT);
        stmtList();
        if(curHanTok.getType() == Token.TokenType.UNTIL){
            matchHanToken(Token.TokenType.UNTIL);
        } else{
            errorFun("Expected UNTIL but found " + curHanTok.getValue());
             }
        condition();
  }

    private void exitStmt() {
        matchHanToken(Token.TokenType.EXIT);
    }
}