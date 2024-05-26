package dev.anhcraft.config.json;

import dev.anhcraft.config.SchemalessDictionary;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class JsonParser {
  private final Reader reader;
  private int currentChar;
  private int pos;
  private final Deque<Object> values = new ArrayDeque<>();

  public JsonParser(@NotNull Reader reader) {
    this.reader = reader;
  }

  // Utilities

  private boolean isWhitespace(int c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
  }

  private void readChar() throws IOException {
    if (currentChar == -1)
      return;
    pos++;
    currentChar = reader.read();
  }

  private void nextNonWhitespaceChar() throws IOException {
    do {
      readChar();
    } while (currentChar != -1 && isWhitespace(currentChar));
  }

  private void skipWhitespace() throws IOException {
    if (currentChar == ' ')
      nextNonWhitespaceChar();
  }

  private void expectCurrentChar(int expected) throws IOException {
    if (currentChar != expected)
      errorUnexpectedChar(expected);
  }

  private void errorUnexpectedChar() throws IOException {
    if (!Character.isValidCodePoint(currentChar))
      throw new IOException(String.format(
        "Unexpected character: %d at position %d", currentChar, pos
      ));
    throw new IOException(String.format(
      "Unexpected character: %d ('%c') at position %d", currentChar, currentChar, pos
    ));
  }

  private void errorUnexpectedChar(int expected) throws IOException {
    if (!Character.isValidCodePoint(expected)) {
      if (!Character.isValidCodePoint(currentChar))
        throw new IOException(String.format(
          "Unexpected character: %d, expected: %d at position %d", currentChar, expected, pos
        ));
      throw new IOException(String.format(
        "Unexpected character: %d ('%c'), expected: %d at position %d", currentChar, currentChar, expected, pos
      ));
    }
    if (!Character.isValidCodePoint(currentChar))
      throw new IOException(String.format(
        "Unexpected character: %d, expected: %d ('%c') at position %d", currentChar, expected, expected, pos
      ));
    throw new IOException(String.format(
      "Unexpected character: %d ('%c'), expected: %d ('%c') at position %d", currentChar, currentChar, expected, expected, pos
    ));
  }

  // Parse

  public Object parse() throws IOException {
    nextNonWhitespaceChar();
    readValue();
    skipWhitespace();
    expectCurrentChar(-1);
    return values.pop();
  }

  private void readValue() throws IOException {
    switch (currentChar) {
      case '{':
        readObject();
        break;
      case '[':
        readArray();
        break;
      case '"':
        readString();
        break;
      case 't':
        readLiteral("true", true);
        break;
      case 'f':
        readLiteral("false", false);
        break;
      case 'n':
        readLiteral("null", null);
        break;
      default:
        readNumber();
    }
  }

  private void readObject() throws IOException {
    expectCurrentChar('{');
    SchemalessDictionary dict = new SchemalessDictionary();
    nextNonWhitespaceChar();
    outer:
    while (true) {
      switch (currentChar) {
        case '}':
          break outer;
        case '"':
          readString();
          String key = (String) values.pop();
          nextNonWhitespaceChar();
          expectCurrentChar(':');
          nextNonWhitespaceChar();
          readValue();
          Object val = values.pop();
          dict.put(key, val);
          skipWhitespace();
          if (currentChar == '}')
            break outer;
          if (currentChar != ',')
            errorUnexpectedChar(',');
          break;
        default:
          errorUnexpectedChar();
      }
      nextNonWhitespaceChar();
    }
    values.push(dict);
    nextNonWhitespaceChar();
  }

  private void readArray() throws IOException {
    expectCurrentChar('[');
    List<Object> list = new ArrayList<>();
    nextNonWhitespaceChar();
    while (true) {
      if (currentChar == ']') {
        break;
      } else {
        readValue();
        Object val = values.pop();
        list.add(val);
        skipWhitespace();
        if (currentChar == ']')
          break;
        if (currentChar != ',')
          errorUnexpectedChar(',');
        nextNonWhitespaceChar();
      }
    }
    values.push(list.toArray(Object[]::new));
    nextNonWhitespaceChar();
  }

  private void readString() throws IOException {
    expectCurrentChar('"');
    StringBuilder sb = new StringBuilder();
    boolean escaped = false;
    do {
      readChar();
      if (escaped) {
        switch (currentChar) {
          case 'n':
            sb.append('\n');
            break;
          case 'r':
            sb.append('\r');
            break;
          case 't':
            sb.append('\t');
            break;
          case 'b':
            sb.append('\b');
            break;
          case 'f':
            sb.append('\f');
            break;
          case '\\':
            sb.append('\\');
            break;
          case '\"':
            sb.append('\"');
            break;
          default:
            sb.append('\\').append(currentChar);
            break;
        }
        escaped = false;
      } else {
        if (currentChar == '\\') {
          escaped = true;
        } else if (currentChar == '"') {
          break;
        } else {
          sb.append(currentChar);
        }
      }
    } while (currentChar != -1);
    expectCurrentChar('"');
    values.push(sb.toString());
    nextNonWhitespaceChar();
  }

  private void readLiteral(String match, Object value) throws IOException {
    int i = 0;
    do {
      if (currentChar == match.charAt(i++)) {
        if (i == match.length()) {
          values.push(value);
          nextNonWhitespaceChar();
          return;
        }
      } else {
        errorUnexpectedChar();
      }
      readChar();
    } while (currentChar != -1);
    errorUnexpectedChar();
  }

  private void readNumber() throws IOException {
    StringBuilder sb = new StringBuilder();;
    do {
      if (currentChar == '.' || currentChar == '+' || currentChar == '-' || (currentChar >= '0' && currentChar <= '9')) {
        sb.append((char) currentChar);
      } else {
        break;
      }
      readChar();
    } while (currentChar != -1);
    values.push(Double.parseDouble(sb.toString()));
    //nextNonWhitespaceChar(); // number is special, see "while" loop above
  }
}
