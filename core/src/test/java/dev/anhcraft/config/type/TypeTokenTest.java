package dev.anhcraft.config.type;

import static dev.anhcraft.config.type.ComplexTypes.describe;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.anhcraft.config.context.PathType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class TypeTokenTest {
  @Test
  public void testCapture() {
    assertEquals(
        "java.util.List<java.lang.String>", describe(new TypeToken<List<String>>() {}.capture()));
    assertEquals(
        "java.util.List<java.lang.String[]>",
        describe(new TypeToken<List<String[]>>() {}.capture()));
    assertEquals(
        "java.util.List<java.lang.String[]>[]",
        describe(new TypeToken<List<String[]>[]>() {}.capture()));
    assertEquals(
        "java.util.Map<java.util.Set<java.util.List<java.lang.String[]>>[][],java.util.List<java.util.Map<int[],dev.anhcraft.config.context.PathType>>[]>",
        describe(
            new TypeToken<
                Map<Set<List<String[]>>[][], List<Map<int[], PathType>>[]>>() {}.capture()));
  }
}
