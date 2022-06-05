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
    private boolean hasError = false;
    private static final int ASCII_BARRIER = 127;

    private Lexer(BufferedReader br, Reporter reporter) {
        this.br = br;
        this.reporter = reporter;
    }

    public static List<Token> scan(BufferedReader br, Reporter reporter) {
        Lexer lexer = new Lexer(br, reporter);
        return lexer.scan();
    }

    private List<Token> scan() {
        while ((line = getLine()) != null) {
            while (position < line.length()) {
                if (Character.isWhitespace(line.charAt(position))) {
                    position++;
                    continue;
                }

                try {
                    valueType();
                } catch (LexerException e) {
                    reporter.report(e.getMessage());
                    return null;
                }
            }
        }

        return hasError || tokens.size() == 0 ? null : tokens;
    }

    private void valueType() throws LexerException {
        if (isDigit()) {
            addString();
            return;
        }

        switch (line.charAt(position)) {
            case 'i' -> addNumber();
            case 'l' -> addComplexType(TokenType.LIST);
            case 'd' -> addComplexType(TokenType.DICTIONARY);
            case 'e' -> addComplexType(TokenType.TYPE_END);
            default -> {
                hasError = true;
                if (!reporter.report(errorPosition("Unknown")))
                    throw new LexerException("Limit error messages");
                position++;
            }
        }
    }

    private void addComplexType(TokenType type) {
        position++;
        tokens.add(new Token(type, nLine, position, null));
    }

    private void addNumber() throws LexerException {
        position++;
        Integer number = getNumber('e');
        tokens.add(new Token(TokenType.INTEGER, nLine, position, number));
    }

    private void addString() throws LexerException {
        int size = getNumber(':');
        if (position + size > line.length())
            throw new LexerException(errorPosition("Missing characters in string\nStart position of string:"));

        String str = line.substring(position, position + size);
        if (str.chars().noneMatch(c -> c <= ASCII_BARRIER))
            throw new LexerException(errorPosition("This string contains not ascii char"));

        tokens.add(new Token(TokenType.STRING, nLine, position, str));
        position += size;
    }

    private Integer getNumber(char endChar) throws LexerException {
        StringBuilder buffer = new StringBuilder();

        while (position < line.length() && line.charAt(position) != endChar) {
            if (!isDigit()) {
                hasError = true;
                if (!reporter.report(errorPosition("Expected number")))
                    throw new LexerException("Limit error messages");
                position++;
                continue;
            }
            buffer.append(line.charAt(position));
            position++;
        }

        if (position >= line.length() || line.charAt(position) != endChar) {
            position--;
            throw new LexerException(errorPosition("Expected '" + endChar + "' after"));
        }

        if (buffer.length() == 0)
            throw new LexerException(errorPosition("No number"));

        try {
            position++;
            return Integer.parseInt(String.valueOf(buffer));
        } catch (NumberFormatException e) {
            throw new LexerException(errorPosition("Too long number"));
        }
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

    private boolean isDigit() {
        return line.charAt(position) >= '0' && line.charAt(position) <= '9';
    }

    private String errorPosition(String message) {
        return """
                %s char '%c' at line %d:
                %s
                %s^--- here
                """.formatted(message, line.charAt(position), nLine, line, " ".repeat(position));
    }
}
