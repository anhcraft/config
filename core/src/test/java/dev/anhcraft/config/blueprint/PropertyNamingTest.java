package dev.anhcraft.config.blueprint;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyNamingTest {

  @Test
  public void testConstructorAndAccessors() {
    LinkedHashSet<String> aliases = new LinkedHashSet<>();
    aliases.add("alias1");
    aliases.add("alias2");

    PropertyNaming propertyNaming = new PropertyNaming("primaryName", aliases);

    assertEquals("primaryName", propertyNaming.primary());
    assertEquals(aliases, propertyNaming.aliases());
  }

  @Test
  public void testConstructorWithEmptyPrimary() {
    LinkedHashSet<String> aliases = new LinkedHashSet<>();
    aliases.add("alias1");

    Executable executable = () -> new PropertyNaming("", aliases);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
    assertEquals("Primary name cannot be empty", exception.getMessage());
  }

  @Test
  public void testConstructorWithEmptyAlias() {
    LinkedHashSet<String> aliases = new LinkedHashSet<>();
    aliases.add("");

    Executable executable = () -> new PropertyNaming("primaryName", aliases);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
    assertEquals("Alias cannot be empty", exception.getMessage());
  }

  @Test
  public void testConstructorWithAliasEqualToPrimary() {
    LinkedHashSet<String> aliases = new LinkedHashSet<>();
    aliases.add("primaryName");

    Executable executable = () -> new PropertyNaming("primaryName", aliases);
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
    assertEquals("Alias must be different from primary name", exception.getMessage());
  }

  @Test
  public void testEquals() {
    LinkedHashSet<String> aliases1 = new LinkedHashSet<>();
    aliases1.add("alias1");
    aliases1.add("alias2");

    LinkedHashSet<String> aliases2 = new LinkedHashSet<>();
    aliases2.add("alias1");
    aliases2.add("alias2");

    PropertyNaming propertyNaming1 = new PropertyNaming("primaryName", aliases1);
    PropertyNaming propertyNaming2 = new PropertyNaming("primaryName", aliases2);

    assertEquals(propertyNaming1, propertyNaming2);
    assertEquals(propertyNaming1.hashCode(), propertyNaming2.hashCode());
  }

  @Test
  public void testNotEqualsDifferentPrimary() {
    LinkedHashSet<String> aliases = new LinkedHashSet<>();
    aliases.add("alias1");
    aliases.add("alias2");

    PropertyNaming propertyNaming1 = new PropertyNaming("primaryName1", aliases);
    PropertyNaming propertyNaming2 = new PropertyNaming("primaryName2", aliases);

    assertNotEquals(propertyNaming1, propertyNaming2);
  }

  @Test
  public void testNotEqualsDifferentAliases() {
    LinkedHashSet<String> aliases1 = new LinkedHashSet<>();
    aliases1.add("alias1");
    aliases1.add("alias2");

    LinkedHashSet<String> aliases2 = new LinkedHashSet<>();
    aliases2.add("alias3");
    aliases2.add("alias4");

    PropertyNaming propertyNaming1 = new PropertyNaming("primaryName", aliases1);
    PropertyNaming propertyNaming2 = new PropertyNaming("primaryName", aliases2);

    assertNotEquals(propertyNaming1, propertyNaming2);
  }

  @Test
  public void testHashCodeConsistency() {
    LinkedHashSet<String> aliases = new LinkedHashSet<>();
    aliases.add("alias1");
    aliases.add("alias2");

    PropertyNaming propertyNaming = new PropertyNaming("primaryName", aliases);

    int initialHashCode = propertyNaming.hashCode();
    assertEquals(initialHashCode, propertyNaming.hashCode());
  }

  @Test
  public void testHashCodeContract() {
    LinkedHashSet<String> aliases1 = new LinkedHashSet<>();
    aliases1.add("alias1");
    aliases1.add("alias2");

    LinkedHashSet<String> aliases2 = new LinkedHashSet<>();
    aliases2.add("alias1");
    aliases2.add("alias2");

    PropertyNaming propertyNaming1 = new PropertyNaming("primaryName", aliases1);
    PropertyNaming propertyNaming2 = new PropertyNaming("primaryName", aliases2);

    assertEquals(propertyNaming1.hashCode(), propertyNaming2.hashCode());
  }
}
