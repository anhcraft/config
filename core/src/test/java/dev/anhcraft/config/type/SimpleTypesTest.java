package dev.anhcraft.config.type;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.SchemalessDictionary;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class SimpleTypesTest {
  @Test
  public void testIsScalar() {
    assertTrue(SimpleTypes.isScalar(byte.class));
    assertTrue(SimpleTypes.isScalar(short.class));
    assertTrue(SimpleTypes.isScalar(int.class));
    assertTrue(SimpleTypes.isScalar(long.class));
    assertTrue(SimpleTypes.isScalar(float.class));
    assertTrue(SimpleTypes.isScalar(double.class));
    assertTrue(SimpleTypes.isScalar(char.class));
    assertTrue(SimpleTypes.isScalar(boolean.class));
    assertTrue(SimpleTypes.isScalar(Byte.class));
    assertTrue(SimpleTypes.isScalar(Short.class));
    assertTrue(SimpleTypes.isScalar(Integer.class));
    assertTrue(SimpleTypes.isScalar(Long.class));
    assertTrue(SimpleTypes.isScalar(Float.class));
    assertTrue(SimpleTypes.isScalar(Double.class));
    assertTrue(SimpleTypes.isScalar(Character.class));
    assertTrue(SimpleTypes.isScalar(Boolean.class));
    assertTrue(SimpleTypes.isScalar(String.class));
  }

  @Test
  public void testValidateType() {
    assertTrue(SimpleTypes.validate(byte.class));
    assertTrue(SimpleTypes.validate(short.class));
    assertTrue(SimpleTypes.validate(int.class));
    assertTrue(SimpleTypes.validate(long.class));
    assertTrue(SimpleTypes.validate(float.class));
    assertTrue(SimpleTypes.validate(double.class));
    assertTrue(SimpleTypes.validate(char.class));
    assertTrue(SimpleTypes.validate(boolean.class));
    assertTrue(SimpleTypes.validate(Byte.class));
    assertTrue(SimpleTypes.validate(Short.class));
    assertTrue(SimpleTypes.validate(Integer.class));
    assertTrue(SimpleTypes.validate(Long.class));
    assertTrue(SimpleTypes.validate(Float.class));
    assertTrue(SimpleTypes.validate(Double.class));
    assertTrue(SimpleTypes.validate(Character.class));
    assertTrue(SimpleTypes.validate(Boolean.class));
    assertTrue(SimpleTypes.validate(String.class));
    assertTrue(SimpleTypes.validate(Dictionary.class));

    assertTrue(SimpleTypes.validate(byte[].class));
    assertTrue(SimpleTypes.validate(short[].class));
    assertTrue(SimpleTypes.validate(int[].class));
    assertTrue(SimpleTypes.validate(long[].class));
    assertTrue(SimpleTypes.validate(float[].class));
    assertTrue(SimpleTypes.validate(double[].class));
    assertTrue(SimpleTypes.validate(char[].class));
    assertTrue(SimpleTypes.validate(boolean[].class));
    assertTrue(SimpleTypes.validate(Byte[].class));
    assertTrue(SimpleTypes.validate(Short[].class));
    assertTrue(SimpleTypes.validate(Integer[].class));
    assertTrue(SimpleTypes.validate(Long[].class));
    assertTrue(SimpleTypes.validate(Float[].class));
    assertTrue(SimpleTypes.validate(Double[].class));
    assertTrue(SimpleTypes.validate(Character[].class));
    assertTrue(SimpleTypes.validate(Boolean[].class));
    assertTrue(SimpleTypes.validate(String[].class));
    assertTrue(SimpleTypes.validate(Dictionary[].class));

    assertTrue(SimpleTypes.validate(byte[][].class));
    assertTrue(SimpleTypes.validate(short[][].class));
    assertTrue(SimpleTypes.validate(int[][].class));
    assertTrue(SimpleTypes.validate(long[][].class));
    assertTrue(SimpleTypes.validate(float[][].class));
    assertTrue(SimpleTypes.validate(double[][].class));
    assertTrue(SimpleTypes.validate(char[][].class));
    assertTrue(SimpleTypes.validate(boolean[][].class));
    assertTrue(SimpleTypes.validate(Byte[][].class));
    assertTrue(SimpleTypes.validate(Short[][].class));
    assertTrue(SimpleTypes.validate(Integer[][].class));
    assertTrue(SimpleTypes.validate(Long[][].class));
    assertTrue(SimpleTypes.validate(Float[][].class));
    assertTrue(SimpleTypes.validate(Double[][].class));
    assertTrue(SimpleTypes.validate(Character[][].class));
    assertTrue(SimpleTypes.validate(Boolean[][].class));
    assertTrue(SimpleTypes.validate(String[][].class));
    assertTrue(SimpleTypes.validate(Dictionary[][].class));
  }

  @Test
  public void testValidateValue() {
    assertTrue(SimpleTypes.test(null));
    assertTrue(SimpleTypes.test("hello world"));
    assertTrue(SimpleTypes.test(0));
    assertTrue(SimpleTypes.test(true));
    assertTrue(SimpleTypes.test('0'));
    assertTrue(SimpleTypes.test(new SchemalessDictionary()));
    assertTrue(SimpleTypes.test(new Object[] {1, "1", true, null}));
    assertFalse(SimpleTypes.test(new Object[] {List.class, "1", true}));
    assertTrue(SimpleTypes.test(new int[] {}));
    assertTrue(SimpleTypes.test(new Integer[] {}));
  }

  @Test
  public void testGetContainerSize() {
    assertEquals(1, SimpleTypes.getContainerSize(0));
    assertEquals(0, SimpleTypes.getContainerSize(new Integer[] {}));
    assertEquals(1, SimpleTypes.getContainerSize(new Integer[] {0}));
    assertEquals(0, SimpleTypes.getContainerSize(new int[] {}));
    assertEquals(1, SimpleTypes.getContainerSize(new int[] {0}));
    assertEquals(0, SimpleTypes.getContainerSize(new SchemalessDictionary()));
    assertEquals(1, SimpleTypes.getContainerSize(Dictionary.of(Map.of("foo", "bar"))));
  }

  @Test
  public void testGetContainerElement() {
    assertEquals(1, SimpleTypes.getContainerElement(1, 0));
    assertEquals(0, SimpleTypes.getContainerElement(new Integer[] {0}, 0));
    assertEquals(0, SimpleTypes.getContainerElement(new int[] {0}, 0));
    assertEquals("bar", SimpleTypes.getContainerElement(Dictionary.of(Map.of("foo", "bar")), 0));
  }

  @Test
  public void testDeepClone() {
    assertNull(SimpleTypes.deepClone(null));
    assertEquals("a", SimpleTypes.deepClone("a"));
    assertEquals('a', SimpleTypes.deepClone('a'));
    assertEquals(0, SimpleTypes.deepClone(0));

    Dictionary dict = new SchemalessDictionary();
    assertNotSame(dict, SimpleTypes.deepClone(dict));

    int[] arr = new int[0];
    assertNotSame(arr, SimpleTypes.deepClone(arr));

    Integer[] arrObj = new Integer[0];
    assertNotSame(arrObj, SimpleTypes.deepClone(arrObj));
  }
}
