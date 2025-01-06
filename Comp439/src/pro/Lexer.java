package pro;

class Lexer {
    private String input; // string to be tokenized
    private int hannaL;
    private int pos; // current position at the string
    private char currentChar; // current character at the pos

    Lexer(String input) {
        this.input = input;
        hannaL = 1; 
        pos = 0;
        currentChar = input.length() > 0 ? input.charAt(pos) : '\0';
    }

    // moves to the next character in the input string
    private void advance() {
    	if (currentChar == '\n') {
    		hannaL++;
        }
        pos++;
        if (pos >= input.length()) {
            currentChar = '\0'; // Indicates end of input
        } else {
            currentChar = input.charAt(pos);
        }
    }

    // skip any white space character
    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance();
        }
    }
    
    public int getLine() {
        return hannaL;
    }

    // recognizes and returns a token for a numeric literal (integer or float)
    private Token number() {
        StringBuilder numberStr = new StringBuilder();
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            numberStr.append(currentChar);
            advance();
        }

        if (currentChar == '.') {
            numberStr.append(currentChar);
            advance();
            while (Character.isDigit(currentChar)) {
                numberStr.append(currentChar);
                advance();
            }
            return new Token(Token.TokenType.FLOAT_VALUE, numberStr.toString(), hannaL);
        }

        return new Token(Token.TokenType.INTEGER_VALUE, numberStr.toString(), hannaL);
    }

    // recognizes identifiers and keywords, it matches an identifier and then checks
    // if its a known keyword
    private Token identifier() {
        StringBuilder idStr = new StringBuilder();
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            idStr.append(currentChar);
            advance();
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

    // returns the next character without advancing the current position(used for
    // multi-character tokens)
    private char peek() {
        int nextPos = pos + 1;
        if (nextPos >= input.length()) {
            return '\0';
        }
        return input.charAt(nextPos);
    }

    public Token getNextToken() {
        // System.out.println("getNextToken - Current position: " + pos + ", Current
        // character: " + currentChar); // debugging
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }

            if (Character.isDigit(currentChar)) {
                return number();
            }

            if (Character.isLetter(currentChar)) {
                return identifier();
            }

            // Handling multi-character tokens
            if (currentChar == ':' && peek() == '=') {
                advance();
                advance();
                return new Token(Token.TokenType.ASSIGN, ":=", hannaL);
            }
            if (currentChar == '=' && peek() == '!') {
                advance();
                advance();
                return new Token(Token.TokenType.NOT_EQUAL, "=!", hannaL);
            }
            if (currentChar == '=' && peek() == '>') {
                advance();
                advance();
                return new Token(Token.TokenType.GREATER_THAN_EQUAL, "=>", hannaL);
            }
            if (currentChar == '=' && peek() == '<') {
                advance();
                advance();
                return new Token(Token.TokenType.LESS_THAN_EQUAL, "=<", hannaL);
            }
            if (currentChar == '>' && peek() == '>') {
                advance(); advance();
                return new Token(Token.TokenType.EXTRACTION, ">>", hannaL);
            } 
            if (currentChar == '<' && peek() == '<') {
                advance(); advance();
                return new Token(Token.TokenType.INSERTION, "<<", hannaL);
            }

            switch (currentChar) {
            	case '#':
            		advance();
            		return new Token(Token.TokenType.HASH, "#", hannaL);
                case ';':
                    advance();
                    return new Token(Token.TokenType.SEMICOLON, ";", hannaL);
                case ':':
                    advance();
                    return new Token(Token.TokenType.COLON, ":", hannaL);
                case '.':
                    advance();
                    return new Token(Token.TokenType.DOT, ".", hannaL);
                case ',':
                    advance();
                    return new Token(Token.TokenType.COMMA, ",", hannaL);
                case '(':
                    advance();
                    return new Token(Token.TokenType.OPEN_PAREN, "(", hannaL);
                case ')':
                    advance();
                    return new Token(Token.TokenType.CLOSE_PAREN, ")", hannaL);
                case '=':
                    advance();
                    return new Token(Token.TokenType.EQUAL, "=", hannaL);
                case '+':
                    advance();
                    return new Token(Token.TokenType.PLUS, "+", hannaL);
                case '-':
                    advance();
                    return new Token(Token.TokenType.MINUS, "-", hannaL);
                case '*':
                    advance();
                    return new Token(Token.TokenType.MULTIPLY, "*", hannaL);
                case '/':
                    advance();
                    return new Token(Token.TokenType.DIVIDE, "/", hannaL);
                case '>':
                    advance();
                    return new Token(Token.TokenType.GREATER_THAN, ">", hannaL);
                case '<':
                    advance();
                    return new Token(Token.TokenType.LESS_THAN, "<", hannaL);
                default:
//                	throw new RuntimeException("Lexical Error: Unexpected character '" + currentChar + 
//                            "' at line " + hannaL + ", position " + pos);
                	System.out.println("Lexical Error: Unexpected character '" + currentChar + "' at line " + hannaL);
                	System.exit(0);
            }
        }

        return new Token(Token.TokenType.EXIT, "", hannaL);

    }
}