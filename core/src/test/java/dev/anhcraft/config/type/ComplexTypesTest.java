package dev.anhcraft.config.type;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.SettingFlag;
import dev.anhcraft.config.context.PathType;
import java.lang.reflect.WildcardType;
import java.net.URL;
import java.util.*;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

public class ComplexTypesTest {
  @Test
  public void testWrapPrimitive() {
    assertEquals(Byte.class, ComplexTypes.wrapPrimitive(byte.class));
    assertEquals(Short.class, ComplexTypes.wrapPrimitive(short.class));
    assertEquals(Integer.class, ComplexTypes.wrapPrimitive(int.class));
    assertEquals(Long.class, ComplexTypes.wrapPrimitive(long.class));
    assertEquals(Float.class, ComplexTypes.wrapPrimitive(float.class));
    assertEquals(Double.class, ComplexTypes.wrapPrimitive(double.class));
    assertEquals(Character.class, ComplexTypes.wrapPrimitive(char.class));
    assertEquals(Boolean.class, ComplexTypes.wrapPrimitive(boolean.class));
  }

  @Test
  public void testWrapNonPrimitive() {
    assertEquals(Byte.class, ComplexTypes.wrapPrimitive(Byte.class));
    assertEquals(Short.class, ComplexTypes.wrapPrimitive(Short.class));
    assertEquals(Integer.class, ComplexTypes.wrapPrimitive(Integer.class));
    assertEquals(Long.class, ComplexTypes.wrapPrimitive(Long.class));
    assertEquals(Float.class, ComplexTypes.wrapPrimitive(Float.class));
    assertEquals(Double.class, ComplexTypes.wrapPrimitive(Double.class));
    assertEquals(Character.class, ComplexTypes.wrapPrimitive(Character.class));
    assertEquals(Boolean.class, ComplexTypes.wrapPrimitive(Boolean.class));
  }

  @Test
  public void testIsNormalClassOrAbstract() {
    assertTrue(ComplexTypes.isNormalClassOrAbstract(ArrayList.class));
    assertTrue(ComplexTypes.isNormalClassOrAbstract(AbstractCollection.class));
    assertTrue(ComplexTypes.isNormalClassOrAbstract(URL.class));
    assertFalse(ComplexTypes.isNormalClassOrAbstract(List.class));
    assertFalse(ComplexTypes.isNormalClassOrAbstract(Nullable.class));
    assertFalse(ComplexTypes.isNormalClassOrAbstract(PathType.class));
    assertFalse(ComplexTypes.isNormalClassOrAbstract(int.class));
    assertFalse(ComplexTypes.isNormalClassOrAbstract(int[].class));
    assertFalse(ComplexTypes.isNormalClassOrAbstract(int[][].class));
    assertFalse(ComplexTypes.isNormalClassOrAbstract(new Object() {}.getClass()));
  }

  @Test
  public void testIsArray() {
    assertTrue(ComplexTypes.isArray(int[].class));
    assertTrue(ComplexTypes.isArray(int[][].class));
    assertTrue(ComplexTypes.isArray(new TypeToken<List<Integer>[]>() {}.capture()));
    assertTrue(ComplexTypes.isArray(new TypeToken<List<Integer>[]>() {}));
    assertTrue(ComplexTypes.isArray(PathType[].class));
    assertTrue(ComplexTypes.isArray(PathType[][].class));
    assertFalse(ComplexTypes.isArray(int.class));
    assertFalse(ComplexTypes.isArray(List.class));
  }

  @Test
  public void testGetComponentType() {
    assertEquals(
        new TypeToken<List<Integer>>() {}.capture(),
        ComplexTypes.getComponentType(new TypeToken<List<Integer>[]>() {}.capture()));
    assertEquals(
        new TypeToken<List<Integer>>() {}.capture(),
        ComplexTypes.getComponentType(new TypeToken<List<Integer>[]>() {}));
    assertEquals(
        new TypeToken<List<Integer>[]>() {}.capture(),
        ComplexTypes.getComponentType(new TypeToken<List<Integer>[][]>() {}));
    assertEquals(int.class, ComplexTypes.getComponentType(int[].class));
    assertEquals(List.class, ComplexTypes.getComponentType(List[].class));
    assertNull(ComplexTypes.getComponentType(List.class));
    assertNull(ComplexTypes.getComponentType(Object.class));
  }

  @Test
  public void testGetArrayType() throws ClassNotFoundException {
    assertEquals(int[].class, ComplexTypes.getArrayType(int.class));
    assertEquals(int[][].class, ComplexTypes.getArrayType(int[].class));
    assertEquals(Object[].class, ComplexTypes.getArrayType(Object.class));
    assertEquals(Object[][].class, ComplexTypes.getArrayType(Object[].class));
    assertEquals(List[].class, ComplexTypes.getArrayType(List.class));
    assertEquals(List[][].class, ComplexTypes.getArrayType(List[].class));
    assertEquals(Nullable[].class, ComplexTypes.getArrayType(Nullable.class));
    assertEquals(Nullable[][].class, ComplexTypes.getArrayType(Nullable[].class));
    assertEquals(PathType[].class, ComplexTypes.getArrayType(PathType.class));
    assertEquals(PathType[][].class, ComplexTypes.getArrayType(PathType[].class));
  }

  @Test
  public void testErasure() throws ClassNotFoundException {
    assertEquals(int.class, ComplexTypes.erasure(int.class));
    assertEquals(Object.class, ComplexTypes.erasure(Object.class));
    assertEquals(List.class, ComplexTypes.erasure(List.class));
    assertEquals(List.class, ComplexTypes.erasure(new TypeToken<List<Integer>>() {}));
    assertEquals(List[].class, ComplexTypes.erasure(new TypeToken<List<Integer>[]>() {}.capture()));
    assertEquals(List[].class, ComplexTypes.erasure(new TypeToken<List<Integer>[]>() {}));
    assertEquals(List[][].class, ComplexTypes.erasure(new TypeToken<List<Integer>[][]>() {}));
    assertEquals(Object.class, ComplexTypes.erasure(ArrayList.class.getTypeParameters()[0]));
  }

  @Test
  public void testDescribe() {
    assertEquals("int", ComplexTypes.describe(int.class));
    assertEquals("int[]", ComplexTypes.describe(int[].class));
    assertEquals("int[][]", ComplexTypes.describe(int[][].class));
    assertEquals(
        "java.util.List<java.lang.Integer>",
        ComplexTypes.describe(new TypeToken<List<Integer>>() {}.capture()));
    assertEquals(
        "java.util.List<int[]>", ComplexTypes.describe(new TypeToken<List<int[]>>() {}.capture()));
    assertEquals(
        "java.util.List<java.util.Map<java.lang.String[],java.util.Set<int[][]>[]>>",
        ComplexTypes.describe(new TypeToken<List<Map<String[], Set<int[][]>[]>>>() {}.capture()));
    assertEquals(
        "dev.anhcraft.config.SettingFlag$Normalizer",
        ComplexTypes.describe(SettingFlag.Normalizer.class));

    Class<?> anonymous = new Object() {}.getClass();
    assertEquals(anonymous.getName(), ComplexTypes.describe(anonymous));
  }

  @Test
  public void testGetActualTypeArgument() {
    assertEquals(
        Integer.class,
        ComplexTypes.getActualTypeArgument(new TypeToken<List<Integer>>() {}.capture(), 0));
    assertNull(ComplexTypes.getActualTypeArgument(new TypeToken<List<Integer>>() {}.capture(), 1));
    assertInstanceOf(
        WildcardType.class,
        ComplexTypes.getActualTypeArgument(new TypeToken<List<?>>() {}.capture(), 0));
    assertNull(ComplexTypes.getActualTypeArgument(new TypeToken<int[]>() {}.capture(), 0));
  }

  @Test
  public void testIsCompatible() {
    assertTrue(ComplexTypes.isCompatible(int.class, Integer.class));
    assertTrue(ComplexTypes.isCompatible(Integer.class, int.class));
    assertTrue(ComplexTypes.isCompatible(double.class, Double.class));
    assertTrue(ComplexTypes.isCompatible(Double.class, double.class));
    assertTrue(ComplexTypes.isCompatible(boolean.class, Boolean.class));
    assertTrue(ComplexTypes.isCompatible(Boolean.class, boolean.class));
    assertTrue(ComplexTypes.isCompatible(byte.class, Byte.class));
    assertTrue(ComplexTypes.isCompatible(Byte.class, byte.class));
    assertTrue(ComplexTypes.isCompatible(char.class, Character.class));
    assertTrue(ComplexTypes.isCompatible(Character.class, char.class));
    assertTrue(ComplexTypes.isCompatible(short.class, Short.class));
    assertTrue(ComplexTypes.isCompatible(Short.class, short.class));
    assertTrue(ComplexTypes.isCompatible(long.class, Long.class));
    assertTrue(ComplexTypes.isCompatible(Long.class, long.class));
    assertTrue(ComplexTypes.isCompatible(float.class, Float.class));
    assertTrue(ComplexTypes.isCompatible(Float.class, float.class));

    assertFalse(ComplexTypes.isCompatible(int.class, Double.class));
    assertFalse(ComplexTypes.isCompatible(Integer.class, double.class));
    assertFalse(ComplexTypes.isCompatible(boolean.class, Byte.class));
    assertFalse(ComplexTypes.isCompatible(Character.class, short.class));

    assertTrue(ComplexTypes.isCompatible(String.class, String.class));
    assertFalse(ComplexTypes.isCompatible(Integer.class, String.class));

    assertTrue(ComplexTypes.isCompatible(int[].class, int[].class));
    assertTrue(ComplexTypes.isCompatible(Integer[].class, Integer[].class));
    assertTrue(ComplexTypes.isCompatible(String[].class, String[].class));
    assertTrue(ComplexTypes.isCompatible(int[].class, Integer[].class));
    assertFalse(ComplexTypes.isCompatible(String[].class, Integer[].class));

    assertTrue(ComplexTypes.isCompatible(int[][].class, int[][].class));
    assertTrue(ComplexTypes.isCompatible(Integer[][][].class, Integer[][][].class));
    assertTrue(ComplexTypes.isCompatible(String[][][][][].class, String[][][][][].class));
    assertTrue(ComplexTypes.isCompatible(int[][].class, Integer[][].class));
    assertFalse(ComplexTypes.isCompatible(String[][][].class, Integer[][][].class));

    assertFalse(ComplexTypes.isCompatible(int.class, int[].class));
    assertFalse(ComplexTypes.isCompatible(Integer.class, Integer[].class));
    assertFalse(ComplexTypes.isCompatible(String.class, String[].class));
    assertFalse(ComplexTypes.isCompatible(double.class, double[].class));
    assertFalse(ComplexTypes.isCompatible(Boolean.class, Boolean[].class));
    assertFalse(ComplexTypes.isCompatible(char.class, char[].class));
  }
}
