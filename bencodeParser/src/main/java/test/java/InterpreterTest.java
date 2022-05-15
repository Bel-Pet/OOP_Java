import error.Reporter;
import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Expr;
import parser.Parser;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
1. string
2. integer
3. dict empty
4. dict one element - primitive
5. list empty
6. list one element - primitive
7. dict in dict
8. list in dict
9. list in list
10. dict in list
 */

class InterpreterTest {

    private String getJsonText(String text, int maxMessages) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        Reporter reporter = new Reporter(maxMessages);
        List<Expr> expressions = Parser.parse(Lexer.scan(br, reporter), reporter);
        return expressions == null ? null : Interpreter.interpret(expressions);
    }

    @Test
    public void expressionsIsNull() {
        assertNull(getJsonText("", 5));
    }

    @Test
    public void noAsciiCharInString() {
        String res = getJsonText("4:e\u0660\u06F0i", 4);
        assertNotNull(res);
        assertEquals("\"ei\"", res);
    }

    @Test
    public void minorErrors() {
        String input = """
                i4x5e
                d t
                4:er\u06F0t i34e
                2:rr l i45e i66e e
                2:gg d
                3:vfr i76es
                e
                e
                i55e
                k
                """;
        String output = """
                45
                {
                 rr: [66],
                 gg:\s
                 {
                  vfr: 76
                 },
                 ert: 34
                }
                55""";
        String res = getJsonText(input, 7);
        assertNotNull(res);
        assertEquals(output, res);
    }

    @Test
    public void correctData() {
        String input = """
                i45e
                d
                3:ert i34e
                2:rr l i45e i66e e
                2:gg d
                3:vfr i76e
                e
                e
                i55e
                """;
        String output = """
                45
                {
                 rr: [66],
                 gg:\s
                 {
                  vfr: 76
                 },
                 ert: 34
                }
                55""";
        String res = getJsonText(input, 7);
        assertNotNull(res);
        assertEquals(output, res);
    }
}