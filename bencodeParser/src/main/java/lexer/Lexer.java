package lexer;
import error.Reporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final BufferedReader br;
    private final Reporter reporter;
    private final List<Token> tokens = new ArrayList<>();

    private String line;
    private int nLine;
    private int position;

    private Lexer(BufferedReader br, Reporter reporter) {
        this.br = br;
        this.reporter = reporter;
    }

    public static List<Token> scan(BufferedReader br, Reporter reporter) {
        Lexer lexer = new Lexer(br, reporter);
        return lexer.scan();
    }

    private List<Token> scan() {
        if ((line = getLine()) == null) return null;
        return textProcessing() ? tokens : null;
    }

    private boolean textProcessing() {
        do {
            while (position < line.length()) {
                Character c = getChar();
                if (c == null) {
                    if (!reporter.report(errorPosition("This char not ascii"))) {
                        return false;
                    }
                    position++;
                    continue;
                }
                if (Character.isWhitespace(c)) {
                    position++;
                    continue;
                }
                if (!valueType(c)) {
                    return false;
                }
            }
        } while ((line = getLine()) != null);

        return true;
    }

    private boolean valueType(char c) {
        if (isDigit(c)) return addString();
        return switch (c) {
            case 'i' -> addNumber();
            case 'l' -> addComplexType(TokenType.LIST);
            case 'd' -> addComplexType(TokenType.DICTIONARY);
            case 'e' -> addComplexType(TokenType.TYPE_END);
            default -> {
                if (!reporter.report(errorPosition("Unknown"))) {
                    yield false;
                }
                position++;
                yield true;
            }
        };
    }

    private boolean addComplexType(TokenType type) {
        position++;
        tokens.add(new Token(type, nLine, position, null));
        return true;
    }

    private boolean addNumber() {
        position++;
        Integer number = getNumber('e');
        if (number == null) return false;

        tokens.add(new Token(TokenType.INTEGER, nLine, position, number));
        return true;
    }

    private boolean addString() {
        Integer size = getNumber(':');
        if (size == null) return false;

        if (position + size > line.length()) {
            reporter.report(errorPosition("Missing characters in string\nStart position of string:"));
            return false;
        }
        int start = position;
        StringBuilder buffer = new StringBuilder();
        while (position < start + size) {
            Character c = getChar();
            if (c == null) {
                if (!reporter.report(errorPosition("This char not ascii"))) {
                    return false;
                }
                position++;
                continue;
            }
            buffer.append(c);
            position++;
        }
        tokens.add(new Token(TokenType.STRING, nLine, position, String.valueOf(buffer)));
        return true;
    }

    private Integer getNumber(char endChar) {
        StringBuilder buffer = new StringBuilder();
        do {
            if (!isDigit(line.charAt(position))) {
                if (!reporter.report(errorPosition("Expected number"))) {
                    return null;
                }
                position++;
                continue;
            }
            buffer.append(line.charAt(position));
            position++;
        } while (position < line.length() && line.charAt(position) != endChar);
        if (buffer.length() == 0) return null;
        position++;
        return Integer.parseInt(String.valueOf(buffer));
    }

    private String getLine() {
        try {
            nLine++;
            position = 0;
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Character getChar() {
        char c = line.charAt(position);
        return c > 127 ? null : c;
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private String errorPosition(String message) {
        return """
                %s char '%c' at line %d:
                %s
                %s^--- here
                """.formatted(message, line.charAt(position), nLine, line, " ".repeat(position));
    }
}
