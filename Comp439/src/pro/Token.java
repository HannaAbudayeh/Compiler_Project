package pro;

class Token {
    private final TokenType type;
    private final int line; 
    private final String value;

    public Token(TokenType type, String value,int line) {
        this.type = type;
        this.value = value;
    	this.line = line;
    }

    public int getLine() {
        return line;
    }
    
    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + type + ", value='" + value + '\'' + ", line=" + line + '}';
    }
    enum TokenType {
    	// Parentheses and Delimiters
        COLON, DOT, SEMICOLON, CLOSE_PAREN, COMMA, OPEN_PAREN,
        // Keywords
        UNTIL, INCLUDE, CONST, CIN, ELSE, COUT, VAR, FUNCTION, NEWB, ENDB, IF,  WHILE, REPEAT, CALL, HASH,
    	EXTRACTION, INSERTION, EXIT,
        // Data Types
    	CHAR, INT, FLOAT,
        // Identifiers and Values
    	FLOAT_VALUE, IDENTIFIER, INTEGER_VALUE, 
        // Operators
        DIV, ASSIGN, LESS_THAN_EQUAL, MINUS, EQUAL, DIVIDE, MOD, MULTIPLY, GREATER_THAN_EQUAL, LESS_THAN, PLUS, GREATER_THAN,NOT_EQUAL,
        
    }
}