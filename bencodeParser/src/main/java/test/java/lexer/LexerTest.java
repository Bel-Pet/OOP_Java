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

    private List<TokenType> getTokenTypes(String text) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        Reporter reporter = new Reporter(10);

        List<Token> tokens = Lexer.scan(br, reporter);

        return tokens == null ? null : tokens.stream().map(Token::tokenType).toList();
    }

    private static void assertTypes(List<TokenType> types, TokenType... expected) {
        MatcherAssert.assertThat(types, is(List.of(expected)));
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
    public void unknownCharInNumber() {
        assertNull(getTokenTypes("1f1:wertyuiopas"));
        assertNull(getTokenTypes("i4g5e"));
    }

    @Test
    public void emptyNumber() {
        assertNull(getTokenTypes("ie"));
    }

    @Test
    public void noColonInString() {
        assertNull(getTokenTypes("45bgf"));
        assertNull(getTokenTypes("45"));
    }

    @Test
    public void missingCharsInString() {
        assertNull(getTokenTypes("5:we"));
        assertNull(getTokenTypes("45:"));
    }

    @Test
    public void oneNumber() {
        assertTypes(getTokenTypes("i34e"), TokenType.INTEGER);
    }

    @Test
    public void oneString() {
        assertTypes(getTokenTypes("3:qwe"), TokenType.STRING);
    }

    @Test
    public void emptyDictionary() {
        assertTypes(getTokenTypes("de"), TokenType.DICTIONARY, TokenType.TYPE_END);
    }

    @Test
    public void numberInDictionary() {
        assertTypes(getTokenTypes("d 3:qwe i34e e")
                , TokenType.DICTIONARY, TokenType.STRING, TokenType.INTEGER, TokenType.TYPE_END);
    }

    @Test
    public void stringInDictionary() {
        assertTypes(getTokenTypes("d 3:qwe 4:efgh e")
                , TokenType.DICTIONARY, TokenType.STRING, TokenType.STRING, TokenType.TYPE_END);
    }

    @Test
    public void emptyList() {
        assertTypes(getTokenTypes("le"), TokenType.LIST, TokenType.TYPE_END);
    }

    @Test
    public void numberInList() {
        assertTypes(getTokenTypes("l i34e e"), TokenType.LIST, TokenType.INTEGER, TokenType.TYPE_END);
    }

    @Test
    public void stringInList() {
        assertTypes(getTokenTypes("l 2:er e"), TokenType.LIST, TokenType.STRING, TokenType.TYPE_END);
    }

    @Test
    public void dictionaryInDictionary() {
        assertTypes(getTokenTypes("d 2:rt d 3:qwe 4:efgh e e")
                , TokenType.DICTIONARY, TokenType.STRING, TokenType.DICTIONARY
                , TokenType.STRING, TokenType.STRING, TokenType.TYPE_END, TokenType.TYPE_END);
    }

    @Test
    public void listInDictionary() {
        assertTypes(getTokenTypes("d 2:rt l 4:efgh e e")
                , TokenType.DICTIONARY, TokenType.STRING, TokenType.LIST
                , TokenType.STRING, TokenType.TYPE_END, TokenType.TYPE_END);
    }

    @Test
    public void listInList() {
        assertTypes(getTokenTypes("l 2:rt l 4:efgh e e")
                , TokenType.LIST, TokenType.STRING, TokenType.LIST
                , TokenType.STRING, TokenType.TYPE_END, TokenType.TYPE_END);
    }

    @Test
    public void dictionaryInList() {
        assertTypes(getTokenTypes("l d 3:qwe 4:efgh e e")
                , TokenType.LIST, TokenType.DICTIONARY
                , TokenType.STRING, TokenType.STRING, TokenType.TYPE_END, TokenType.TYPE_END);
    }

    @Test
    public void complexData() {
        String str = """
                d
                4:adas i6e
                3:bsd i5e
                3:fgh l l i435e 5:qwert
                    d   2:gt i12e
                        4:vfrd 2:rt
                    e
                    i78e
                    e
                    e
                6:kjftgy d
                         3:kds i345e
                         e
                3:vfd l 4:dfgg 3:bgf i45e e
                e
                """;
        assertTypes(getTokenTypes(str), TokenType.DICTIONARY, TokenType.STRING, TokenType.INTEGER
                , TokenType.STRING, TokenType.INTEGER, TokenType.STRING, TokenType.LIST, TokenType.LIST
                , TokenType.INTEGER, TokenType.STRING, TokenType.DICTIONARY, TokenType.STRING, TokenType.INTEGER
                , TokenType.STRING, TokenType.STRING, TokenType.TYPE_END, TokenType.INTEGER, TokenType.TYPE_END
                , TokenType.TYPE_END, TokenType.STRING, TokenType.DICTIONARY, TokenType.STRING, TokenType.INTEGER
                , TokenType.TYPE_END, TokenType.STRING, TokenType.LIST, TokenType.STRING, TokenType.STRING
                , TokenType.INTEGER, TokenType.TYPE_END, TokenType.TYPE_END);
    }
}
