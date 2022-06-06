package parser;

import error.Reporter;
import lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private List<Expr> getExpressions(String text) {
        BufferedReader br = new BufferedReader(new StringReader(text));

        Reporter reporter = new Reporter(10);

        // CR: pass tokens
        return Parser.parse(Lexer.scan(br, reporter), reporter);
    }

    // CR: positive tests for Expr types

    @Test
    public void emptyData() {
        assertNull(getExpressions(""));
    }

    @Test
    public void noEndArray() {
        assertNull(getExpressions("l i34e 2:we"));
    }

    @Test
    public void noEndDictionary() {
        assertNull(getExpressions("d 2:we i34e"));
    }

    @Test
    public void keyIsNotStringInDictionary() {
        assertNull(getExpressions("d i34e i34e e"));
    }

    @Test
    public void duplicateKeyInDictionary() {
        assertNull(getExpressions("d 2:er i34e 2:er i55e e"));
    }

    @Test
    public void expectedValueAfterKeyInDictionary() {
        assertNull(getExpressions("d 3:qwe e"));
    }

    @Test
    public void noValueAndEndTypeInDictionary() {
        assertNull(getExpressions("d 3:qwe"));
    }
}
