package compiler;

enum TokenType {
    KEYWORD,
    NUMBER,
    IDENTIFIER,
};

public class Token {
    private String value;
    private int classification;
    private int lineNumber;

    public Token(String value, int classification, int lineNumber) {
        this.value = value;
        this.classification = classification;
        this.lineNumber = lineNumber;
    }

    public String getValue() {
        return value;
    }

    public int getClassification() {
        return classification;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public TokenType getType() {
        switch (classification) {
            case 0:
                return TokenType.KEYWORD;
            case 1:
                return TokenType.NUMBER;
            case 2:
                return TokenType.IDENTIFIER;
        }
        return TokenType.KEYWORD;
    }

    public boolean isKeyword() {
        return getClassification() == 0;
    }

    @Override
    public String toString() {
        return "Token [value=" + value + ", classification=" + classification + ", lineNumber=" + lineNumber + "]";
    }

}