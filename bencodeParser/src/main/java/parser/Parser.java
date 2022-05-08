package parser;

import error.ConsoleReporter;
import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private final ConsoleReporter errorReporter;
    private int position;

    private Parser(List<Token> tokens, ConsoleReporter errorReporter) {
        this.tokens = tokens;
        this.errorReporter = errorReporter;
    }

    public static List<Expr> parse(List<Token> tokens, ConsoleReporter errorReporter) {
        Parser parser = new Parser(tokens, errorReporter);
        return parser.parse();
    }

    private List<Expr> parse() {
        List<Expr> expressions = new ArrayList<>();
        while (!matches()) {
            try {
                Expr expr = parseExpr();
                expressions.add(expr);
            } catch (ParserException e) {
                errorReporter.report(e.getMessage());
                return null;
            }
        }
        return expressions;
    }

    private Expr parseExpr() {
        return parseComplexType();
    }

    private Expr parseComplexType() {
        if (tokens.get(position).tokenType() == TokenType.LIST) {
            position++;
            List<Expr> list = new ArrayList<>();
            while (tokens.get(position).tokenType() != TokenType.TYPE_END) {
                list.add(parseExpr());
            }
            position++;
            return new Expr.Array(list);
        }
        if (tokens.get(position).tokenType() == TokenType.DICTIONARY) {
            position++;
            HashMap<Expr, Expr> map = new HashMap<>();
            while (tokens.get(position).tokenType() != TokenType.TYPE_END) {
                Expr key = parseExpr();
                Expr value = parseExpr();
                map.put(key, value);
            }
            position++;
            return new Expr.Dictionary(map);
        }
        return parseSimpleType();
    }

    private Expr parseSimpleType() {
        if (tokens.get(position).tokenType() == TokenType.STRING) {
            Expr expr = new Expr.Line((String) tokens.get(position).value());
            position++;
            return expr;
        }
        if (tokens.get(position).tokenType() == TokenType.INTEGER) {
            Expr expr = new Expr.Number((String) tokens.get(position).value());
            position++;
            return expr;
        }
        String massage = unexpectedToken(tokens.get(position), TokenType.TYPE_END);
        throw new ParserException(massage);
    }

    private static String unexpectedToken(Token token, TokenType... expected) {
        int pos = token.pos();
        String position = pos == -1 ?
                "End of line " + token.nLine() :
                "Line " + token.nLine() + ", position: " + pos;
        return """
                %s
                Expected tokens: %s,
                Actual: %s
                """.formatted(position, Arrays.toString(expected), token.tokenType());
    }

    private boolean matches(TokenType... rest) {
        Token token = tokens.get(position);
        TokenType actual = token.tokenType();
        if (actual == TokenType.EOF) return true;
        for (TokenType expected : rest) {
            if (actual == expected) return true;
        }
        return false;
    }
}
