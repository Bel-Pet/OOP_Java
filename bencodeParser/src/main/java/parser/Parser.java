package parser;

import error.Reporter;
import lexer.Token;
import lexer.TokenType;

import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private final Reporter reporter;
    private int position;

    private Parser(List<Token> tokens, Reporter reporter) {
        this.tokens = tokens;
        this.reporter = reporter;
    }

    public static List<Expr> parse(List<Token> tokens, Reporter reporter) {
        Parser parser = new Parser(tokens, reporter);
        return parser.parse();
    }

    private List<Expr> parse() {
        if (tokens == null) return null;
        List<Expr> expressions = new ArrayList<>();
        while (position < tokens.size()) {
            try {
                expressions.add(parseExpr());
            } catch (ParserException e) {
                reporter.report(e.getMessage());
                return null;
            }
        }
        return expressions;
    }

    private Expr parseExpr() throws ParserException {
        return switch (tokens.get(position).tokenType()) {
            case LIST -> parseList();
            case DICTIONARY -> parseDictionary();
            default -> parseSimpleType();
        };
    }

    private Expr parseList() throws ParserException {
        int startType = position;
        position++;
        List<Expr> list = new ArrayList<>();
        while (position < tokens.size()) {
            if (tokens.get(position).tokenType() == TokenType.TYPE_END) {
                position++;
                return new Expr.Array(list);
            }
            position++;
            list.add(parseExpr());
        }
        String message = unexpectedToken("No end complex char, complex type:", tokens.get(startType), TokenType.TYPE_END);
        throw new ParserException(message);
    }

    private Expr parseDictionary() throws ParserException {
        int startType = position;
        position++;
        Map<String, Expr> map = new HashMap<>();
        while (position < tokens.size()) {
            if (tokens.get(position).tokenType() == TokenType.TYPE_END) {
                position++;
                return new Expr.Dictionary(map);
            }
            String key = addKey(map);
            if (position >= tokens.size()) {
                String message = "Expected value and end complex type in end of file";
                throw new ParserException(message);
            }
            Expr value = parseExpr();
            map.put(key, value);
        }
        String message = unexpectedToken("No end complex char, complex type:", tokens.get(startType), TokenType.TYPE_END);
        throw new ParserException(message);
    }

    private Expr parseSimpleType() throws ParserException {
        if (tokens.get(position).tokenType() == TokenType.STRING) {
            Expr expr = new Expr.Line((String) tokens.get(position).value());
            position++;
            return expr;
        }
        if (tokens.get(position).tokenType() == TokenType.INTEGER) {
            Expr expr = new Expr.Number((Integer) tokens.get(position).value());
            position++;
            return expr;
        }
        String message = unexpectedToken("Expected value", tokens.get(position), TokenType.INTEGER, TokenType.STRING, TokenType.LIST, TokenType.DICTIONARY);
        throw new ParserException(message);
    }

    private String addKey(Map<String, Expr> map) {
        if (tokens.get(position).tokenType() != TokenType.STRING) {
            String message = unexpectedToken("Invalid key", tokens.get(position), TokenType.STRING);
            throw new ParserException(message);
        }
        String key = (String) tokens.get(position).value();
        if (map.containsKey(key)) {
            String message = unexpectedToken("Repeating key", tokens.get(position), TokenType.STRING);
            throw new ParserException(message);
        }
        position++;
        return key;
    }

    private String unexpectedToken(String message, Token token, TokenType... expected) {
        String position = "Line " + token.nLine() + ", position: " + token.pos();
        return """
                %s
                %s
                Expected tokens: %s,
                Actual: %s
                """.formatted(message, position, Arrays.toString(expected), token.tokenType());
    }
}
