package parser;

import error.Reporter;
import error.TranslateBencodeException;
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
        List<Expr> expressions = new ArrayList<>();

        while (position < tokens.size()) {
            try {
                expressions.add(parseExpr());
            } catch (TranslateBencodeException e) {
                reporter.report(e.getMessage());
                return null;
            }
        }
        return expressions;
    }

    private Expr parseExpr() throws TranslateBencodeException {
        return switch (tokens.get(position).tokenType()) {
            case LIST -> parseList();
            case DICTIONARY -> parseDictionary();
            case STRING -> parseString();
            case INTEGER -> parseInteger();
            default -> {
                String message = unexpectedToken("Expected value",
                        tokens.get(position), TokenType.INTEGER, TokenType.STRING, TokenType.LIST, TokenType.DICTIONARY);

                throw new TranslateBencodeException(message);
            }
        };
    }

    private Expr parseString() {
        position++;
        return new Expr.Line((String) tokens.get(position - 1).value());
    }

    private Expr parseInteger() {
        position++;
        return new Expr.Number((Integer) tokens.get(position - 1).value());
    }

    private Expr parseList() throws TranslateBencodeException {
        int startType = position;
        position++;

        List<Expr> list = new ArrayList<>();

        while (position < tokens.size()) {
            if (tokens.get(position).tokenType() == TokenType.TYPE_END) {
                position++;
                return new Expr.Array(list);
            }

            list.add(parseExpr());
        }

        throw new TranslateBencodeException(unexpectedToken("No end complex char, complex type:",
                                                            tokens.get(startType), TokenType.TYPE_END));
    }

    private Expr parseDictionary() throws TranslateBencodeException {
        int startType = position;
        position++;

        LinkedHashMap<String, Expr> map = new LinkedHashMap<>();
        String previousKey = null;

        while (position < tokens.size()) {
            if (tokens.get(position).tokenType() == TokenType.TYPE_END) {
                position++;
                return new Expr.Dictionary(map);
            }

            String key = addKey(previousKey);
            previousKey = key;

            if (position >= tokens.size())
                throw new TranslateBencodeException("Expected value and end complex type in end of file");

            Expr value = parseExpr();
            map.put(key, value);
        }

        throw new TranslateBencodeException(unexpectedToken("No end complex char, complex type:"
                                                            , tokens.get(startType), TokenType.TYPE_END));
    }

    private String addKey(String previousKey) throws TranslateBencodeException {
        if (tokens.get(position).tokenType() != TokenType.STRING)
            throw new TranslateBencodeException(unexpectedToken("Invalid key"
                                                , tokens.get(position), TokenType.STRING));

        String key = (String) tokens.get(position).value();

        if (previousKey != null && previousKey.compareTo(key) >= 0)
            throw new TranslateBencodeException(unexpectedToken("Wrong key order"
                                                                , tokens.get(position), TokenType.STRING));

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
