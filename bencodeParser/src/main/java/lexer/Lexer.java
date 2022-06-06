package lexer;
import error.Reporter;
import error.TranslateBencodeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final BufferedReader br;
    private final Reporter reporter;
    private final List<Token> tokens = new ArrayList<>();

    private String line;
    private int nLine;
    private int position;
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
                } catch (TranslateBencodeException e) {
                    reporter.report(e.getMessage());
                    return null;
                }
            }
        }

        return reporter.hasErrors() || tokens.size() == 0 ? null : tokens;
    }

    private void valueType() throws TranslateBencodeException {
        if (isDigit(line.charAt(position))) {
            addString();
            return;
        }

        switch (line.charAt(position)) {
            case 'i' -> addNumber();
            case 'l' -> addComplexType(TokenType.LIST);
            case 'd' -> addComplexType(TokenType.DICTIONARY);
            case 'e' -> addComplexType(TokenType.TYPE_END);
            default -> {
                if (!reporter.report(errorPosition("Unknown")))
                    throw new TranslateBencodeException("Limit error messages");
                position++;
            }
        }
    }

    private void addComplexType(TokenType type) {
        position++;
        tokens.add(new Token(type, nLine, position, null));
    }

    private void addNumber() throws TranslateBencodeException {
        position++;
        Integer number = getNumber('e');
        tokens.add(new Token(TokenType.INTEGER, nLine, position, number));
    }

    private void addString() {
        int size = getNumber(':');
        if (position + size > line.length()) {
            position--;
            throw new TranslateBencodeException(errorPosition("Missing characters in string\nStart position of string:"));
        }

        String str = line.substring(position, position + size);
        if (str.chars().noneMatch(c -> c < ASCII_BARRIER))
            throw new TranslateBencodeException(errorPosition("This string contains non ascii char"));

        tokens.add(new Token(TokenType.STRING, nLine, position, str));
        position += size;
    }

    private int getNumber(char endChar) {
        StringBuilder builder = new StringBuilder();
        int startPosition = position;

        while (position < line.length() && line.charAt(position) != endChar) {
            if (!isDigit(line.charAt(position))) {
                if (!reporter.report(errorPosition("Expected number")))
                    throw new TranslateBencodeException("Limit error messages");
                position++;
                continue;
            }
            builder.append(line.charAt(position));
            position++;
        }

        if (position >= line.length() || line.charAt(position) != endChar) {
            position--;
            throw new TranslateBencodeException(errorPosition("Expected '" + endChar + "' after"));
        }

        if (builder.length() == 0)
            throw new TranslateBencodeException(errorPosition("No number"));

        try {
            position++;
            return Integer.parseInt(String.valueOf(builder));
        } catch (NumberFormatException e) {
            position = startPosition;
            throw new TranslateBencodeException(errorPosition("Too long number"));
        }
    }

    private String getLine() {
        try {
            nLine++;
            position = 0;
            return br.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
