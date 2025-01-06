package pro;

class Parser {
    private Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
        // System.out.println("First token in Parser: " + currentToken); // debugging
    }

    private void eat(Token.TokenType tokenType) {
//        System.out.println("Eating token: " + currentToken); // Debugging

        if (currentToken.getType() == tokenType) {
            currentToken = lexer.getNextToken();
//            System.out.println("Next token: " + currentToken); // Debugging

        } else {
            error("Syntax error: Expected " + tokenType + " but found " + currentToken.getType());
        }
    }

    private void moduleDecl() {
        heading();
        declarations();
        functionDecl();  
        block();
        name();
        
//        eat(Token.TokenType.DOT);
    }

    private void heading() {
        
        while (currentToken.getType() == Token.TokenType.HASH) {
            eat(Token.TokenType.HASH);
            eat(Token.TokenType.INCLUDE);
            eat(Token.TokenType.LESS_THAN);
            name();
            eat(Token.TokenType.GREATER_THAN);
            eat(Token.TokenType.SEMICOLON);
        }
    }

    private void name() {
    	
    	if (currentToken.getType() == Token.TokenType.EXIT) {
            exitStmt(); // Consume EXIT if present.
            return;
        }
    	if (currentToken.getType() != Token.TokenType.IDENTIFIER) {
            error("Expected an identifier but found " + currentToken.getValue());
        }
    	
        String tokenValue = currentToken.getValue();
        // Check if the token value matches the pattern for a name according to the
        // grammar rule
        if (tokenValue.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            eat(Token.TokenType.IDENTIFIER); // This consumes the identifier token.
        } else {
            // If the token value does not match the pattern, report an error.
            error("Name does not match the required pattern. Expected an identifier starting with a letter followed by letters or digits."+
            "Invalid identifier '" + tokenValue + "' on line " + currentToken.getLine());        
        }
    }
    
    private void declarations() {
//    	System.out.println("Parsing declarations: " + currentToken);
    	
//        constDecl();
//        while (currentToken.getType() == Token.TokenType.VAR) {
//            varDecl();
//        }
        
        
        while (currentToken.getType() == Token.TokenType.CONST || currentToken.getType() == Token.TokenType.VAR) {
            if (currentToken.getType() == Token.TokenType.CONST) {
                constDecl();
            } else if (currentToken.getType() == Token.TokenType.VAR) {
                varDecl();
            }
        }
//        System.out.println("Parsed declarations successfully: " + currentToken);

    }

    private void constDecl() {

        if (currentToken.getType() == Token.TokenType.CONST) {
//            eat(Token.TokenType.CONST);
            constList();
        } else if (isTokenInFollowSetOfConstDecl(currentToken.getType())) {
            // if the current token is in the FOLLOW set of const-decl its okay do nothing,
            // because it can be empty(lambda)
        } else {
            // If the current token is not CONST and not in the FOLLOW set of const-decl,
            // report an error
            error("Error: unexpected token " + currentToken.getValue() + " in constDecl.");
        }
    }

    private boolean isTokenInFollowSetOfConstDecl(Token.TokenType tokenType) {
        // FOLLOW(const-decl) includes the first tokens of var-decl and procedure-decl
        return tokenType == Token.TokenType.VAR;
    }

    private void constList() {
    	while (currentToken.getType() == Token.TokenType.CONST) {
            eat(Token.TokenType.CONST); // Consume 'CONST'
            dataType(); // Consume the data type (int, float, char)
            name(); // Consume the constant name (identifier)
            eat(Token.TokenType.EQUAL); // Consume '='
            value(); // Consume the value
            eat(Token.TokenType.SEMICOLON); // Consume ';'
        }
    }

    private void value() {
        if (currentToken.getType() == Token.TokenType.INTEGER_VALUE) {
            integerValue(); // If the token is an integer value parse as integer value.
        } else if (currentToken.getType() == Token.TokenType.FLOAT_VALUE) {
            realValue(); // If the token is a real value parse as real value.
        } else {
            error("Error: Expected integer or real value.");
        }
    }

    private void integerValue() {
        eat(Token.TokenType.INTEGER_VALUE);
    }

    private void realValue() {

        eat(Token.TokenType.FLOAT_VALUE);
    }

    private void varDecl() {
//        System.out.println("Entering varDecl: " + currentToken); // Debugging

        if (currentToken.getType() == Token.TokenType.VAR) {
            eat(Token.TokenType.VAR);
//            varList();
            dataType(); // Consume the data type (int, float, char)
            nameList(); // Parse the name list (comma-separated variable names)
            eat(Token.TokenType.SEMICOLON); // Consume ';'
        } else {
            error("Error: unexpected token " + currentToken.getValue() + " in varDecl.");
        }
    }

    private void varList() {
        while (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            varItem();
            if (currentToken.getType() == Token.TokenType.SEMICOLON) {
                eat(Token.TokenType.SEMICOLON);
            } else {
                break; // Exit if no more semicolons follow var-items
            }
        }
    }

    private void varItem() {
        nameList();
        eat(Token.TokenType.COLON);
        dataType();
    }

    private void nameList() {
//        System.out.println("Parsing nameList: " + currentToken);

        name();
        while (currentToken.getType() == Token.TokenType.COMMA) {
            eat(Token.TokenType.COMMA);
            name();
        }
//        System.out.println("Parsed nameList successfully.");

    }

    private void dataType() {
        if (currentToken.getType() == Token.TokenType.INT) {
            eat(Token.TokenType.INT);
        } else if (currentToken.getType() == Token.TokenType.FLOAT) {
            eat(Token.TokenType.FLOAT);
        } else if (currentToken.getType() == Token.TokenType.CHAR) {
            eat(Token.TokenType.CHAR);
        } else {
            error("Error: expected data type but found " + currentToken.getValue());
        }
    }

    private void functionDecl() {
        while (currentToken.getType() == Token.TokenType.FUNCTION) {
//            System.out.println("Parsing functionDecl: " + currentToken); // Debugging
            functionHeading(); // Parse function header
            declarations();    // Parse local declarations within the function
            block();           // Parse the function block
            eat(Token.TokenType.SEMICOLON); // Consume the terminating ';'
//            System.out.println("Parsed functionDecl successfully.");

        }
    }
    
    private void functionHeading() {
//        System.out.println("Parsing functionHeading: " + currentToken); // Debugging
        eat(Token.TokenType.FUNCTION);
        // Parse the function name (an identifier)
        name();
        eat(Token.TokenType.SEMICOLON);

//        System.out.println("Parsed functionHeading successfully.");
    }
    
    private void block() {
        eat(Token.TokenType.NEWB);
        stmtList();
        eat(Token.TokenType.ENDB);
    }

    private void stmtList() {

        while (currentToken.getType() != Token.TokenType.ENDB && currentToken.getType() != Token.TokenType.UNTIL) {
//            System.out.println("Current token in stmtList: " + currentToken);
            
            if (currentToken.getType() == Token.TokenType.EXIT) {
//                System.out.println("Found EXIT statement.");
                exitStmt(); // Consume the EXIT token
                return; // EXIT is the final statement, so exit the stmtList
            }

            
            // Parse the current statement
            statement();

            // If a semicolon follows, consume it
            if (currentToken.getType() == Token.TokenType.SEMICOLON) {
                eat(Token.TokenType.SEMICOLON);
            } else if (currentToken.getType() != Token.TokenType.ENDB && currentToken.getType() != Token.TokenType.UNTIL) {
                // If the next token is not ENDB or UNTIL, ensure it's a valid statement start
                if (!isStatementStart(currentToken.getType())) {
                	System.out.println("_|__|_|_|_|_|_|__|_");
                    error("Expected SEMICOLON or ENDB but found " + currentToken.getType());
                }
            }
        }
//        System.out.println("Exiting stmtList.");
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
    
    private void statement() {
        // Check for lambda (empty statement)
        if (isTokenInFollowSetOfStatement(currentToken.getType())) {
            // Do nothing for lambda
            return;
        }

        switch (currentToken.getType()) {
            case IDENTIFIER: // assuming assignment starts with an identifier
                assStmt();
                break;
            case IF:
                ifStmt();
                break;
            case WHILE:
                whileStmt();
                break;
            case REPEAT:
                repeatStmt();
                break;
            case NEWB:
                block();
                break;
            case CIN:
                cinStmt(); 
                break;
            case COUT:
                coutStmt();
                break;
            case EXIT:
                exitStmt();
                break;
            case CALL:
                callStmt();
                break;
            default:
                error("Error: unexpected statement " + currentToken.getValue() + " in statement.");
                break;
        }
    }

    private boolean isTokenInFollowSetOfStatement(Token.TokenType tokenType) {
        return tokenType == Token.TokenType.SEMICOLON ||
                tokenType == Token.TokenType.ENDB ||
                tokenType == Token.TokenType.ELSE ||
                tokenType == Token.TokenType.ELSEIF ||
                tokenType == Token.TokenType.UNTIL;
    }
    
    private void cinStmt() {
//        System.out.println("Parsing cinStmt: " + currentToken);
        eat(Token.TokenType.CIN); // Consume 'cin'

        do {
            eat(Token.TokenType.EXTRACTION); // Consume '>>'
            name(); // Parse variable name
        } while (currentToken.getType() == Token.TokenType.EXTRACTION);

        eat(Token.TokenType.SEMICOLON); // Consume ';'
//        System.out.println("Parsed cinStmt successfully.");
    }
    
    private void coutStmt() {
//        System.out.println("Parsing coutStmt: " + currentToken);
        eat(Token.TokenType.COUT); // Consume 'cout'

        do {
            eat(Token.TokenType.INSERTION); // Consume '<<'
            nameValue(); // Parse variable or literal
        } while (currentToken.getType() == Token.TokenType.INSERTION);

        eat(Token.TokenType.SEMICOLON); // Consume ';'
//        System.out.println("Parsed coutStmt successfully.");
    }
    
    private void assStmt() {
        name();
        eat(Token.TokenType.ASSIGN);
        exp();
    }

    private void exp() {
        term();
        while (currentToken.getType() == Token.TokenType.PLUS || currentToken.getType() == Token.TokenType.MINUS) {
            addOper();
            term();
        }
    }

    private void term() {
        factor();
        while (currentToken.getType() == Token.TokenType.MULTIPLY ||
                currentToken.getType() == Token.TokenType.DIVIDE ||
                currentToken.getType() == Token.TokenType.MOD ||
                currentToken.getType() == Token.TokenType.DIV) {
            mulOper();
            factor();
        }
    }

    private void factor() {
        if (currentToken.getType() == Token.TokenType.OPEN_PAREN) {
            eat(Token.TokenType.OPEN_PAREN);
            exp();
            eat(Token.TokenType.CLOSE_PAREN);
        } else if (currentToken.getType() == Token.TokenType.IDENTIFIER ||
                currentToken.getType() == Token.TokenType.INTEGER_VALUE ||
                currentToken.getType() == Token.TokenType.FLOAT_VALUE) {

            if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
                name();
            } else {
                value();
            }
        } else {
            error("Error: Expected factor but found " + currentToken.getValue());
        }
    }

    private void addOper() {
        if (currentToken.getType() == Token.TokenType.PLUS) {
            eat(Token.TokenType.PLUS);
        } else if (currentToken.getType() == Token.TokenType.MINUS) {
            eat(Token.TokenType.MINUS);
        } else {
            error("Error: Expected addition operator but found " + currentToken.getValue());
        }
    }

    private void mulOper() {
        switch (currentToken.getType()) {
            case MULTIPLY:
                eat(Token.TokenType.MULTIPLY);
                break;
            case DIVIDE:
                eat(Token.TokenType.DIVIDE);
                break;
            case MOD:
                eat(Token.TokenType.MOD);
                break;
            case DIV:
                eat(Token.TokenType.DIV);
                break;
            default:
                error("Error: Expected multiplication operator but found " + currentToken.getValue());
                break;
        }
    }

    private void writeList() {
        writeItem();
        while (currentToken.getType() == Token.TokenType.COMMA) {
            eat(Token.TokenType.COMMA);
            writeItem();
        }
    }

    private void writeItem() {
        if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            name();
        } else if (currentToken.getType() == Token.TokenType.INTEGER_VALUE ||
                currentToken.getType() == Token.TokenType.FLOAT_VALUE) {
            value();
        } else {
            error("Error in writeItem: unexpected token " + currentToken.getValue());
        }
    }

    private void ifStmt() {
//    	 System.out.println("Parsing ifStmt: " + currentToken); // Debugging

    	    eat(Token.TokenType.IF);
    	    eat(Token.TokenType.OPEN_PAREN);
    	    condition();
    	    eat(Token.TokenType.CLOSE_PAREN);
    	    statement();
    	    if (currentToken.getType() == Token.TokenType.ELSE) {
    	        eat(Token.TokenType.ELSE);
    	        statement();
    	    }
//    	    System.out.println("Parsed ifStmt successfully.");
    }

    private void elseifPart() {
        while (currentToken.getType() == Token.TokenType.ELSEIF) {
            eat(Token.TokenType.ELSEIF);
            condition();
            eat(Token.TokenType.THEN);
            stmtList();
        }
    }

    private void elsePart() {
        if (currentToken.getType() == Token.TokenType.ELSE) {
            eat(Token.TokenType.ELSE);
            stmtList();
        } else if (isTokenInFollowSetOfElsePart(currentToken.getType())) {

        } else {
            error("Error: unexpected token " + currentToken.getValue() + " in elsePart.");
        }
    }

    private boolean isTokenInFollowSetOfElsePart(Token.TokenType tokenType) {
        return tokenType == Token.TokenType.ENDB || tokenType == Token.TokenType.SEMICOLON;
    }

    private void whileStmt() {
//        System.out.println("Parsing whileStmt: " + currentToken);
    	
        eat(Token.TokenType.WHILE);
        eat(Token.TokenType.OPEN_PAREN);
        condition();
        eat(Token.TokenType.CLOSE_PAREN);
        block();
        
//        System.out.println("Parsed whileStmt successfully.");
    }

    private void condition() {
//        System.out.println("Parsing condition: " + currentToken); // Debugging

        // Parse left operand
        nameValue();
//        System.out.println("Parsed left operand: " + currentToken);
        // Parse relational operator
        if (isRelationalOperator(currentToken.getType())) {
            relationalOper();
//            System.out.println("Parsed relational operator: " + currentToken);

        } else {
            error("Expected a relational operator but found " + currentToken.getValue());
        }
        // Parse right operand
        nameValue();
//        System.out.println("Parsed right operand: " + currentToken);
//        System.out.println("Parsed condition successfully.");
    }

    private boolean isRelationalOperator(Token.TokenType tokenType) {
        return tokenType == Token.TokenType.GREATER_THAN ||
               tokenType == Token.TokenType.GREATER_THAN_EQUAL ||
               tokenType == Token.TokenType.LESS_THAN ||
               tokenType == Token.TokenType.LESS_THAN_EQUAL ||
               tokenType == Token.TokenType.EQUAL ||
               tokenType == Token.TokenType.NOT_EQUAL;
    }
    
    
    private void nameValue() {
    	if (currentToken.getType() == Token.TokenType.OPEN_PAREN) {
            eat(Token.TokenType.OPEN_PAREN); // Consume '('
            condition(); // Parse the expression
            eat(Token.TokenType.CLOSE_PAREN); // Consume ')'
    	}else if (currentToken.getType() == Token.TokenType.IDENTIFIER) {
            name();
                       
        } else if (currentToken.getType() == Token.TokenType.INTEGER_VALUE ||
                currentToken.getType() == Token.TokenType.FLOAT_VALUE) {
            value();
        } else {
            error("Error: Expected a name or value but found " + currentToken.getValue());
        }
    }

    private void relationalOper() {
    	if (isRelationalOperator(currentToken.getType())) {
            eat(currentToken.getType()); // Consume the relational operator
        } else {
            error("Expected a relational operator but found " + currentToken.getValue());
        }
    	
    	
//        switch (currentToken.getType()) {
//            case EQUAL:
//                eat(Token.TokenType.EQUAL);
//                break;
//            case NOT_EQUAL:
//                eat(Token.TokenType.NOT_EQUAL);
//                break;
//            case LESS_THAN:
//                eat(Token.TokenType.LESS_THAN);
//                break;
//            case LESS_THAN_EQUAL:
//                eat(Token.TokenType.LESS_THAN_EQUAL);
//                break;
//            case GREATER_THAN:
//                eat(Token.TokenType.GREATER_THAN);
//                break;
//            case GREATER_THAN_EQUAL:
//                eat(Token.TokenType.GREATER_THAN_EQUAL);
//                break;
//            default:
//                error("Error: Expected a relational operator but found " + currentToken.getValue());
//                break;
//        }
    }

    private void callStmt() {
        eat(Token.TokenType.CALL);
        name();
    }

    private void repeatStmt() {
//        System.out.println("Parsing repeatStmt: " + currentToken); // Debugging
        // Consume 'REPEAT'
        eat(Token.TokenType.REPEAT);
        // Parse the statement list
        stmtList();

        // Consume 'UNTIL'
        if (currentToken.getType() == Token.TokenType.UNTIL) {
            eat(Token.TokenType.UNTIL);
        } else {
            error("Expected UNTIL but found " + currentToken.getValue());
        }
        // Parse the condition
        condition();

//        System.out.println("Parsed repeatStmt successfully.");
    }

    private void exitStmt() {
        eat(Token.TokenType.EXIT);
    }

//    private void error(String message) {
//        throw new RuntimeException("Error: " + message);
//    }
    private void error(String message) {
        throw new RuntimeException("Error on line " + currentToken.getLine() + ": " + message + ". Token: " + currentToken);
        
//        System.out.println("Error on line " + currentToken.getLine() + ": " + message + ". Token: " + currentToken);
//        System.exit(0);
    }
    public void parse() {
        // begin parsing
        moduleDecl();

        // to check if the currentToken is EOF to ensure the whole input has been
        // correctly parsed
        if (currentToken.getType() != Token.TokenType.EXIT) {
//            throw new RuntimeException("Unexpected tokens at the end of input.");
            System.out.println("Unexpected tokens at the end of input.");
            System.exit(0);
        }
    }
}