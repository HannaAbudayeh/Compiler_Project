package pro;

class Token {
    enum TokenType {
        // Keywords
    	INCLUDE, CONST, VAR, FUNCTION, NEWB, ENDB, CIN, COUT, IF, ELSE, WHILE, UNTIL, REPEAT, CALL, HASH,
    	EXTRACTION, INSERTION, 
    	
        DO, THEN, ELSEIF,
        // Data Types
        INT, FLOAT, CHAR,
        // Operators
        ASSIGN, PLUS, MINUS, MULTIPLY, DIVIDE, MOD, DIV, EQUAL, NOT_EQUAL, LESS_THAN, LESS_THAN_EQUAL, GREATER_THAN,
        GREATER_THAN_EQUAL,
        // Parentheses and Delimiters
        OPEN_PAREN, CLOSE_PAREN, SEMICOLON, COMMA, DOT, COLON,
        // Identifiers and Values
        IDENTIFIER, INTEGER_VALUE, FLOAT_VALUE,
        // End of File
        EXIT
    }
    
    private final int line; 
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value,int line) {
        this.type = type;
        this.value = value;
    	this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", line=" + line +
                '}';
    }
}