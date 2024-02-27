package dev.anhcraft.config.validate;

import dev.anhcraft.config.error.ValidationParseException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static dev.anhcraft.config.validate.ValidationRegistry.DEFAULT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationRegistryTest {
    @Nested
    public class DefaultRegistry {
        @Test
        public void testNotNullValidation() {
            assertFalse(DEFAULT.parseString("not-null", true).check(null));
            assertFalse(DEFAULT.parseString("notNull  ", true).check(null));
            assertFalse(DEFAULT.parseString("  non-null", true).check(null));
            assertFalse(DEFAULT.parseString("nonNull  ", true).check(null));
            assertFalse(DEFAULT.parseString("not-null=", true).check(null));
            assertFalse(DEFAULT.parseString("notNull=  ", true).check(null));
            assertFalse(DEFAULT.parseString("  non-null=", true).check(null));
            assertFalse(DEFAULT.parseString("nonNull=  ", true).check(null));
            assertFalse(DEFAULT.parseString("  nonNull  =  ", true).check(null));
            assertThrows(ValidationParseException.class, () -> DEFAULT.parseString("not null", true));
            assertThrows(ValidationParseException.class, () -> DEFAULT.parseString("not-NULL", true));
            assertThrows(ValidationParseException.class, () -> DEFAULT.parseString("notnull", true));
            assertThrows(ValidationParseException.class, () -> DEFAULT.parseString("notnull=", true));
        }

        @Test
        public void testNotBlankValidation() {
            assertFalse(DEFAULT.parseString("not-blank  ", true).check(""));
            assertFalse(DEFAULT.parseString("notBlank", true).check(""));
            assertFalse(DEFAULT.parseString("  non-blank", true).check(""));
            assertFalse(DEFAULT.parseString("nonBlank", true).check(""));
            assertFalse(DEFAULT.parseString("  not-blank", true).check(" "));
            assertFalse(DEFAULT.parseString("notBlank  ", true).check(" "));
            assertFalse(DEFAULT.parseString("non-blank  ", true).check(" "));
            assertFalse(DEFAULT.parseString("  nonBlank", true).check(" "));
            assertFalse(DEFAULT.parseString("not-blank=  ", true).check(""));
            assertFalse(DEFAULT.parseString("notBlank=", true).check(""));
            assertFalse(DEFAULT.parseString("  non-blank=", true).check(""));
            assertFalse(DEFAULT.parseString("nonBlank=", true).check(""));
        }

        @Test
        public void testNotEmptyValidation() {
            assertFalse(DEFAULT.parseString(" not-empty", true).check(""));
            assertFalse(DEFAULT.parseString("notEmpty  ", true).check(""));
            assertFalse(DEFAULT.parseString("  non-empty", true).check(""));
            assertFalse(DEFAULT.parseString("nonEmpty  ", true).check(""));
            assertFalse(DEFAULT.parseString(" not-empty=", true).check(""));
            assertFalse(DEFAULT.parseString("notEmpty=  ", true).check(""));
            assertFalse(DEFAULT.parseString("  non-empty=", true).check(""));
            assertFalse(DEFAULT.parseString("nonEmpty=  ", true).check(""));
        }

        @Test
        public void testRangeValidation() {
            assertFalse(DEFAULT.parseString("  range=2.000001", true).check(3));
            assertFalse(DEFAULT.parseString("  range=2.000001|3  ", true).check(5));
            assertFalse(DEFAULT.parseString("  range=|3  ", true).check(4));
            assertFalse(DEFAULT.parseString("  range=2.000001|  ", true).check(0));
            assertFalse(DEFAULT.parseString("  range=  2", true).check(3));
            assertFalse(DEFAULT.parseString("  range  =   2|3  ", true).check(5));
            assertFalse(DEFAULT.parseString("  range  =  |3  ", true).check(4));
            assertFalse(DEFAULT.parseString("  range  =  2|  ", true).check(0));
            assertThrows(ValidationParseException.class, () -> DEFAULT.parseString("range =", true));
            assertThrows(ValidationParseException.class, () -> DEFAULT.parseString("range=2||", true));
            assertThrows(ValidationParseException.class, () -> DEFAULT.parseString("-0.0001=range", true));
        }

        @Test
        public void testSizeValidation() {
            assertFalse(DEFAULT.parseString("  size=4", true).check("foo"));
            assertFalse(DEFAULT.parseString("  size=2|2  ", true).check("foo"));
            assertFalse(DEFAULT.parseString("  size=|1  ", true).check("bar"));
            assertFalse(DEFAULT.parseString("  size=4|  ", true).check("bar"));
            assertThrows(ValidationParseException.class, () -> DEFAULT.parseString("size =", true));
            assertThrows(ValidationParseException.class, () -> DEFAULT.parseString("size=2||", true));
        }

        @Test
        public void testAggregatedValidation() {
            assertFalse(DEFAULT.parseString("non-null, size=2", true).check(" "));
            assertFalse(DEFAULT.parseString("non-null  , size=2", true).check(null));
            assertFalse(DEFAULT.parseString("  non-null  , size = 2  ", true).check(null));
            assertThrows(ValidationParseException.class, () -> DEFAULT.parseString("range=,,size=", true));
        }
    }
}
