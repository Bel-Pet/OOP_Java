package parser;

import error.Reporter;
import lexer.Token;
import lexer.TokenType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private List<Expr> getExpressionsWithTokenTypes(List<TokenType> types) {
        Reporter reporter = new Reporter(10);
        List<Token> tokens = new ArrayList<>();
        types.forEach(value -> tokens.add(new Token(value, 0, 0, null)));
        return Parser.parse(tokens, reporter);
    }

    private List<Expr> getExpressionsWithTokens(List<Token> tokens) {
        Reporter reporter = new Reporter(10);
        return Parser.parse(tokens, reporter);
    }

    private Token createToken(TokenType type, Object value) {
        return new Token(type, 0, 0, value);
    }

    @Test
    public void noEndList() {
        assertNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.LIST, TokenType.STRING))));
    }

    @Test
    public void noEndDictionary() {
        assertNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.DICTIONARY,
                TokenType.STRING, TokenType.INTEGER))));
    }

    @Test
    public void keyIsNotStringInDictionary() {
        assertNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.DICTIONARY,
                TokenType.INTEGER, TokenType.INTEGER, TokenType.TYPE_END))));
    }

    @Test
    public void noValueAndEndTypeInDictionary() {
        assertNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.DICTIONARY, TokenType.STRING))));
    }

    @Test
    public void expectedValueAfterKeyInDictionary() {
        assertNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.DICTIONARY,
                TokenType.STRING, TokenType.TYPE_END))));
    }

    @Test
    public void duplicateKeyInDictionary() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(createToken(TokenType.DICTIONARY, null));
        tokens.add(createToken(TokenType.STRING, "ada"));
        tokens.add(createToken(TokenType.INTEGER, null));
        tokens.add(createToken(TokenType.STRING, "ada"));
        tokens.add(createToken(TokenType.INTEGER, null));
        tokens.add(createToken(TokenType.TYPE_END, null));
        assertNull(getExpressionsWithTokens(tokens));
    }

    @Test
    public void wrongOrder() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(createToken(TokenType.DICTIONARY, null));
        tokens.add(createToken(TokenType.STRING, "bda"));
        tokens.add(createToken(TokenType.INTEGER, null));
        tokens.add(createToken(TokenType.STRING, "ada"));
        tokens.add(createToken(TokenType.INTEGER, null));
        tokens.add(createToken(TokenType.TYPE_END, null));
        assertNull(getExpressionsWithTokens(tokens));
    }

    @Test
    public void oneNumber() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.INTEGER))));
    }

    @Test
    public void oneString() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.STRING))));
    }

    @Test
    public void emptyDictionary() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.DICTIONARY, TokenType.TYPE_END))));
    }

    @Test
    public void numberInDictionary() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.DICTIONARY
                , TokenType.STRING, TokenType.INTEGER, TokenType.TYPE_END))));
    }

    @Test
    public void stringInDictionary() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.DICTIONARY
                , TokenType.STRING, TokenType.STRING, TokenType.TYPE_END))));
    }

    @Test
    public void emptyList() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.LIST, TokenType.TYPE_END))));
    }

    @Test
    public void numberInList() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.LIST
                , TokenType.INTEGER, TokenType.TYPE_END))));
    }

    @Test
    public void stringInList() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.LIST
                , TokenType.STRING, TokenType.TYPE_END))));
    }

    @Test
    public void dictionaryInDictionary() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.DICTIONARY, TokenType.STRING
                , TokenType.DICTIONARY, TokenType.STRING, TokenType.INTEGER, TokenType.TYPE_END, TokenType.TYPE_END))));
    }

    @Test
    public void listInDictionary() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.DICTIONARY
                , TokenType.STRING, TokenType.LIST, TokenType.TYPE_END, TokenType.TYPE_END))));
    }

    @Test
    public void listInList() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.LIST
                , TokenType.LIST, TokenType.TYPE_END, TokenType.TYPE_END))));
    }

    @Test
    public void dictionaryInList() {
        assertNotNull(getExpressionsWithTokenTypes(new ArrayList<>(List.of(TokenType.LIST, TokenType.DICTIONARY
                , TokenType.STRING, TokenType.INTEGER, TokenType.TYPE_END, TokenType.TYPE_END))));
    }

    @Test
    public void complexData() {
        List<Token> tokens = new ArrayList<>();
        tokens.add(createToken(TokenType.DICTIONARY, null));
        tokens.add(createToken(TokenType.STRING, "adas"));
        tokens.add(createToken(TokenType.INTEGER, 6));
        tokens.add(createToken(TokenType.STRING, "bsd"));
        tokens.add(createToken(TokenType.INTEGER, 5));
        tokens.add(createToken(TokenType.STRING, "fgh"));
        tokens.add(createToken(TokenType.LIST, null));
        tokens.add(createToken(TokenType.LIST, null));
        tokens.add(createToken(TokenType.INTEGER, 435));
        tokens.add(createToken(TokenType.STRING, "qwert"));
        tokens.add(createToken(TokenType.DICTIONARY, null));
        tokens.add(createToken(TokenType.STRING, "gt"));
        tokens.add(createToken(TokenType.INTEGER, 12));
        tokens.add(createToken(TokenType.TYPE_END, null));
        tokens.add(createToken(TokenType.INTEGER, 78));
        tokens.add(createToken(TokenType.TYPE_END, null));
        tokens.add(createToken(TokenType.TYPE_END, null));
        tokens.add(createToken(TokenType.STRING, "kjftgy"));
        tokens.add(createToken(TokenType.DICTIONARY, null));
        tokens.add(createToken(TokenType.STRING, "kds"));
        tokens.add(createToken(TokenType.INTEGER, 345));
        tokens.add(createToken(TokenType.TYPE_END, null));
        tokens.add(createToken(TokenType.TYPE_END, null));
        assertNotNull(getExpressionsWithTokens(tokens));
        /*      d
                    4:adas i6e
                    3:bsd i5e
                    3:fgh l l i435e 5:qwert
                        d
                            2:gt i12e
                        e
                        i78e
                        e
                        e
                    6:kjftgy d
                                3:kds i345e
                             e
                e*/
    }
}
