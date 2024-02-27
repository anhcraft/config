package dev.anhcraft.config.validate.check;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NotBlankValidationTest {
    private final NotBlankValidation validation = new NotBlankValidation();

    @Test
    public void testCheck() {
        Assertions.assertTrue(validation.check("foo"));
        Assertions.assertTrue(validation.check("   bar   "));
        Assertions.assertTrue(validation.check("a à á e ẻ ẽ ù ú ỷ ì í"));
        Assertions.assertTrue(validation.check("万事起头难"));
        Assertions.assertFalse(validation.check(""));
        Assertions.assertFalse(validation.check(" "));
        Assertions.assertFalse(validation.check("   "));
        Assertions.assertTrue(validation.check(0.5f));
        Assertions.assertTrue(validation.check(1));
        Assertions.assertTrue(validation.check(true));
        Assertions.assertTrue(validation.check(new Object()));
    }
}
