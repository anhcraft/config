package dev.anhcraft.config.json;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.type.ComplexTypes;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A JSON writer compliant with JSON spec (RFC 8259) that natively supports Config functionalities.<br>
 * <b>Important Considerations:</b>
 * <ul>
 *   <li>{@code null} values are not written</li>
 *   <li>{@code /} (slash) is not escaped</li>
 *   <li>Number is handled by {@link Number#toString()}</li>
 * </ul>
 * @see JsonWriterOptions
 */
public class JsonWriter {
  private int currentIndent = 0;
  private final Writer writer;
  private final JsonWriterOptions options;

  /**
   * Constructs a new {@code JsonWriter} with the specified writer and options.
   *
   * @param writer the {@code Writer} to which JSON text will be written
   * @param options the {@link JsonWriterOptions} specifying formatting options
   */
  public JsonWriter(@NotNull Writer writer, @NotNull JsonWriterOptions options) {
    this.writer = writer;
    this.options = options;
  }

  /**
   * Constructs a new {@code JsonWriter} with the specified writer and default options.
   *
   * @param writer the {@code Writer} to which JSON text will be written
   */
  public JsonWriter(@NotNull Writer writer) {
    this(writer, JsonWriterOptions.DEFAULT);
  }

  /**
   * Serializes the given object into a JSON string.
   * @param obj the object
   * @throws IOException if an I/O error occurs
   */
  public void serialize(@Nullable Object obj) throws IOException {
    if (obj instanceof Dictionary) {
      serializeSection((Dictionary) obj);
    } else if (ComplexTypes.isArray(obj)) {
      serializeArray(obj);
    } else if (obj instanceof String) {
      appendEscape(obj.toString());
    } else if (obj != null) {
      writer.append(obj.toString());
    }
  }

  private void startBlock() throws IOException {
    if (options.isPretty()) {
      writer.append('\n');
      writer.append(" ".repeat(currentIndent));
    }
  }

  private void serializeSection(@NotNull Dictionary dict) throws IOException {
    writer.append('{');
    currentIndent += 2;
    int i = 0;
    for (String key : dict.keySet()) {
      startBlock();
      appendEscape(key);
      writer.append(options.isPretty() ? ": " : ":");
      serialize(dict.get(key));
      if (i++ < dict.size() - 1 || options.shouldAppendTrailingCommas()) writer.append(',');
    }
    currentIndent -= 2;
    startBlock();
    writer.append('}');
  }

  private void appendEscape(@NotNull String s) throws IOException {
    writer.append('\"');

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '\\':
        case '\"':
          writer.append('\\');
          writer.append(c);
          break;
        case '\n':
          writer.append('\\');
          writer.append('\n');
          break;
        case '\r':
          writer.append('\\');
          writer.append('\r');
          break;
        case '\t':
          writer.append('\\');
          writer.append('\t');
          break;
        case '\b':
          writer.append('\\');
          writer.append('\b');
          break;
        case '\f':
          writer.append('\\');
          writer.append('\f');
          break;
        default:
          writer.append(c);
          break;
      }
    }

    writer.append('\"');
  }

  private void serializeArray(@NotNull Object obj) throws IOException {
    writer.append('[');
    currentIndent += 2;
    int n = Array.getLength(obj);
    for (int i = 0; i < n; i++) {
      startBlock();
      serialize(Array.get(obj, i));
      if (i < n - 1 || options.shouldAppendTrailingCommas()) writer.append(',');
    }
    currentIndent -= 2;
    startBlock();
    writer.append(']');
  }
}
