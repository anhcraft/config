package dev.anhcraft.config.internal;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.InstanceAssembler;
import dev.anhcraft.config.InstanceFactory;
import dev.anhcraft.config.context.Context;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class InstanceFactoryImplTest {

  private InstanceFactory instanceFactory;

  @BeforeEach
  void setUp() {
    Map<Class<?>, InstanceAssembler> instanceAssemblers = new HashMap<>();
    instanceFactory = new InstanceFactoryImpl(instanceAssemblers);
  }

  @Test
  void testGetInstanceAssemblerWithDefaultConstructor() throws InstantiationException {
    InstanceAssembler assembler =
        instanceFactory.getInstanceAssembler(TestClassWithDefaultConstructor.class);
    assertNotNull(assembler);

    TestClassWithDefaultConstructor instance =
        assembler.newInstance(new ContextStub(), TestClassWithDefaultConstructor.class);
    assertNotNull(instance);
  }

  @Test
  void testGetInstanceAssemblerWithoutDefaultConstructor() throws InstantiationException {
    InstanceAssembler assembler =
        instanceFactory.getInstanceAssembler(TestClassWithoutDefaultConstructor.class);
    assertNotNull(assembler);

    TestClassWithoutDefaultConstructor instance =
        assembler.newInstance(new ContextStub(), TestClassWithoutDefaultConstructor.class);
    assertNotNull(instance);
  }

  @Test
  void testGetInstanceAssemblerWithNonInstantiableClass() {
    Executable executable = () -> instanceFactory.getInstanceAssembler(AbstractClass.class);
    assertThrows(IllegalArgumentException.class, executable);
  }

  @Test
  void testNewInstance() throws InstantiationException {
    TestClassWithDefaultConstructor instance =
        instanceFactory.newInstance(new ContextStub(), TestClassWithDefaultConstructor.class);
    assertNotNull(instance);
  }

  @Test
  void testNewInstanceWithNonInstantiableClass() {
    Executable executable =
        () -> instanceFactory.newInstance(new ContextStub(), AbstractClass.class);
    assertThrows(IllegalArgumentException.class, executable);
  }

  public static class TestClassWithDefaultConstructor {
    public TestClassWithDefaultConstructor() {}
  }

  public static class TestClassWithoutDefaultConstructor {
    private TestClassWithoutDefaultConstructor() {}
  }

  public abstract static class AbstractClass {}

  public static class ContextStub extends Context {
    public ContextStub() {
      super(ConfigFactory.create().build());
    }
  }
}
