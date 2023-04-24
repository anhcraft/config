# config
Configuration mapper for Java objects

[![Release](https://travis-ci.com/anhcraft/config.svg?branch=master)](https://travis-ci.com/anhcraft/config)
[![](https://jitpack.io/v/anhcraft/config.svg)](https://jitpack.io/#anhcraft/config)<br>

## Build
```clean test package install```

If `java: package sun.misc does not exist` occurs, disable the `--release` option in Java compiler settings (IntelliJ)

## Features
- Support common Java types including collection, map, etc
- Support platforms:
  1. Bukkit: `Yaml`
  2. Bungeecord: `Yaml`, `JSON`
  3. [Neep](https://github.com/anhcraft/Neep): my own configuration format. Let's check it out!
- Custom adapter (mapper)
- Various settings for quickly serializing/deserializing
- Nesting sections supported
- `ConfigDoc`: runtime config documentation generator

## Usage

### Simple & Complex types
- Simple types are basic unit types that any configuration system must be able to understand. They include:
  - number, character, boolean
  - array
  - string
  - configuration section
- Complex types are Java objects & primitives

### Schema
A schema represents the structure of a class.

```java
ConfigSchema schema = SchemaScanner.scanConfig(YourConfig.class);
```

By default, they are cached. To turn off:

```java
ConfigSchema schema = SchemaScanner.scanConfig(YourConfig.class, false);
```

### Annotations

#### Configurable
To make a configurable class (a class representing a configuration), add the `@Configurable`

```java
@Configurable
public class MyConfig {
    // ...
}
```

By default, all declared fields (either private or public) are considered configurable, and will be the configuration settings.

#### Exclude
`@Exclude` is used to mark a specific field as non-configurable

```java
@Configurable
public class MyConfig {
    private int times; // configurable

    @Exclude
    private String msg; // not configurable
}
```

#### Path
`@Path` specifies the key path of the related field. If it is missing, the field's name will be used. Path is platform-independent - e.g: path with dots such as `a.b.c` may be invalid path in specific platforms.

```java
@Path("TIMES")
private int times;
```

#### Description
This annotation is used to describe an option. It will also be shown in the ConfigDoc.

```java
@Path("TIMES")
@Description({"Line 1", "Line 2"})
private int times;
```

#### Validation
This annotation is used to quickly validate value of an option.

```java
@Validation(notNull = true)
private String msg;
```

`silent = true` option can be added to avoid exceptions.

#### Example(s)
`@Example` and `@Examples` is used for showing examples in how to configure the associated option. Examples will also be shown in the ConfigDoc.

```java
@Example("times: 10")
private int times;
```

#### Constant
Adding `@Constant` to an option prevents it being changed during the deserialization process.

#### Virtual
Adding `@Virtual` to an option prevents it being exposed to the configuration during the serialization process.

For example, `id` is a runtime-generated option. We do not want it to be exported.

#### PostHandler
`@PostHandler` is used with methods inside a `@Configurable` class. It indicates a handler that will be executed when an object was deserialized from a config.

The following parameters can be present in the involving method:
- The first one should be ConfigDeserializer
- The second one should be ConfigSchema
- The third one should be ConfigSection

All post handlers will be executed in order, and before ConfigDeserializer.Callback

```
@PostHandler
public void callback1() {
    
}

@PostHandler
public void callback2(ConfigDeserializer deserializer) {
    
}

@PostHandler
public void callback3(ConfigDeserializer deserializer, ConfigSchema schema) {
    
}

@PostHandler
public void callback4(ConfigDeserializer deserializer, ConfigSchema schema, ConfigSection section) {
    
}
```

## Middlewares
Middleware allows to handle configuration entries at pre-serialization / pre-deserialization stage.<br>
For example, it can be used to transform types, relocate paths before validation or transformation take place.

### Built-in: EntryKeyInjector (deserialization)
```
given: ConfigSection<K, V extends ConfigSection>
and filter: if K matches whatever return S, otherwise null 
for any K matched in which S is non-null: set V[S] = K
```

Example usage:
```java
d.addMiddleware(new EntryKeyInjector(entrySchema -> {
    return entrySchema.getKey().equals("groups") ? "id" : null;
))
```

We have:
```java
@Configurable
public class RoleTable {
    public Map<String, UserGroup> groups;
}

@Configurable
public class UserGroup {
  @Virtual
  public String id;

  public String name;
}
```

Assume we deserialize the following configuration:
```yaml
groups:
  admin:
    name: "Admin"
  member:
    name: "Member"
```

If we do not use the injector, after deserializing, the `id` will be null.
```java
groups.get("admin").id // null
groups.get("member").id // null
```

By using the EntryKeyInjector middleware, we copy the key of `groups` to the `id` field:
```java
groups.get("admin").id // == "admin"
groups.get("member").id // == "member"
```

This pattern is commonly used with `@Virtual` to prevent the `id` being exported to the child section.

## Callback
The deserializer supports callbacks to call a specific code after deserialization.

## Custom adapters
Take a look at how the color adapter works (Bukkit platform)

```java
public class ColorAdapter implements TypeAdapter<Color> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Color value) throws Exception {
        ConfigSection cs = serializer.getConfigProvider().createSection();
        cs.set("red", value.getRed());
        cs.set("green", value.getGreen());
        cs.set("blue", value.getBlue());
        return SimpleForm.of(cs);
    }

    @Override
    public @Nullable Color complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isSection()) {
            ConfigSection cs = Objects.requireNonNull(value.asSection());
            return Color.fromRGB(
                    Optional.ofNullable(cs.get("red")).map(SimpleForm::asInt).orElse(0),
                    Optional.ofNullable(cs.get("green")).map(SimpleForm::asInt).orElse(0),
                    Optional.ofNullable(cs.get("blue")).map(SimpleForm::asInt).orElse(0)
            );
        }
        return null;
    }
}
```

Then registers it to the `ConfigHandler`:
```java
handler.registerTypeAdapter(Color.class, new ColorAdapter());
```

## ConfigDoc
ConfigDoc is a built-in automatic-generated documentation which can be executed at runtime.

Example usage:
```java
new ConfigDocGenerator()
        .withSchemaOf(MyConfig1.class)
        .withSchemaOf(MyConfig2.class)
        .withSchemaOf(MyConfig3.class)
        .withSchema(CachedSchema)
        .combineWith(anotherConfigDocGenerator)
        .addJavadoc("(com.example*)", "https://example.com/")
        .generate(new File("docs"));
```
