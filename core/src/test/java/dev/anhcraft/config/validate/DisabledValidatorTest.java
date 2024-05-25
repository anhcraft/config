package dev.anhcraft.config.validate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DisabledValidatorTest {

  @Test
  public void testCheck() {
    DisabledValidator validator = DisabledValidator.INSTANCE;
    assertTrue(validator.check(null));
    assertTrue(validator.check(""));
    assertTrue(validator.check("Test"));
  }

  @Test
  public void testMessage() {
    DisabledValidator validator = DisabledValidator.INSTANCE;
    assertEquals("", validator.message());
  }

  @Test
  public void testValidations() {
    DisabledValidator validator = DisabledValidator.INSTANCE;
    assertTrue(validator.validations().isEmpty());
  }

  @Test
  public void testSilent() {
    DisabledValidator validator = DisabledValidator.INSTANCE;
    assertTrue(validator.silent());
  }
}
