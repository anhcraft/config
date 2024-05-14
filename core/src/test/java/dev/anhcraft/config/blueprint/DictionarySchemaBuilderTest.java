package dev.anhcraft.config.blueprint;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.error.UnsupportedSchemaException;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class DictionarySchemaBuilderTest {
  @Test
  public void testSchemaWithDuplicateNames() {
    var schemaBuilder = DictionarySchema.create();
    schemaBuilder.addProperty("age", p -> p.withNames("age", "name").withType(int.class));
    schemaBuilder.addProperty("name", p -> p.withType(String.class));
    schemaBuilder.addProperty("age", p -> p.withType(double.class));
    assertThrows(UnsupportedSchemaException.class, schemaBuilder::build);
  }

  @Test
  public void testSchemaWithInvalidDictionaryProperty() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            DictionarySchema.create()
                .addProperty(
                    "foo",
                    p -> p.withType(int.class).withSchema(DictionarySchema.create().build())));
    assertThrows(
        IllegalArgumentException.class,
        () -> DictionarySchema.create().addProperty("foo", p -> p.withType(Set.class)));
  }

  @Test
  public void testSchemaWithExistingAliases() {
    var schema =
        DictionarySchema.create()
            .addProperty("name", p -> p.withType(String.class).withAliases("title"))
            .addProperty("title", p -> p.withType(String.class).withAliases("nickname"))
            .build();
    assertEquals(Set.of("name", "title", "nickname"), schema.propertyNames());
    assertEquals("name", schema.property("name").name());
    assertEquals("title", schema.property("title").name());
    assertEquals("title", schema.property("nickname").name());
  }

  @Test
  public void testBuildFoodSchema() {
    var ingredient =
        DictionarySchema.create()
            .addProperty("name", (p) -> p.withType(String.class))
            .addProperty("quantity", (p) -> p.withType(int.class))
            .build();

    var root =
        DictionarySchema.create()
            .addProperty("name", (p) -> p.withType(String.class))
            .addProperty("ingredients", (p) -> p.isDictionary(ingredient))
            .addProperty("instructions", (p) -> p.withType(String[].class))
            .build();

    assertEquals(Set.of("name", "quantity"), ingredient.propertyNames());

    assertNull(ingredient.property("name").schema());
    assertEquals(String.class, ingredient.property("name").type());
    assertTrue(ingredient.property("name").aliases().isEmpty());

    assertNull(ingredient.property("quantity").schema());
    assertEquals(int.class, ingredient.property("quantity").type());
    assertTrue(ingredient.property("quantity").aliases().isEmpty());

    assertEquals(Set.of("name", "ingredients", "instructions"), root.propertyNames());

    assertNull(root.property("name").schema());
    assertEquals(String.class, root.property("name").type());
    assertTrue(root.property("name").aliases().isEmpty());

    assertEquals(ingredient, root.property("ingredients").schema());
    assertEquals(Dictionary.class, root.property("ingredients").type());
    assertTrue(root.property("ingredients").aliases().isEmpty());

    assertNull(root.property("instructions").schema());
    assertEquals(String[].class, root.property("instructions").type());
    assertTrue(root.property("instructions").aliases().isEmpty());
  }

  @Test
  public void testBuildAnimalSchema() {
    var animalSchema =
        DictionarySchema.create()
            .addProperty("name", p -> p.withType(String.class))
            .addProperty("type", p -> p.withType(String.class))
            .addProperty("color", p -> p.withType(String.class).isOptional())
            .addProperty("legs", p -> p.withType(int.class))
            .addProperty("endangered", p -> p.withType(boolean.class).isOptional())
            .build();

    assertEquals(
        Set.of("name", "type", "color", "legs", "endangered"), animalSchema.propertyNames());

    assertNull(animalSchema.property("name").schema());
    assertEquals(String.class, animalSchema.property("name").type());
    assertTrue(animalSchema.property("name").aliases().isEmpty());

    assertNull(animalSchema.property("type").schema());
    assertEquals(String.class, animalSchema.property("type").type());
    assertTrue(animalSchema.property("type").aliases().isEmpty());

    assertNull(animalSchema.property("color").schema());
    assertEquals(String.class, animalSchema.property("color").type());
    assertTrue(animalSchema.property("color").aliases().isEmpty());
    assertTrue(animalSchema.property("color").isOptional());

    assertNull(animalSchema.property("legs").schema());
    assertEquals(int.class, animalSchema.property("legs").type());
    assertTrue(animalSchema.property("legs").aliases().isEmpty());

    assertNull(animalSchema.property("endangered").schema());
    assertEquals(boolean.class, animalSchema.property("endangered").type());
    assertTrue(animalSchema.property("endangered").aliases().isEmpty());
    assertTrue(animalSchema.property("endangered").isOptional());
  }

  @Test
  public void testBuildGameCharacterSchemas() {
    var characterSchema =
        DictionarySchema.create()
            .addProperty("name", p -> p.withType(String.class))
            .addProperty("class", p -> p.withType(String.class).isConstant())
            .addProperty("level", p -> p.withType(int.class).isTransient())
            .addProperty("health", p -> p.withType(double.class).isOptional())
            .addProperty("mana", p -> p.withType(double.class).isOptional())
            .build();

    var inventorySchema =
        DictionarySchema.create()
            .addProperty(
                "items",
                p ->
                    p.isDictionary(
                        DictionarySchema.create()
                            .addProperty("name", bp -> bp.withType(String.class))
                            .build()))
            .build();

    var skillSchema =
        DictionarySchema.create()
            .addProperty(
                "skills",
                p ->
                    p.withType(Dictionary.class)
                        .isConstant()
                        .withSchema(
                            DictionarySchema.create()
                                .addProperty("name", bp -> bp.withType(String.class))
                                .build()))
            .build();

    var questSchema =
        DictionarySchema.create()
            .addProperty(
                "quests",
                p ->
                    p.isTransient()
                        .isDictionaryArray(
                            DictionarySchema.create()
                                .addProperty("name", bp -> bp.withType(String.class))
                                .build()))
            .build();

    assertEquals(
        Set.of("name", "class", "level", "health", "mana"), characterSchema.propertyNames());
    assertTrue(characterSchema.property("name").aliases().isEmpty());
    assertEquals(String.class, characterSchema.property("name").type());
    assertFalse(characterSchema.property("class").isOptional());
    assertTrue(characterSchema.property("class").isConstant());
    assertTrue(characterSchema.property("level").isTransient());
    assertEquals(int.class, characterSchema.property("level").type());
    assertTrue(characterSchema.property("health").isOptional());
    assertEquals(double.class, characterSchema.property("health").type());
    assertTrue(characterSchema.property("mana").isOptional());
    assertEquals(double.class, characterSchema.property("mana").type());

    assertEquals(Set.of("items"), inventorySchema.propertyNames());
    assertEquals(Dictionary.class, inventorySchema.property("items").type());
    assertEquals(String.class, inventorySchema.property("items").schema().property("name").type());

    assertEquals(Set.of("skills"), skillSchema.propertyNames());
    assertFalse(skillSchema.property("skills").isOptional());
    assertTrue(skillSchema.property("skills").isConstant());
    assertEquals(Dictionary.class, skillSchema.property("skills").type());
    assertEquals(String.class, skillSchema.property("skills").schema().property("name").type());

    assertEquals(Set.of("quests"), questSchema.propertyNames());
    assertTrue(questSchema.property("quests").isTransient());
    assertEquals(Dictionary[].class, questSchema.property("quests").type());
    assertEquals(String.class, questSchema.property("quests").schema().property("name").type());
  }
}
