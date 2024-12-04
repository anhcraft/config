package dev.anhcraft.config;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.meta.Discriminator;
import dev.anhcraft.config.meta.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ShapeRegistryTest {
  private static ConfigFactory factory;

  @BeforeEach
  public void setup() {
    factory = ConfigFactory.create().build();
  }

  @ParameterizedTest
  @MethodSource("provideC1_C2_P")
  public void testDiscriminator(List<Class<?>> classes) throws Exception {
    ShapeRegistry registry = factory.getShapeRegistry();
    for (Class<?> clazz : classes) {
      registry.register(clazz);
    }

    Context ctx = factory.createContext();
    Parent base = new Parent();

    base.version = 0;
    assertNull(registry.solve(ctx, base));

    base.version = 1;
    assertEquals(Child1.class, registry.solve(ctx, base));

    base.version = 2;
    assertEquals(Child2.class, registry.solve(ctx, base));

    base.version = 3;
    assertNull(registry.solve(ctx, base)); // we have not registered Child21
  }

  @ParameterizedTest
  @MethodSource("provideC1_C2_P")
  public void testAdjacentDiscriminator(List<Class<?>> classes) throws Exception {
    ShapeRegistry registry = factory.getShapeRegistry();
    for (Class<?> clazz : classes) {
      registry.register(clazz);
    }

    Context ctx = factory.createContext();

    GrantParent base = new GrantParent();
    base.majorVersion = "-1";
    assertEquals(Parent.class, registry.solve(ctx, base));

    base = new Parent();
    base.majorVersion = "-1";
    assertEquals(Parent.class, registry.solve(ctx, base));
  }

  @Test
  public void testShapeLinkingAmbiguousTwoSiblings() {
    ShapeRegistry registry = factory.getShapeRegistry();
    registry.register(Child1.class);
    assertThrows(IllegalArgumentException.class, () -> registry.register(Child21A.class));
  }

  @Test
  public void testShapeLinkingSubtypeReplaceSuperType() throws Exception {
    ShapeRegistry registry = factory.getShapeRegistry();
    registry.register(Child2.class);
    assertDoesNotThrow(() -> registry.register(Child21B.class));

    Context ctx = factory.createContext();
    Parent base = new Parent();
    base.version = 2;
    assertEquals(Child21B.class, registry.solve(ctx, base));
  }

  @Test
  public void testShapeLinkingSuperTypeDoesNotReplaceSubtype() throws Exception {
    ShapeRegistry registry = factory.getShapeRegistry();
    registry.register(Child21B.class);
    assertDoesNotThrow(() -> registry.register(Child2.class));

    Context ctx = factory.createContext();
    Parent base = new Parent();
    base.version = 2;
    assertEquals(Child21B.class, registry.solve(ctx, base));
  }

  @ParameterizedTest
  @MethodSource("provideC2_C21_C21A")
  public void testOverlappedDiscriminator(List<Class<?>> classes) throws Exception {
    ShapeRegistry registry = factory.getShapeRegistry();
    for (Class<?> clazz : classes) {
      registry.register(clazz);
    }

    Context ctx = factory.createContext();
    {
      Parent base = new Parent();

      base.version = 1;
      assertEquals(Child21A.class, registry.solve(ctx, base));

      base.version = 2;
      assertEquals(Child2.class, registry.solve(ctx, base));

      registry.register(Child21B.class); // Child21B now holds more detailed shape for version 2
      assertEquals(Child21B.class, registry.solve(ctx, base));

      base.version = 3;
      assertEquals(Child21.class, registry.solve(ctx, base));
    }

    {
      Child21 base = new Child21();
      base.version = '3';
      assertNull(registry.solve(ctx, base));

      base.version = '1';
      assertEquals(Child21A.class, registry.solve(ctx, base));
    }
  }

  static class GrantParent {
    @Discriminator protected String majorVersion;
    private int version;
  }

  @Shape(discriminator = "version", value = "0") // should be ignored by Parent#version
  @Shape(discriminator = "majorVersion", value = "-1")
  static class Parent extends GrantParent {
    @Discriminator private int version;
  }

  @Shape(discriminator = "version", value = "1")
  static class Child1 extends Parent {
    private String kind;
  }

  @Shape(discriminator = "version", value = "2")
  static class Child2 extends Parent {
    private int type;
  }

  @Shape(discriminator = "version", value = "3")
  static class Child21 extends Child2 {
    @Discriminator private char version;
  }

  @Shape(discriminator = "version", value = "1")
  static class Child21A extends Child21 {}

  @Shape(discriminator = "version", value = "2")
  static class Child21B extends Child21 {}

  private static <T> List<List<T>> generatePermutations(List<T> elements) {
    List<List<T>> permutations = new ArrayList<>();
    permute(new ArrayList<>(elements), 0, permutations);
    return permutations;
  }

  private static <T> void permute(List<T> elements, int start, List<List<T>> result) {
    if (start == elements.size() - 1) {
      result.add(new ArrayList<>(elements));
    } else {
      for (int i = start; i < elements.size(); i++) {
        Collections.swap(elements, start, i);
        permute(elements, start + 1, result);
        Collections.swap(elements, start, i);
      }
    }
  }

  private static List<List<Class<?>>> provideC1_C2_P() {
    return generatePermutations(List.of(Child1.class, Child2.class, Parent.class));
  }

  private static List<List<Class<?>>> provideC2_C21_C21A() {
    return generatePermutations(List.of(Child2.class, Child21.class, Child21A.class));
  }
}
