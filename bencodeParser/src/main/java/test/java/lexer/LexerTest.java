package lexer;

import error.Reporter;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {

    private List<TokenType> getTokenTypes(String text, int maxMessages) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        Reporter reporter = new Reporter(maxMessages);
        List<Token> tokens = Lexer.scan(br, reporter);
        return tokens == null ? null : tokens.stream().map(Token::tokenType).toList();
    }

    private static void assertTypes(List<TokenType> types, TokenType... expected) {
        MatcherAssert.assertThat(types, is(List.of(expected)));
    }

    private List<String> getReporters(String text, int maxMessages) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        Reporter reporter = new Reporter(maxMessages);
        Lexer.scan(br, reporter);
        return reporter.getReporters().size() == 0 ? null : reporter.getReporters();
    }

    private static void assertReporters(List<String> reporters, String... expected) {
        MatcherAssert.assertThat(reporters, is(List.of(expected)));
    }

    private String errorPosition(String message, String line, int position, int nLine) {
        return """
                %s char '%c' at line %d:
                %s
                %s^--- here
                """.formatted(message, line.charAt(position), nLine, line, " ".repeat(position));
    }

    @Test
    public void emptyData() {
        assertNull(getTokenTypes("", 1));
        assertNull(getReporters("", 1));
    }

    @Test
    public void limitErrorMessages() {
        String str = "xc";
        assertNull(getTokenTypes(str, 2));
        assertReporters(getReporters(str, 2),
                errorPosition("Unknown", str, 0, 1),
                errorPosition("Unknown", str, 1, 1));
    }

    @Test
    public void noLimitErrorMessages() {
        String str = "i32e xd 2:we i53eef";
        assertTypes(getTokenTypes(str, 5), TokenType.INTEGER, TokenType.DICTIONARY,
                TokenType.STRING, TokenType.INTEGER, TokenType.TYPE_END);
        assertReporters(getReporters(str, 5),
                errorPosition("Unknown", str, 5, 1),
                errorPosition("Unknown", str, 18, 1));
    }

    @Test
    public void noAsciiChar() {
        String str = "i23e\u0660\u06F0i66e";
        assertTypes(getTokenTypes(str, 4), TokenType.INTEGER, TokenType.INTEGER);
        assertReporters(getReporters(str, 4),
                errorPosition("This char not ascii", str, 4, 1),
                errorPosition("This char not ascii", str, 5, 1));
    }

    @Test
    public void unknownChar() {
        String str = "f l x i34e 3:qwe e";
        assertTypes(getTokenTypes(str, 4), TokenType.LIST, TokenType.INTEGER,
                TokenType.STRING, TokenType.TYPE_END);
        assertReporters(getReporters(str, 4), errorPosition("Unknown", str, 0, 1),
                errorPosition("Unknown", str, 4, 1));
    }

    @Test
    public void missingCharsInString() {
        String str = "5:we";
        assertNull(getTokenTypes(str, 4));
        assertReporters(getReporters(str, 4),
                errorPosition("Missing characters in string\nStart position of string:", str, 2, 1));
    }

    @Test
    public void unknownCharInNumber() {
        String str = "1f1:wertyuiopas i4g5e";
        assertTypes(getTokenTypes(str, 4), TokenType.STRING, TokenType.INTEGER);
        assertReporters(getReporters(str, 4),
                errorPosition("Expected number", str, 1, 1),
                errorPosition("Expected number", str, 18, 1));
    }

    @Test
    public void expectedNumberInType() {
        String str = "ie";
        assertNull(getTokenTypes(str, 4));
        assertReporters(getReporters(str, 4), errorPosition("Expected number", str, 1, 1));
    }

    @Test
    public void noEndTypeInList() {
        String str = "l i45e";
        assertTypes(getTokenTypes(str, 4), TokenType.LIST, TokenType.INTEGER);
        assertNull(getReporters(str, 4));
    }

    @Test
    public void noEndTypeInDictionary() {
        String str = "d 3:4r5 i45e";
        assertTypes(getTokenTypes(str, 4), TokenType.DICTIONARY, TokenType.STRING, TokenType.INTEGER);
        assertNull(getReporters(str, 4));
    }

    @Test
    public void keyNotStringInDictionary() {
        String str = "d i34e i45e";
        assertTypes(getTokenTypes(str, 4), TokenType.DICTIONARY, TokenType.INTEGER, TokenType.INTEGER);
        assertNull(getReporters(str, 4));
    }

    @Test
    public void notValueInDictionary() {
        String str = "d 3:4r5";
        assertTypes(getTokenTypes(str, 4), TokenType.DICTIONARY, TokenType.STRING);
        assertNull(getReporters(str, 4));
    }

    @Test
    public void correctData() {
        String str = """
                d
                3:qwe i34e
                5:trrew l i87e 1:g l i5e e e
                e
                """;
        assertTypes(getTokenTypes(str, 4), TokenType.DICTIONARY, TokenType.STRING,
                TokenType.INTEGER, TokenType.STRING, TokenType.LIST, TokenType.INTEGER, TokenType.STRING,
                TokenType.LIST, TokenType.INTEGER, TokenType.TYPE_END, TokenType.TYPE_END, TokenType.TYPE_END);
        assertNull(getReporters(str, 4));
    }
}