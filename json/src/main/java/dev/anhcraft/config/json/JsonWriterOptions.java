package dev.anhcraft.config.json;

import org.jetbrains.annotations.NotNull;

/**
 * Represents options in writing JSON.
 */
public class JsonWriterOptions {
  /**
   * The default options for JSON writing.
   */
  public static final JsonWriterOptions DEFAULT = create().build();

  private final boolean pretty;
  private final boolean appendTrailingCommas;
  private final int indent;

  /**
   * Constructs a {@code JsonWriterOptions} instance using the provided {@link Builder}.
   *
   * @param builder the builder used to create the instance
   * @throws IllegalArgumentException if the indent value is less than 0
   */
  public JsonWriterOptions(@NotNull Builder builder) {
    if (builder.indent < 0) throw new IllegalArgumentException("indent must be >= 0");
    this.pretty = builder.pretty;
    this.appendTrailingCommas = builder.trailingCommas;
    this.indent = builder.indent;
  }

  /**
   * Returns whether the JSON should be pretty-printed.
   *
   * @return {@code true} if the JSON should be pretty-printed, {@code false} otherwise
   */
  public boolean isPretty() {
    return pretty;
  }

  /**
   * Returns whether trailing commas should be appended.
   *
   * @return {@code true} if trailing commas should be appended
   */
  public boolean shouldAppendTrailingCommas() {
    return appendTrailingCommas;
  }

  /**
   * Returns the indentation level to be used for pretty-printing.
   *
   * @return the number of spaces to use for each indentation level
   */
  public int getIndent() {
    return indent;
  }

  /**
   * Creates a new {@link Builder} instance.
   *
   * @return a new {@code Builder} instance
   */
  public static @NotNull Builder create() {
    return new Builder();
  }

  /**
   * A builder class for {@link JsonWriterOptions}.
   */
  public static class Builder {
    private boolean pretty = true;
    private boolean trailingCommas;
    private int indent = 2;

    /**
     * Sets whether the JSON should be pretty-printed.
     * By default, sets to {@code true}.
     *
     * @param pretty {@code true} if the JSON should be pretty-printed, {@code false} otherwise
     * @return this {@code Builder} instance
     */
    public @NotNull Builder setPretty(boolean pretty) {
      this.pretty = pretty;
      return this;
    }

    /**
     * Sets whether trailing commas should be appended.
     * By default, sets to {@code false}.
     *
     * @param trailingCommas {@code true} if trailing commas should be appended
     * @return this {@code Builder} instance
     */
    public @NotNull Builder appendTrailingCommas(boolean trailingCommas) {
      this.trailingCommas = trailingCommas;
      return this;
    }

    /**
     * Sets the indentation level to be used for pretty-printing.
     * By default, sets to {@code 2}.
     *
     * @param indent the number of spaces to use for each indentation level
     * @return this {@code Builder} instance
     */
    public @NotNull Builder setIndent(int indent) {
      this.indent = indent;
      return this;
    }

    /**
     * Builds and returns a {@link JsonWriterOptions} instance.
     *
     * @return a new {@code JsonWriterOptions} instance
     */
    public @NotNull JsonWriterOptions build() {
      return new JsonWriterOptions(this);
    }
  }
}
