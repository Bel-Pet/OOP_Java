package parser;

import error.Reporter;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private List<Expr> getExpressions(String text, int maxMessages) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        Reporter reporter = new Reporter(maxMessages);
        return Parser.parse(Lexer.scan(br, reporter), reporter);
    }

    private List<String> getReporters(String text, int maxMessages) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        Reporter reporter = new Reporter(maxMessages);
        Parser.parse(Lexer.scan(br, reporter), reporter);
        return reporter.getReporters().size() == 0 ? null : reporter.getReporters();
    }

    private static void assertReporters(List<String> reporters, String... expected) {
        MatcherAssert.assertThat(reporters, is(List.of(expected)));
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

    @Test
    public void emptyData() {
        assertNull(getExpressions("", 4));
        assertNull(getReporters("", 4));
    }

    @Test
    public void noEndArray() {
        String str = "l i34e 2:we";
        Token token = new Token(TokenType.LIST, 1, 1, null);
        assertNull(getExpressions(str, 4));
        assertReporters(getReporters(str, 4),
                unexpectedToken("No end complex char, complex type:", token, TokenType.TYPE_END));
    }

    @Test
    public void correctArray() {
        String str = "l i34e 2:we e";
        Token token = new Token(TokenType.LIST, 1, 1, null);
        //assertNull(getExpressions(str, 4));
        assertNull(getReporters(str, 4));
    }

    @Test
    public void noEndDictionary() {
        String str = "d 2:we i34e";
        Token token = new Token(TokenType.DICTIONARY, 1, 1, null);
        assertNull(getExpressions(str, 4));
        assertReporters(getReporters(str, 4),
                unexpectedToken("No end complex char, complex type:", token, TokenType.TYPE_END));
    }

    @Test
    public void correctDictionary() {
        String str = "d 2:we i34e e";
        Token token = new Token(TokenType.LIST, 1, 1, null);
        //assertNull(getExpressions(str, 4));
        assertNull(getReporters(str, 4));
    }

    @Test
    public void keyIsNotStringInDictionary() {
        String str = "d i34e i34e e";
        Token token = new Token(TokenType.INTEGER, 1, 6, 34);
        assertNull(getExpressions(str, 4));
        assertReporters(getReporters(str, 4),
                unexpectedToken("Invalid key", token, TokenType.STRING));
    }

    @Test
    public void duplicateKeyInDictionary() {
        String str = "d 2:er i34e 2:er i55e e";
        Token token = new Token(TokenType.STRING, 1, 16, 34);
        assertNull(getExpressions(str, 4));
        assertReporters(getReporters(str, 4),
                unexpectedToken("Repeating key", token, TokenType.STRING));
    }

    @Test
    public void expectedValueAfterKeyInDictionary() {
        String str = "d 3:qwe e";
        Token token = new Token(TokenType.TYPE_END, 1, 9, null);
        assertNull(getExpressions(str, 4));
        assertReporters(getReporters(str, 4),
                unexpectedToken("Expected value", token,
                        TokenType.INTEGER, TokenType.STRING, TokenType.LIST, TokenType.DICTIONARY));
    }

    @Test
    public void noValueAndEndTypeInDictionary() {
        String str = "d 3:qwe";
        assertNull(getExpressions(str, 4));
        assertReporters(getReporters(str, 4), "Expected value and end complex type in end of file");
    }

    @Test
    public void minorErrorsInLexer() {
        assertNotNull(getExpressions("t d r 4:rt\u06F0y g i34e e y", 7));
    }

    @Test
    public void correctData() {
        String str = """
                d
                3:qwe i34e
                5:trrew l i87e 1:g l i5e e
                e
                """;
        assertNotNull(getExpressions(str, 7));
        assertNull(getReporters(str, 7));
    }
}