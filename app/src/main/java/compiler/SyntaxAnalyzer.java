package compiler;

import java.util.Arrays;
import java.util.List;

public class SyntaxAnalyzer {
    private List<Token> tokens;
    private int currentTokenIndex;
    private Token currentToken;

    public SyntaxAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        currentTokenIndex = 0;
        if (!tokens.isEmpty())
            currentToken = tokens.get(currentTokenIndex);
        program();
    }

    private int getTokenTypeIndex(TokenType type) {
        switch (type) {
            case KEYWORD:
                return 0;
            case NUMBER:
                return 1;
            case IDENTIFIER:
                return 2;
            default:
                return 0;
        }
    }

    public TokenType getTokenTypeFromIndex(int tt) {
        switch (tt) {
            case 0:
                return TokenType.KEYWORD;
            case 1:
                return TokenType.NUMBER;
            case 2:
                return TokenType.IDENTIFIER;
        }
        return TokenType.KEYWORD;
    }

    private boolean match(String tn, int tt) {
        System.out.println(currentToken);
        if (((tt == 0) && (tn.compareToIgnoreCase(currentToken.getValue()) == 0)
                && (tt == currentToken.getClassification()))
                || ((tt == 1 || tt == 2) && (tt == currentToken.getClassification()))) {
            nextToken();
            return true;
        } else {
            error(tn, getTokenTypeFromIndex(tt));
            // Best way is to list all errors in user program from the first
            // to make user edit them all and then run again
            
            // nextToken();
        }
        return false;
    }

    private void nextToken() {
        if (currentTokenIndex < tokens.size() - 1) {
            currentTokenIndex++;
            currentToken = tokens.get(currentTokenIndex);
        }
    }

    private void previousToken() {
        if (currentTokenIndex > 0 && currentTokenIndex < tokens.size() - 1) {
            currentTokenIndex--;
            currentToken = tokens.get(currentTokenIndex);
        }
    }

    private void error(String tn, TokenType tt) {
        if (tt == TokenType.KEYWORD) {
            System.err.println(">>> It is expected to have a keyword '" + tn + "' in line: "
                    + tokens.get(currentTokenIndex).getLineNumber() + " <<<");
        } else if (tt == TokenType.NUMBER) {
            System.err.println(">>> It is expected to have a numeric constant in line: "
                    + tokens.get(currentTokenIndex).getLineNumber() + " <<<");
        } else if (tt == TokenType.IDENTIFIER) {
            System.err.println(">>> It is expected to have an identifier in line: "
                    + tokens.get(currentTokenIndex).getLineNumber() + " <<<");
        }
    }

    private void program() {
        match("PROGRAM", getTokenTypeIndex(TokenType.KEYWORD));
        match("", getTokenTypeIndex(TokenType.IDENTIFIER));
        match(";", getTokenTypeIndex(TokenType.KEYWORD));
        declarations();
        subprogram_declarations();
        compound_statement(false);
        match(".", getTokenTypeIndex(TokenType.KEYWORD));
    }

    private void identifier_list() {
        match("", getTokenTypeIndex(TokenType.IDENTIFIER));
        while (currentToken.getValue().compareTo(",") == 0) {
            match(",", getTokenTypeIndex(TokenType.KEYWORD));
            match("", getTokenTypeIndex(TokenType.IDENTIFIER));
        }
    }

    private void declarations() {
        while (currentToken.getValue().compareToIgnoreCase("VAR") == 0) {
            match("VAR", getTokenTypeIndex(TokenType.KEYWORD));
            identifier_list();
            match(":", getTokenTypeIndex(TokenType.KEYWORD));
            type();
            match(";", getTokenTypeIndex(TokenType.KEYWORD));
        }
    }

    private void type() {
        switch (currentToken.getValue()) {
            case "INTEGER":
                match("INTEGER", getTokenTypeIndex(TokenType.KEYWORD));
                break;
            case "REAL":
                match("REAL", getTokenTypeIndex(TokenType.KEYWORD));
                break;
            case "BOOLEAN":
                match("BOOLEAN", getTokenTypeIndex(TokenType.KEYWORD));
                break;
            case "ARRAY":
                match("ARRAY", getTokenTypeIndex(TokenType.KEYWORD));
                match("[", getTokenTypeIndex(TokenType.KEYWORD));
                match("", getTokenTypeIndex(TokenType.NUMBER));
                match("..", getTokenTypeIndex(TokenType.KEYWORD));
                match("", getTokenTypeIndex(TokenType.NUMBER));
                match("]", getTokenTypeIndex(TokenType.KEYWORD));
                match("OF", getTokenTypeIndex(TokenType.KEYWORD));
                standard_type();
                break;

        }
    }

    private void standard_type() {
        if (currentToken.getValue().compareTo("INTEGER") == 0) {
            match("INTEGER", getTokenTypeIndex(TokenType.KEYWORD));
            return;
        } else if (currentToken.getValue().compareTo("REAL") == 0) {
            match("REAL", getTokenTypeIndex(TokenType.KEYWORD));
            return;
        }
    }

    private void subprogram_declarations() {
        while (subprogram_declaration()) {
            if(currentToken.getValue().compareToIgnoreCase("END") == 0) {
                continue;
            }
            match(";", getTokenTypeIndex(TokenType.KEYWORD));
        }
    }

    private boolean subprogram_declaration() {
        if (subprogram_head()) {
            declarations();
            compound_statement(true);
            return true;
        } else {
            return false;
        }
    }

    private boolean subprogram_head() {
        switch (currentToken.getValue()) {
            case "PROCEDURE":
                match("PROCEDURE", getTokenTypeIndex(TokenType.KEYWORD));
                match("", getTokenTypeIndex(TokenType.IDENTIFIER));
                match("(", getTokenTypeIndex(TokenType.KEYWORD));
                parameter_list();
                match(")", getTokenTypeIndex(TokenType.KEYWORD));
                match(":", getTokenTypeIndex(TokenType.KEYWORD));
                standard_type();
                match(";", getTokenTypeIndex(TokenType.KEYWORD));
                return true;
            case "FUNCTION":
                match("FUNCTION", getTokenTypeIndex(TokenType.KEYWORD));
                match("", getTokenTypeIndex(TokenType.IDENTIFIER));
                match("(", getTokenTypeIndex(TokenType.KEYWORD));
                parameter_list();
                match(")", getTokenTypeIndex(TokenType.KEYWORD));
                match(":", getTokenTypeIndex(TokenType.KEYWORD));
                standard_type();
                match(";", getTokenTypeIndex(TokenType.KEYWORD));
                return true;
        }
        return false;
    }

    private void parameter_list() {
        if (currentToken.getValue().compareTo(")") == 0) {
            return;
        } else if (currentToken.getValue().compareTo("(") == 0) {
            match("(", getTokenTypeIndex(TokenType.KEYWORD));
        }else if (currentToken.getValue().compareTo(":") == 0) {
            match(":", getTokenTypeIndex(TokenType.KEYWORD));
            type();
        } else if (currentToken.getValue().compareTo("MOD") == 0) {
            System.out.println("Im here and current value is :" + currentToken.getValue());
            match("MOD", getTokenTypeIndex(TokenType.KEYWORD));
            match("", getTokenTypeIndex(TokenType.IDENTIFIER));
        } else if (currentToken.getValue().compareTo(",") == 0) {
            match(",", getTokenTypeIndex(TokenType.KEYWORD));
        } else {
            match("", getTokenTypeIndex(TokenType.IDENTIFIER));
        }
        parameter_list();
    }

    private void compound_statement(boolean needSemicolon) {
        if (currentToken.getValue().compareToIgnoreCase("BEGIN") == 0) {
            match("BEGIN", getTokenTypeIndex(TokenType.KEYWORD));
        }
        if (currentToken.getValue().compareToIgnoreCase("END") != 0) {
            optional_statements(true);
            // compound_statement(needSemicolon);
        } else {
            match("END", getTokenTypeIndex(TokenType.KEYWORD));
            System.out.println("need semi colon");
            if (needSemicolon) {
                // match(";", getTokenTypeIndex(TokenType.KEYWORD));
            }
            return;
        }
    }

    private void optional_statements(boolean needSemicolon) {
        if(currentToken.getValue().compareToIgnoreCase("END") == 0) {
            return;
        }
        while (currentToken.getType() == TokenType.IDENTIFIER ||
                Arrays.asList("BEGIN", "IF", "WHILE", "READ", "WRITE").contains(currentToken.getValue())) {
            statement(needSemicolon);
        }
    }

    public void statement(boolean needSemicolon) {
        if (currentToken.getType() == TokenType.IDENTIFIER) {
            assign_statement(needSemicolon);
        } else {
            switch (currentToken.getValue()) {
                case "IF":
                    if_statement();
                    break;
                case "WHILE":
                    while_statement();
                    break;
                case "BEGIN":
                    compound_statement(true);
                    break;
                case "READ":
                    // TODO: handle read
                    match("READ", getTokenTypeIndex(TokenType.KEYWORD));
                    match("(", getTokenTypeIndex(TokenType.KEYWORD));
                    parameter_list();
                    match(")", getTokenTypeIndex(TokenType.KEYWORD));
                    match(";", getTokenTypeIndex(TokenType.KEYWORD));
                    // assign_statement(false);
                    break;
                case "WRITE":
                    match("WRITE", getTokenTypeIndex(TokenType.KEYWORD));
                    match("(", getTokenTypeIndex(TokenType.KEYWORD));
                    parameter_list();
                    match(")", getTokenTypeIndex(TokenType.KEYWORD));
                    match(";", getTokenTypeIndex(TokenType.KEYWORD));
                    break;
                default:
                    error(currentToken.getValue(), currentToken.getType());
                    break;
            }
        }
    }

    private void assign_statement(boolean needSemicolon) {
        if (currentToken.getValue().equals("(")) {
            match("(", getTokenTypeIndex(TokenType.KEYWORD));
            parameter_list();
            match(")", getTokenTypeIndex(TokenType.KEYWORD));
            return;
        }
        match("", getTokenTypeIndex(TokenType.IDENTIFIER));
        match(":=", getTokenTypeIndex(TokenType.KEYWORD));
        expression();
        if (needSemicolon) {
            match(";", getTokenTypeIndex(TokenType.KEYWORD));
        }
    }

    private void if_statement() {
        match("IF", getTokenTypeIndex(TokenType.KEYWORD));
        condition();
        match("THEN", getTokenTypeIndex(TokenType.KEYWORD));
        optional_statements(false);
        match("ELSE", getTokenTypeIndex(TokenType.KEYWORD));
        optional_statements(false);
    }

    // whileStatement -> while condition do statement
    private void while_statement() {
        match("WHILE", getTokenTypeIndex(TokenType.KEYWORD));
        condition();
        match("DO", getTokenTypeIndex(TokenType.KEYWORD));
        optional_statements(true);
    }

    private void expression() {
        simple_expression();
        if (relational_operator()) {
            simple_expression();
        }
    }

    private void condition() {
        expression();
        relational_operator();
        expression();
    }

    private boolean relational_operator() {
        switch (currentToken.getType()) {
            case KEYWORD:
                switch (currentToken.getValue()) {
                    case "=":
                    case "MOD":
                    case "<>":
                    case "<":
                    case "<=":
                    case ">":
                    case ">=":
                        match(currentToken.getValue(), getTokenTypeIndex(TokenType.KEYWORD));
                        return true;
                }
            default:
                return false;
        }
    }

    private void simple_expression() {
        term();
        while (currentToken.getValue().compareTo("+") == 0 || currentToken.getValue().compareTo("-") == 0) {
            match(currentToken.getValue(), getTokenTypeIndex(TokenType.KEYWORD));
            term();
        }
    }

    private void term() {
        factor();
        while (currentToken.getValue().compareTo("*") == 0 || currentToken.getValue().compareTo("/") == 0) {
            match(currentToken.getValue(), getTokenTypeIndex(TokenType.KEYWORD));
            factor();
        }
    }

    private void factor() {
        if (currentToken.getType() == TokenType.IDENTIFIER) {
            match("", getTokenTypeIndex(TokenType.IDENTIFIER));
            if (currentToken.getValue().compareTo("(") == 0) {
                match("(", getTokenTypeIndex(TokenType.KEYWORD));
                expression_list();
                match(")", getTokenTypeIndex(TokenType.KEYWORD));
            }
        } else if (currentToken.getType() == TokenType.NUMBER) {
            match("", getTokenTypeIndex(TokenType.NUMBER));
        } else if (currentToken.getValue().compareTo("(") == 0) {
            match("(", getTokenTypeIndex(TokenType.KEYWORD));
            expression();
            match(")", getTokenTypeIndex(TokenType.KEYWORD));
        } else if (currentToken.getValue().compareTo("NOT") == 0) {
            match("NOT", getTokenTypeIndex(TokenType.KEYWORD));
            factor();
        }
    }

    private void expression_list() {
        expression();
        while (currentToken.getValue().compareTo(",") == 0) {
            match(",", getTokenTypeIndex(TokenType.KEYWORD));
            expression();
        }
    }
}