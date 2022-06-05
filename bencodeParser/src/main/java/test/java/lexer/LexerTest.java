package lexer;

import error.Reporter;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {

    private List<TokenType> getTokenTypes(String text) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        Reporter reporter = new Reporter(10);

        List<Token> tokens = Lexer.scan(br, reporter);

        return tokens == null ? null : tokens.stream().map(Token::tokenType).toList();
    }

    @Test
    public void emptyData() {
        assertNull(getTokenTypes(""));
    }

    @Test
    public void noAsciiChar() {
        assertNull(getTokenTypes("i23e\u0660\u06F0i66e"));
    }

    @Test
    public void unknownChar() {
        assertNull(getTokenTypes("f l x i34e 3:qwe e"));
    }

    @Test
    public void noTypeEndInNumber() {
        assertNull(getTokenTypes("i45"));
        assertNull(getTokenTypes("i"));
    }

    @Test
    public void noColonInString() {
        assertNull(getTokenTypes("45bgf"));
    }

    @Test
    public void missingCharsInString() {
        assertNull(getTokenTypes("5:we"));
    }

    @Test
    public void unknownCharInNumber() {
        assertNull(getTokenTypes("1f1:wertyuiopas i4g5e"));
    }

    @Test
    public void expectedNumberInType() {
        assertNull(getTokenTypes("ie"));
    }
}
