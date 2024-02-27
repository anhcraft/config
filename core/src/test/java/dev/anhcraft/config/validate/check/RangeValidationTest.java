package dev.anhcraft.config.validate.check;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.anhcraft.config.error.ValidationParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RangeValidationTest {
  @Test
  public void testValidInstantiation() {
    Assertions.assertDoesNotThrow(() -> new RangeValidation("0"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("0|"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("|0"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("0|0"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("0.0005"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("0.0005|"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("|0.0005"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("0.0001|0.0005"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("-0.0005"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("-0.0005|"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("|-0.0005"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("-0.0002|-0.0001"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("-.0001"));
    Assertions.assertDoesNotThrow(() -> new RangeValidation("0.000001|0.000001"));
  }

  @Test
  public void testInvalidInstantiation() {
    Assertions.assertThrows(ValidationParseException.class, () -> new RangeValidation("one"));
    Assertions.assertThrows(ValidationParseException.class, () -> new RangeValidation("NaN"));
    Assertions.assertThrows(ValidationParseException.class, () -> new RangeValidation("NaN|0"));
    Assertions.assertThrows(ValidationParseException.class, () -> new RangeValidation("Infinity"));
    Assertions.assertThrows(ValidationParseException.class, () -> new RangeValidation("-Infinity"));
    Assertions.assertThrows(ValidationParseException.class, () -> new RangeValidation("+Infinity"));
    Assertions.assertThrows(
        ValidationParseException.class, () -> new RangeValidation("0|Infinity"));
    Assertions.assertThrows(ValidationParseException.class, () -> new RangeValidation("3|2"));
    Assertions.assertThrows(ValidationParseException.class, () -> new RangeValidation("3|2.99999"));
    Assertions.assertThrows(
        ValidationParseException.class, () -> new RangeValidation("0.0001|-0.0001"));
  }

  @Test
  public void testCheck() {
    assertTrue(new RangeValidation("0.0001|0.0001").check(0.0001));
    assertFalse(new RangeValidation("0.0001|0.0001").check(0.000101));
    assertTrue(new RangeValidation("0.0001").check(0.0001));
    assertFalse(new RangeValidation("0.0001").check(0.00012));
    assertTrue(new RangeValidation("0.0001|").check(0.0001));
    assertTrue(new RangeValidation("0.0001|").check(0.00012));
    assertTrue(new RangeValidation("|0.0001").check(0.0001));
    assertTrue(new RangeValidation("|0.0001").check(0.00009));
    assertTrue(new RangeValidation("-0.0001|").check(-0.0001));
    assertTrue(new RangeValidation("-0.0001|").check(-0.00009));
    assertTrue(new RangeValidation("|-0.0001").check(-0.0001));
    assertTrue(new RangeValidation("|-0.0001").check(-0.0002));
    assertFalse(new RangeValidation("|-0.0001").check(-0.00009));
  }
}
