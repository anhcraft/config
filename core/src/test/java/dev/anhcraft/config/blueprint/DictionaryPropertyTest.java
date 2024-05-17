package dev.anhcraft.config.blueprint;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.ConstrainedDictionary;
import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.SchemalessDictionary;
import dev.anhcraft.config.validate.DisabledValidator;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DictionaryPropertyTest {
  private static PropertyNaming naming;

  @BeforeAll
  public static void setUp() {
    naming = new PropertyNaming("foo", new LinkedHashSet<>());
  }

  @Test
  public void testDescribeType() {
    assertEquals("", DictionaryProperty.create().withNames("foo").build().describeType(false));
    assertEquals(
        "int",
        DictionaryProperty.create()
            .withNames("foo")
            .withType(int.class)
            .build()
            .describeType(false));
    assertEquals(
        "int[]",
        DictionaryProperty.create()
            .withNames("foo")
            .withType(int[].class)
            .build()
            .describeType(false));
    assertEquals(
        "int[][]",
        DictionaryProperty.create()
            .withNames("foo")
            .withType(int[][].class)
            .build()
            .describeType(false));
    assertEquals(
        "java.lang.Integer",
        DictionaryProperty.create()
            .withNames("foo")
            .withType(Integer.class)
            .build()
            .describeType(false));
  }

  @ParameterizedTest
  @ValueSource(classes = {ArrayList.class, Map.class})
  public void testIllegalTypeDetect(Class<?> type) {
    assertThrows(
        IllegalArgumentException.class,
        () -> DictionaryProperty.create().withNames("foo").withType(type).build());
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        String.class, int.class, Integer.class, double.class, Double.class, boolean.class,
            Boolean.class,
        String[].class, int[].class, Integer[].class, double[].class, Double[].class,
            boolean[].class, Boolean[].class,
        String[][].class, int[][].class, Integer[][].class, double[][].class, Double[][].class,
            boolean[][].class, Boolean[][].class,
      })
  public void testValidTypeDetect(Class<?> type) {
    assertDoesNotThrow(() -> DictionaryProperty.create().withNames("foo").withType(type).build());
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        ArrayList.class,
        Map.class,
        String.class,
        int.class,
        Integer.class,
        double.class,
        Double.class,
        boolean.class,
        Boolean.class,
        String[].class,
        int[].class,
        Integer[].class,
        double[].class,
        Double[].class,
        boolean[].class,
        Boolean[].class
      })
  public void testSchemaWithIllegalType(Class<?> type) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            DictionaryProperty.create()
                .withNames("foo")
                .withType(type)
                .withSchema(new DictionarySchema(List.of(), Map.of(), null, null))
                .build());
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        Dictionary.class, SchemalessDictionary.class, ConstrainedDictionary.class,
        Dictionary[].class, SchemalessDictionary[].class, ConstrainedDictionary[].class
      })
  public void testSchemaWithValidType(Class<?> type) {
    assertDoesNotThrow(
        () ->
            DictionaryProperty.create()
                .withNames("foo")
                .withType(type)
                .withSchema(new DictionarySchema(List.of(), Map.of(), null, null))
                .build());
  }

  @Test
  public void testIsCompatible() {
    var foo =
        new DictionaryProperty(
            naming,
            List.of(),
            DisabledValidator.INSTANCE,
            Dictionary.class,
            DictionarySchema.create()
                .addProperty("name", p -> p.withType(String.class))
                .addProperty("level", p -> p.withType(double.class))
                .addProperty("health", p -> p.withType(double.class))
                .build());
    assertTrue(foo.isCompatible(null));
    assertFalse(foo.isCompatible(""));
    assertFalse(foo.isCompatible(true));
    assertTrue(foo.isCompatible(Dictionary.of(Map.of("name", "foo", "level", 1.0, "health", 2.0))));
    assertFalse(foo.isCompatible(Dictionary.of(Map.of("name", "foo", "level", 1, "health", 2.0))));
    assertFalse(foo.isCompatible(Dictionary.of(Map.of("name", "foo", "level", 1, "health", true))));
    assertFalse(foo.isCompatible(Dictionary.of(Map.of("name", 'a', "level", 1.0, "health", 2.0))));
    assertFalse(foo.isCompatible(Dictionary.of(Map.of("name", 'a', "level", 1.0, "health", 2.0f))));
    assertFalse(
        foo.isCompatible(
            new Dictionary[] {Dictionary.of(Map.of("name", "foo", "level", 1.0, "health", 2.0))}));
  }
}
