import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.json.JsonParser;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.UnicodeUnescaper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class JsonParserTest {
  private static Object parse(String str) throws IOException {
    return new JsonParser(new StringReader(str)).parse();
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/strings.csv")
  public void testParseString(String str) throws Exception {
    var unescapedUnicode = new UnicodeUnescaper().translate(str);
    assertEquals(
        unescapedUnicode, parse("\"" + StringEscapeUtils.escapeJava(unescapedUnicode) + "\""));
  }

  @Test
  public void testParseIllegalString() {
    assertThrows(IOException.class, () -> parse("\""));
    assertThrows(IOException.class, () -> parse("\"\"\""));
    assertThrows(IOException.class, () -> parse("\"\n\""));
    assertThrows(IOException.class, () -> parse("\"\b\""));
    assertThrows(IOException.class, () -> parse("\"\t\""));
    assertThrows(IOException.class, () -> parse("\"\r\""));
    assertThrows(IOException.class, () -> parse("\"\f\""));
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/numbers.csv")
  public void testParseNumber(String str) throws Exception {
    assertEquals(Double.parseDouble(str), ((Number) parse(str)).doubleValue(), 1e-8);
  }

  @Test
  public void testParseBoolean() throws Exception {
    assertEquals(true, parse("true"));
    assertEquals(false, parse("false"));
  }

  @Test
  public void testParseIntArray() throws Exception {
    assertArrayEquals(new Object[] {}, (Object[]) parse("[]"));
    assertArrayEquals(new Object[] {1}, (Object[]) parse("[1]"));
    assertArrayEquals(new Object[] {1, 2}, (Object[]) parse("[1,2]"));
    assertArrayEquals(new Object[] {1, 2}, (Object[]) parse("[1,2,]"));
    assertArrayEquals(new Object[] {1, 2}, (Object[]) parse("[1,\n2,\n]"));
    assertArrayEquals(new Object[] {1, 2}, (Object[]) parse("[1,\n2\n\n\n,]"));
  }

  @Test
  public void testParseIllegalIntArray() {
    assertThrows(IOException.class, () -> parse("[,]"));
    assertThrows(IOException.class, () -> parse("[,,]"));
    assertThrows(IOException.class, () -> parse("[1,2,,]"));
    assertThrows(IOException.class, () -> parse("[,   ]"));
    assertThrows(IOException.class, () -> parse("[  , ,]"));
    assertThrows(IOException.class, () -> parse("[    1,2  , , ]"));
    assertThrows(IOException.class, () -> parse("[\n,\n\n]"));
    assertThrows(IOException.class, () -> parse("[ \n,,\n]"));
    assertThrows(IOException.class, () -> parse("[  \n1,2\n  , \n, ]"));
  }

  @Test
  public void testParseBooleanArray() throws Exception {
    assertArrayEquals(new Object[] {true}, (Object[]) parse("[true]"));
    assertArrayEquals(new Object[] {true, false}, (Object[]) parse("[true,false]"));
    assertArrayEquals(new Object[] {true, false}, (Object[]) parse("[true,false,]"));
    assertArrayEquals(new Object[] {true, false}, (Object[]) parse("[true,\nfalse,\n]"));
    assertArrayEquals(new Object[] {true, false}, (Object[]) parse("[true,\nfalse\n\n\n,]"));
  }

  @Test
  public void testParseIllegalBooleanArray() {
    assertThrows(IOException.class, () -> parse("[true,,false]"));
    assertThrows(IOException.class, () -> parse("[    true,false  , , ]"));
    assertThrows(IOException.class, () -> parse("[  \ntrue,false\n  , \n, ]"));
  }

  @Test
  public void testParseStringArray() throws Exception {
    assertArrayEquals(new Object[] {"a"}, (Object[]) parse("[\"a\"]"));
    assertArrayEquals(new Object[] {"a", "b"}, (Object[]) parse("[\"a\",\"b\"]"));
    assertArrayEquals(new Object[] {"a", "b"}, (Object[]) parse("[\"a\",\"b\",]"));
    assertArrayEquals(new Object[] {"a", "b"}, (Object[]) parse("[\"a\",\n\"b\",\n]"));
    assertArrayEquals(new Object[] {"a", "b"}, (Object[]) parse("[\"a\",\n\"b\"\n\n\n,]"));
  }

  @Test
  public void testParseIllegalStringArray() {
    assertThrows(IOException.class, () -> parse("[\"a\",,\"b\"]"));
    assertThrows(IOException.class, () -> parse("[    \"a\",\"b\"  , , ]"));
    assertThrows(IOException.class, () -> parse("[  \n\"a\",\"b\"\n  , \n, ]"));
  }

  @Test
  public void testParseNestedArray() throws Exception {
    assertArrayEquals(new Object[] {new Object[] {1}}, (Object[]) parse("[[1]]"));
    assertArrayEquals(new Object[] {new Object[] {1}}, (Object[]) parse("[[1],]"));
    assertArrayEquals(new Object[] {new Object[] {1}}, (Object[]) parse("[[1,],]"));
    assertArrayEquals(
        new Object[] {new Object[] {1, 2}, new Object[] {3, 4}}, (Object[]) parse("[[1,2],[3,4]]"));
    assertArrayEquals(
        new Object[] {new Object[] {1, 2}, new Object[] {3, 4}},
        (Object[]) parse("[[1,2],[3,4],]"));
    assertArrayEquals(
        new Object[] {new Object[] {1, 2}, new Object[] {3, 4}},
        (Object[]) parse("[[1,2],\n[3,4],\n]"));
    assertArrayEquals(
        new Object[] {new Object[] {1, 2}, new Object[] {3, 4}},
        (Object[]) parse("[[1,2],\n[3,4]\n\n\n,]"));
  }

  @Test
  public void testParseIllegalNestedArray() {
    assertThrows(IOException.class, () -> parse("[[1,2],,[3,4]]"));
    assertThrows(IOException.class, () -> parse("[    [1,2],[3,4]  , , ]"));
    assertThrows(IOException.class, () -> parse("[  \n[1,2],[3,4]\n  , \n, ]"));
  }

  @Test
  public void testParseDeeplyNestedArrays() {
    int times = 10_000;
    assertDoesNotThrow(() -> parse("[".repeat(times) + "]".repeat(times)));
  }

  @Test
  public void testParseValidObject() throws Exception {
    assertEquals(Map.of(), parse("{}"));
    assertEquals(Map.of("key", 1), parse("{\"key\": 1}"));
    assertEquals(Map.of("key1", 1, "key2", 2), parse("{\"key1\": 1, \"key2\": 2}"));
    assertEquals(Map.of("key1", 1, "key2", 2), parse("{\"key1\": 1, \"key2\": 2,}"));
    assertEquals(
        Map.of("nested", Map.of("key", "value")), parse("{\"nested\": {\"key\": \"value\"}}"));
    assertEquals(
        Map.of("nested", Map.of("key", "value")), parse("{\"nested\": {\"key\": \"value\"},}"));
  }

  @Test
  public void testParseInvalidObject() {
    assertThrows(IOException.class, () -> parse("{}{}"));
    assertThrows(IOException.class, () -> parse("{"));
    assertThrows(IOException.class, () -> parse("{}}"));
    assertThrows(IOException.class, () -> parse("{key: 1}"));
    assertThrows(IOException.class, () -> parse("{\"key\" 1}"));
    assertThrows(IOException.class, () -> parse("{\"key\": 1 \"key2\": 2}"));
    assertThrows(IOException.class, () -> parse("{\"key\": 1, \"key2\"}"));
    assertThrows(
        IOException.class, () -> parse("{\"key\": {\"nestedKey\": 1, \"nestedKey2\": { }"));
  }

  @Test
  public void testParseValidNestedObject() throws Exception {
    assertEquals(Map.of("a", Map.of("b", Map.of("c", 1))), parse("{\"a\": {\"b\": {\"c\": 1}}}"));
    assertEquals(Map.of("a", Map.of("b", 2), "c", 3), parse("{\"a\": {\"b\": 2}, \"c\": 3}"));
    assertEquals(
        Map.of("a", Map.of("b", Map.of("c", 1, "d", 2))),
        parse("{\"a\": {\"b\": {\"c\": 1, \"d\": 2}}}"));
  }

  @Test
  public void testParseDeeplyNestedObjects() {
    int times = 10_000;
    assertDoesNotThrow(() -> parse("{\"a\":".repeat(times) + "}".repeat(times)));
  }
}
