package dev.anhcraft.config.validate.check;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.error.ValidationParseException;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SizeValidationTest {
  @Test
  public void testValidInstantiation() {
    Assertions.assertDoesNotThrow(() -> new SizeValidation("0"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("0|"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("|0"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("0|0"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("-0"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("-0|"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("|-0"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("-0|-0"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("999999"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("999999|"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("|999999"));
    Assertions.assertDoesNotThrow(() -> new SizeValidation("999999|999999"));
  }

  @Test
  public void testInvalidInstantiation() {
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("one"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("NaN"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("NaN|0"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("Infinity"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("-Infinity"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("+Infinity"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("0|Infinity"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("3.2"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("3.2|"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("|3.2"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("0.0001"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("-1"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("-1|-1"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("|-1"));
    Assertions.assertThrows(ValidationParseException.class, () -> new SizeValidation("-1|"));
  }

  @Test
  public void testCheck() {
    assertTrue(new SizeValidation("0").check(-1));
    assertTrue(new SizeValidation("0").check(""));
    assertTrue(new SizeValidation("0").check(new int[] {}));
    assertTrue(new SizeValidation("0").check(new Dictionary()));
    assertTrue(new SizeValidation("0").check(new ArrayList<>()));
    assertTrue(new SizeValidation("|1").check(-1));
    assertTrue(new SizeValidation("|1").check(""));
    assertTrue(new SizeValidation("|1").check(new int[] {}));
    assertTrue(new SizeValidation("|1").check(new Dictionary()));
    assertTrue(new SizeValidation("|1").check(new ArrayList<>()));
    assertTrue(new SizeValidation("1").check(0));
    assertFalse(new SizeValidation("1").check(""));
    assertFalse(new SizeValidation("1").check(new int[] {}));
    assertFalse(new SizeValidation("1").check(new Dictionary()));
    assertFalse(new SizeValidation("1").check(new ArrayList<>()));
  }
}
