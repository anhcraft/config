package dev.anhcraft.config.json;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.SchemalessDictionary;
import dev.anhcraft.config.json.error.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A JSON parser compliant with JSON spec (RFC 8259) that natively supports Config functionalities.<br>
 * <b>Important Considerations:</b>
 * <ul>
 *   <li>Trailing comma is allowed</li>
 *   <li>{@code null} values are ignored</li>
 *   <li>{@code /} (slash) is not escaped</li>
 *   <li>Number is handled by {@link Double#parseDouble(String)} or {@link Integer#parseInt(String)}</li>
 *   <li>Invalid Unicode codepoint is parsed as string without error</li>
 *   <li>JSON Lines is unsupported</li>
 *   <li>Comment is unsupported</li>
 * </ul>
 */
public class JsonParser {
  private final Reader reader;
  private final StringBuilder buffer = new StringBuilder();
  private int currentChar;
  private int pos = -1;

  /**
   * Constructs a new {@code JsonParser} with the specified reader.
   *
   * @param reader the {@code Reader} from which JSON text will be read
   */
  public JsonParser(@NotNull Reader reader) {
    this.reader = reader;
  }

  // Utilities

  private boolean isWhitespace(int c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
  }

  private void readChar() throws IOException {
    if (currentChar == -1) return;
    pos++;
    currentChar = reader.read();
  }

  private void nextNonWhitespaceChar() throws IOException {
    do {
      readChar();
    } while (currentChar != -1 && isWhitespace(currentChar));
  }

  private void skipWhitespace() throws IOException {
    if (isWhitespace(currentChar)) nextNonWhitespaceChar();
  }

  private void expectCurrentChar(int expected) throws IOException {
    if (currentChar != expected) errorUnexpectedChar(expected);
  }

  private String errorMessage(String s) {
    return s + " at position " + pos;
  }

  private String formatChar(int codepoint) {
    if (Character.isValidCodePoint(codepoint))
      return "U+" + Integer.toHexString(codepoint) + " ('" + (char) codepoint + "')";
    return "U+" + Integer.toHexString(codepoint);
  }

  private void errorUnexpectedChar() throws IOException {
    throw new MalformedJsonException(
        errorMessage("Unexpected character: " + formatChar(currentChar)));
  }

  private void errorUnexpectedChar(int expected) throws IOException {
    throw new MalformedJsonException(
        errorMessage(
            "Unexpected character: "
                + formatChar(currentChar)
                + ", expected "
                + formatChar(expected)));
  }

  // Parse

  /**
   * Parses the given JSON text and returns the corresponding object.
   * @return the parsed object (JSON object, array, number, boolean) or {@code null}
   * @throws IOException if an I/O error occurs
   */
  public @Nullable Object parse() throws IOException {
    nextNonWhitespaceChar();
    Object value = readValue();
    skipWhitespace();
    expectCurrentChar(-1);
    return value;
  }

  enum State {
    VALUE,
    OBJECT_KEY,
    OBJECT_DELIMITER,
    END
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Object readValue() throws IOException {
    class NamedDictionary {
      final String name;
      final Dictionary value;

      NamedDictionary(String name, Dictionary value) {
        this.name = name;
        this.value = value;
      }
    }

    State state = State.VALUE;
    String key = null;
    Object value = null;
    Deque<Object> containers = new ArrayDeque<>();

    while (currentChar != -1) {
      String newKey = key;
      State newState = state;
      Object newValue = null;

      switch (currentChar) {
          // Object + String
        case '{':
          if (state != State.VALUE) errorUnexpectedChar();
          newState = State.OBJECT_KEY;
          containers.push(new NamedDictionary(key, new SchemalessDictionary()));
          nextNonWhitespaceChar();
          break;
        case '}':
          if (state != State.OBJECT_KEY && state != State.VALUE) errorUnexpectedChar();
          NamedDictionary d = (NamedDictionary) containers.pop();
          if (key != null && value != null) d.value.put(key, value);
          newKey = d.name;
          newValue = d.value;
          newState = containers.isEmpty() ? State.END : State.VALUE;
          nextNonWhitespaceChar();
          break;
        case ':':
          if (state != State.OBJECT_DELIMITER) errorUnexpectedChar();
          newState = State.VALUE;
          nextNonWhitespaceChar();
          break;

          // Object & string
        case '"':
          if (state != State.VALUE && state != State.OBJECT_KEY) errorUnexpectedChar();
          if (state == State.OBJECT_KEY) {
            newKey = readString();
            newState = State.OBJECT_DELIMITER;
          } else {
            newValue = readString();
          }
          break;

          // Array
        case '[':
          if (state != State.VALUE) errorUnexpectedChar();
          containers.push(new ArrayList<>());
          nextNonWhitespaceChar();
          break;
        case ']':
          if (state != State.VALUE) errorUnexpectedChar();
          if (value != null) ((ArrayList) containers.getFirst()).add(value);
          newValue = ((ArrayList) containers.pop()).toArray(Object[]::new);
          newState = containers.isEmpty() ? State.END : State.VALUE;
          nextNonWhitespaceChar();
          break;

          // Object & Array
        case ',':
          if (state != State.VALUE) errorUnexpectedChar();
          if (value == null) errorUnexpectedChar();
          if (!containers.isEmpty()) {
            Object container = containers.getFirst();
            if (container instanceof NamedDictionary) {
              if (key == null) errorUnexpectedChar();
              ((NamedDictionary) containers.getFirst()).value.put(key, value);
              newKey = null;
              newState = State.OBJECT_KEY;
            } else {
              ((ArrayList) containers.getFirst()).add(value);
            }
          }
          nextNonWhitespaceChar();
          break;

          // Values
        case 't':
          if (state != State.VALUE) errorUnexpectedChar();
          newValue = readLiteral("true", true);
          break;
        case 'f':
          if (state != State.VALUE) errorUnexpectedChar();
          newValue = readLiteral("false", false);
          break;
        case 'n':
          if (state != State.VALUE) errorUnexpectedChar();
          readLiteral("null", null);
          break;
        default:
          if (state != State.VALUE) errorUnexpectedChar();
          newValue = readNumber();
      }

      key = newKey;
      value = newValue;
      state = newState;
    }

    if (!containers.isEmpty()) errorUnexpectedChar();

    return value;
  }

  private int hexToDecimal(char[] hexChars) throws IOException {
    int result = 0;
    for (char hexChar : hexChars) {
      int value = Character.digit(hexChar, 16);
      if (value == -1) {
        throw new MalformedJsonException(errorMessage("Invalid hexadecimal character: " + hexChar));
      }
      result = (result << 4) + value;
    }
    return result;
  }

  private String readString() throws IOException {
    expectCurrentChar('"');
    buffer.setLength(0);
    boolean escaped = false;
    byte unicode = -1;
    char[] unicodeBuffer = new char[4];

    do {
      readChar();

      if (currentChar < ' ' || currentChar > 1114111)
        throw new MalformedJsonException(
            errorMessage("Illegal Unicode codepoint: U+" + Integer.toHexString(currentChar)));

      if (unicode != -1) {
        if (currentChar >= '0' && currentChar <= '9'
            || currentChar >= 'a' && currentChar <= 'f'
            || currentChar >= 'A' && currentChar <= 'F') {
          unicodeBuffer[unicode++] = (char) currentChar;
          if (unicode == 4) {
            int codepoint = hexToDecimal(unicodeBuffer);
            if (Character.isValidCodePoint(codepoint)) {
              buffer.appendCodePoint(codepoint);
              unicode = -1;
              continue;
            }
          } else continue;
        }

        buffer.append('\\').append('u').append(unicodeBuffer, 0, unicode + 1);
        unicode = -1;
        continue;
      }

      if (escaped) {
        switch (currentChar) {
          case 'n':
            buffer.append('\n');
            break;
          case 'r':
            buffer.append('\r');
            break;
          case 't':
            buffer.append('\t');
            break;
          case 'b':
            buffer.append('\b');
            break;
          case 'f':
            buffer.append('\f');
            break;
          case 'u':
            unicode = 0;
            break;
          case '\\':
            buffer.append('\\');
            break;
          case '\"':
            buffer.append('\"');
            break;
          default:
            buffer.append('\\').append((char) currentChar);
            break;
        }
        escaped = false;
      } else {
        if (currentChar == '\\') {
          escaped = true;
        } else if (currentChar == '"') {
          break;
        } else {
          buffer.append((char) currentChar);
        }
      }
    } while (currentChar != -1);

    expectCurrentChar('"');
    nextNonWhitespaceChar();

    return buffer.toString();
  }

  private <T> T readLiteral(String match, T value) throws IOException {
    int i = 0;

    do {
      if (currentChar == match.charAt(i++)) {
        if (i == match.length()) {
          nextNonWhitespaceChar();
          return value;
        }
      } else {
        errorUnexpectedChar();
      }

      readChar();
    } while (currentChar != -1);

    throw new MalformedJsonException(
        errorMessage("End of input reached while reading literal: " + match));
  }

  private Number readNumber() throws IOException {
    buffer.setLength(0);
    boolean doubleNum = false;

    do {
      if (currentChar == '.' || currentChar == 'e' || currentChar == 'E') doubleNum = true;
      if (currentChar == '.'
          || currentChar == '+'
          || currentChar == '-'
          || currentChar == 'e'
          || currentChar == 'E'
          || (currentChar >= '0' && currentChar <= '9')) {
        buffer.append((char) currentChar);
      } else {
        skipWhitespace();
        break;
      }
      readChar();
    } while (currentChar != -1);

    try {
      Number v;
      if (doubleNum) v = Double.parseDouble(buffer.toString());
      else v = Integer.parseInt(buffer.toString());
      return v;
    } catch (NumberFormatException e) {
      throw new MalformedJsonException(errorMessage("Invalid number: " + buffer), e);
    }
  }
}
