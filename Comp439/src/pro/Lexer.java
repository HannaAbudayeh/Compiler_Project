package pro;

class Lexer{
    private String hh; // string to token
    private int hannaL; // count lines 
    private int loc; // current location in the string
    private char CurLocCH; // char at the location

    Lexer(String input){
        this.hh = input;
        hannaL = 1; 
        loc = 0;
        if (input.length() > 0){
            CurLocCH = input.charAt(loc);
        } else {
            CurLocCH = '\0';
        }
        }
    
    // method to skip white space 
    private void skipHspace(){
        while (CurLocCH != '\0' && Character.isWhitespace(CurLocCH)) {
        	hannaIncrement();
        }
    }
    // in input string move to next char
    private void hannaIncrement(){
    	if (CurLocCH == '\n'){
    	   hannaL++;
        }
    	loc++;
        if (loc >= hh.length()){
           CurLocCH = '\0'; // show end of input
        } else {
          CurLocCH = hh.charAt(loc);
        }
    	}
    
    public int getLine(){
        return hannaL;
    }
 
    // returns the next character without advancing the current position(used for
    // multi-character tokens)
    private char dayehForward() {
        int nextPos = loc + 1;
        if (nextPos >= hh.length()) {
            return '\0';
        }
        return hh.charAt(nextPos);
    }
    
    // recognizes and returns a token for a numeric literal (integer or float)
    private Token number(){
        StringBuilder numberStr = new StringBuilder();
        while (CurLocCH != '\0' && Character.isDigit(CurLocCH)){
            numberStr.append(CurLocCH);
            hannaIncrement();
        }

        if (CurLocCH == '.'){
            numberStr.append(CurLocCH);
            hannaIncrement();
            while (Character.isDigit(CurLocCH)){
                numberStr.append(CurLocCH);
                hannaIncrement();
            }
            return new Token(Token.TokenType.FLOAT_VALUE, numberStr.toString(), hannaL);
        }

        return new Token(Token.TokenType.INTEGER_VALUE, numberStr.toString(), hannaL);
    }

    // recognizes identifiers and keywords, it matches an identifier and then checks
    // if its a known keyword
    private Token identifier(){
        StringBuilder idStr = new StringBuilder();
        while (CurLocCH != '\0' && (Character.isLetterOrDigit(CurLocCH) || CurLocCH == '_')){
            idStr.append(CurLocCH);
            hannaIncrement();
        }
        String id = idStr.toString().toUpperCase(); // Convert to uppercase
        // Check if it's a keyword ot identifier
        try {
//            System.out.println(id);
            return new Token(Token.TokenType.valueOf(id), id, hannaL);
        } catch (IllegalArgumentException e) {
            return new Token(Token.TokenType.IDENTIFIER, idStr.toString(), hannaL);
        }
    }



    public Token getNextToken() {

        while (CurLocCH != '\0') {
            if (Character.isDigit(CurLocCH)) {
                return number();
            }
            
            if (Character.isWhitespace(CurLocCH)) {
                skipHspace();
                continue;
            }
            
            if (Character.isLetter(CurLocCH)) {
                return identifier();
            }

            // Handling multi-character tokens
            if (CurLocCH == '<' && dayehForward() == '<') {
            	hannaIncrement(); 
            	hannaIncrement();
                return new Token(Token.TokenType.INSERTION, "<<", hannaL);
            }
            if (CurLocCH == '=' && dayehForward() == '<') {
            	hannaIncrement();
            	hannaIncrement();
                return new Token(Token.TokenType.LESS_THAN_EQUAL, "=<", hannaL);
            }
            if (CurLocCH == ':' && dayehForward() == '=') {
            	hannaIncrement();
            	hannaIncrement();
                return new Token(Token.TokenType.ASSIGN, ":=", hannaL);
            }
            if (CurLocCH == '>' && dayehForward() == '>') {
            	hannaIncrement(); 
            	hannaIncrement();
                return new Token(Token.TokenType.EXTRACTION, ">>", hannaL);
            } 
            if (CurLocCH == '=' && dayehForward() == '!') {
            	hannaIncrement();
            	hannaIncrement();
                return new Token(Token.TokenType.NOT_EQUAL, "=!", hannaL);
            }
            if (CurLocCH == '=' && dayehForward() == '>') {
            	hannaIncrement();
            	hannaIncrement();
                return new Token(Token.TokenType.GREATER_THAN_EQUAL, "=>", hannaL);
            }
            
            switch (CurLocCH) {
            	case '#':
            		hannaIncrement();
            		return new Token(Token.TokenType.HASH, "#", hannaL);
                case '/':
                	hannaIncrement();
                    return new Token(Token.TokenType.DIVIDE, "/", hannaL);
                case ';':
                	hannaIncrement();
                    return new Token(Token.TokenType.SEMICOLON, ";", hannaL);
                case '*':
                	hannaIncrement();
                    return new Token(Token.TokenType.MULTIPLY, "*", hannaL);
                case ':':
                	hannaIncrement();
                    return new Token(Token.TokenType.COLON, ":", hannaL);
                case '>':
                	hannaIncrement();
                    return new Token(Token.TokenType.GREATER_THAN, ">", hannaL);
                case '.':
                	hannaIncrement();
                    return new Token(Token.TokenType.DOT, ".", hannaL);
                case '-':
                	hannaIncrement();
                    return new Token(Token.TokenType.MINUS, "-", hannaL);
                case ',':
                	hannaIncrement();
                    return new Token(Token.TokenType.COMMA, ",", hannaL);
                case '+':
                	hannaIncrement();
                    return new Token(Token.TokenType.PLUS, "+", hannaL);
                case '(':
                	hannaIncrement();
                    return new Token(Token.TokenType.OPEN_PAREN, "(", hannaL);
                case '<':
                	hannaIncrement();
                    return new Token(Token.TokenType.LESS_THAN, "<", hannaL);
                case ')':
                	hannaIncrement();
                    return new Token(Token.TokenType.CLOSE_PAREN, ")", hannaL);
                case '=':
                	hannaIncrement();
                    return new Token(Token.TokenType.EQUAL, "=", hannaL);
                default:

                	System.out.println("Lexical Error: Unexpected character '" + CurLocCH + "' at line " + hannaL);
                	System.exit(0);
                }
       }
        return new Token(Token.TokenType.EXIT, "", hannaL);
      }
}