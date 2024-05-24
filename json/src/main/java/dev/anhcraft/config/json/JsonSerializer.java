package dev.anhcraft.config.json;

import dev.anhcraft.config.Dictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;

public class JsonSerializer {
  private int currentIndent = 0;
  private final Writer writer;
  private final Options options;

  public JsonSerializer(@NotNull Writer writer, @NotNull Options options) {
    this.writer = writer;
    this.options = options;
  }

  private void startBlock() throws IOException {
    if (options.pretty) {
      writer.append('\n');
      writer.append(" ".repeat(currentIndent));
    }
  }

  public void serialize(@Nullable Object obj) throws IOException {
    if (obj instanceof Dictionary) {
      serializeSection((Dictionary) obj);
    } else if (obj != null && obj.getClass().isArray()) {
      serializeArray(obj);
    } else if (obj instanceof String) {
      appendEscape(obj.toString());
    } else if (obj != null) {
      writer.append(obj.toString());
    }
  }

  public void serializeSection(@NotNull Dictionary dict) throws IOException {
    writer.append('{');
    currentIndent += 2;
    int i = 0;
    for (String key : dict.keySet()) {
      startBlock();
      appendEscape(key);
      writer.append(options.pretty ? ": " : ":");
      serialize(dict.get(key));
      if (i++ < dict.size() - 1 || options.trailingCommas)
        writer.append(',');
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

  public void serializeArray(@NotNull Object obj) throws IOException {
    writer.append('[');
    currentIndent += 2;
    int n = Array.getLength(obj);
    for (int i = 0; i < n; i++) {
      startBlock();
      serialize(Array.get(obj, i));
      if (i < n - 1 || options.trailingCommas)
        writer.append(',');
    }
    currentIndent -= 2;
    startBlock();
    writer.append(']');
  }
  
  public static class Options {
    public static final Options DEFAULT = new Options(false, false, 2);

    private final boolean pretty;
    private final boolean trailingCommas;
    private final int indent;

    public Options(boolean pretty, boolean trailingCommas, int indent) {
      this.pretty = pretty;
      this.trailingCommas = trailingCommas;
      this.indent = Math.max(indent, 0);
    }

    public boolean isPretty() {
      return pretty;
    }

    public boolean withTrailingCommas() {
      return trailingCommas;
    }

    public int getIndent() {
      return indent;
    }
  }
}
