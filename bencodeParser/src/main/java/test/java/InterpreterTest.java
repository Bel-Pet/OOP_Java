import error.Reporter;
import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Expr;
import parser.Parser;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {

    private String getJasonText(String text) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        Reporter reporter = new Reporter(10);

        List<Expr> expressions = Parser.parse(Lexer.scan(br, reporter), reporter);

        return expressions == null ? null : Interpreter.interpret(expressions);
    }

    @Test
    public void empty() {
        assertNull(getJasonText(""));
    }

    @Test
    public void oneString() {
        assertEquals("\"qwerty\"", getJasonText("6:qwerty"));
    }

    @Test
    public void oneNumber() {
        assertEquals("456", getJasonText("i456e"));
    }

    @Test
    public void emptyDictionary() {
        String output = """
                {
                
                }""";
        assertEquals(output, getJasonText("de"));
    }

    @Test
    public void oneElementDictionary() {
        String output = """
                {
                 adas: 6
                }""";
        assertEquals(output, getJasonText("d 4:adas i6e e"));
    }

    @Test
    public void dictionaryInDictionary() {
        String output = """
                {
                 qwe:\s
                 {
                  adas: 6
                 }
                }""";
        assertEquals(output, getJasonText("d 3:qwe d 4:adas i6e e e"));
    }

    @Test
    public void listInDictionary() {
        String output = """
                {
                 qwe: [456]
                }""";
        assertEquals(output, getJasonText("d 3:qwe l i456e e e"));
    }

    @Test
    public void emptyList() {
        assertEquals("[]", getJasonText("le"));
    }

    @Test
    public void oneElementList() {
        assertEquals("[543]", getJasonText("l i543e e"));
    }

    @Test
    public void listInList() {
        assertEquals("[[543]]", getJasonText("l l i543e e e"));
    }

    @Test
    public void dictionaryInList() {
        String output = """
                [{
                 rty: 456
                }]""";
        assertEquals(output, getJasonText("l d 3:rty i456e e e"));
    }

    @Test
    public void complexData() {
        String input = """
                d
                3:bsd i5e
                4:adas i6e
                3:vfd l 4:dfgg 3:bgf i45e e
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
                e
                """;

        String output = """
                {
                 adas: 6,
                 bsd: 5,
                 fgh: [[435, "qwert",\s
                 {
                  gt: 12,
                  vfrd: "rt"
                 }, 78]],
                 kjftgy:\s
                 {
                  kds: 345
                 },
                 vfd: ["dfgg", "bgf", 45]
                }""";

        assertEquals(output, getJasonText(input));
    }
}
