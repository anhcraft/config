package dev.anhcraft.config;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.meta.*;
import java.util.*;
import org.junit.jupiter.api.*;

public class ConfigDenormalizerPolymorphismTest {
  @Test
  public void testUnregisteredShape() {
    class BaseConfig {
      @Discriminator public int version;
    }

    @Shape(discriminator = "version", value = "1")
    class ConfigV1 extends BaseConfig {}

    ConfigFactory factory = ConfigFactory.create().build();
    assertInstanceOf(
        BaseConfig.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 1).build(), BaseConfig.class));
  }

  @Test
  public void testMatchSingleTypeSingleShape() {
    class BaseConfig {
      @Discriminator public int version;
    }

    @Shape(discriminator = "version", value = "1")
    class ConfigV1 extends BaseConfig {
      public String name;
    }

    ConfigFactory factory = ConfigFactory.create().build();
    factory.getShapeRegistry().register(ConfigV1.class);
    BaseConfig cfg =
        (BaseConfig)
            factory
                .getDenormalizer()
                .denormalize(
                    SchemalessDictionary.create().put("version", 1).put("name", "foo").build(),
                    BaseConfig.class);
    assertInstanceOf(ConfigV1.class, cfg);
    assertEquals("foo", ((ConfigV1) cfg).name);
  }

  @Test
  public void testUnmatchedShape() {
    class BaseConfig {
      @Discriminator public int version;
    }

    @Shape(discriminator = "version", value = "1")
    class ConfigV1 extends BaseConfig {
      public String name;
    }

    ConfigFactory factory = ConfigFactory.create().build();
    factory.getShapeRegistry().register(ConfigV1.class);
    assertInstanceOf(
        BaseConfig.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 2).put("name", "foo").build(),
                BaseConfig.class));
  }

  @Test
  public void testMatchSingleTypeDualShape() {
    class BaseConfig {
      @Discriminator public int version;

      @Discriminator
      @Name("type")
      public String versionType;
    }

    @Shape(discriminator = "version", value = "1")
    @Shape(discriminator = "versionType", value = "next")
    class ConfigV1 extends BaseConfig {
      public String name;
    }

    ConfigFactory factory = ConfigFactory.create().build();
    factory.getShapeRegistry().register(ConfigV1.class);

    BaseConfig cfg =
        (BaseConfig)
            factory
                .getDenormalizer()
                .denormalize(
                    SchemalessDictionary.create().put("version", 1).put("name", "foo").build(),
                    BaseConfig.class);
    assertInstanceOf(ConfigV1.class, cfg);
    assertEquals("foo", ((ConfigV1) cfg).name);

    cfg =
        (BaseConfig)
            factory
                .getDenormalizer()
                .denormalize(
                    SchemalessDictionary.create().put("type", "next").put("name", "bar").build(),
                    BaseConfig.class);
    assertInstanceOf(ConfigV1.class, cfg);
    assertEquals("bar", ((ConfigV1) cfg).name);
  }

  @Test
  public void testMatchingSingleDiscriminatorOverlappedShapes() {
    class BaseConfig {
      @Discriminator public int version;
    }

    @Shape(discriminator = "version", value = "1")
    class ConfigV1 extends BaseConfig {}

    @Shape(discriminator = "version", value = "2")
    class ConfigV2 extends ConfigV1 {}

    @Shape(discriminator = "version", value = "3")
    class ConfigV3 extends BaseConfig {}

    ConfigFactory factory = ConfigFactory.create().build();
    factory.getShapeRegistry().register(ConfigV1.class);
    factory.getShapeRegistry().register(ConfigV2.class);
    factory.getShapeRegistry().register(ConfigV3.class);

    assertInstanceOf(
        ConfigV1.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 1).build(), BaseConfig.class));
    assertInstanceOf(
        ConfigV2.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 2).build(), BaseConfig.class));
    assertInstanceOf(
        ConfigV3.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 3).build(), BaseConfig.class));
  }

  @Test
  public void testMatchingDualDiscriminatorOverlappedShapes() {
    class BaseConfig {
      @Discriminator public int version;
    }

    @Shape(discriminator = "version", value = "1")
    class ConfigV1 extends BaseConfig {
      @Discriminator public int type;
    }

    @Shape(discriminator = "version", value = "2")
    @Shape(discriminator = "type", value = "1")
    class ConfigV2 extends ConfigV1 {}

    @Shape(discriminator = "version", value = "3")
    class ConfigV3 extends BaseConfig {}

    ConfigFactory factory = ConfigFactory.create().build();
    factory.getShapeRegistry().register(ConfigV1.class);
    factory.getShapeRegistry().register(ConfigV2.class);
    factory.getShapeRegistry().register(ConfigV3.class);

    assertInstanceOf(
        ConfigV1.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 1).build(), BaseConfig.class));
    assertInstanceOf(
        ConfigV2.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 2).build(), BaseConfig.class));
    assertInstanceOf(
        ConfigV2.class,
        factory
            .getDenormalizer()
            .denormalize(SchemalessDictionary.create().put("type", 1).build(), ConfigV1.class));
    assertInstanceOf(
        ConfigV3.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 3).build(), BaseConfig.class));
  }

  @Test
  public void testMatchingOverrideDiscriminatorOverlappedShapes() {
    class BaseConfig {
      @Discriminator public int version;
    }

    @Shape(discriminator = "version", value = "1")
    class ConfigV1 extends BaseConfig {
      @Discriminator
      @Name("version")
      public int type;
    }

    @Shape(discriminator = "type", value = "1")
    @Shape(discriminator = "version", value = "2")
    class ConfigV2 extends ConfigV1 {}

    @Shape(discriminator = "version", value = "3")
    class ConfigV3 extends BaseConfig {}

    ConfigFactory factory = ConfigFactory.create().build();
    factory.getShapeRegistry().register(ConfigV1.class);
    factory.getShapeRegistry().register(ConfigV2.class);
    factory.getShapeRegistry().register(ConfigV3.class);

    assertInstanceOf(
        ConfigV1.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 1).build(), BaseConfig.class));
    assertInstanceOf(
        ConfigV2.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 2).build(), BaseConfig.class));
    assertInstanceOf(
        ConfigV2.class,
        factory
            .getDenormalizer()
            .denormalize(SchemalessDictionary.create().put("version", 1).build(), ConfigV1.class));
    assertInstanceOf(
        ConfigV1.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("type", 1).build(), ConfigV1.class)); // fallback
    assertInstanceOf(
        ConfigV3.class,
        factory
            .getDenormalizer()
            .denormalize(
                SchemalessDictionary.create().put("version", 3).build(), BaseConfig.class));
  }
}
