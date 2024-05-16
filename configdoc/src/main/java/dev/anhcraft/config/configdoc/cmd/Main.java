package dev.anhcraft.config.configdoc.cmd;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.blueprint.DictionarySchema;
import dev.anhcraft.config.configdoc.ConfigDocGenerator;
import dev.anhcraft.jvmkit.utils.FileUtil;
import java.io.File;
import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    var factory = ConfigFactory.create().build();
    var itemSchema =
        DictionarySchema.create()
            .addProperty(
                "itemCode",
                p ->
                    p.withType(String.class)
                        .withDescription("The unique code identifying the item")
                        .withValidator("not-null, not-empty, not-blank"))
            .addProperty(
                "itemName",
                p ->
                    p.withType(String.class)
                        .withDescription("The name or description of the item")
                        .withValidator("not-null, not-empty"))
            .addProperty(
                "quantity",
                p ->
                    p.withType(int.class)
                        .withDescription("The current quantity of this item in the warehouse")
                        .withValidator("range=0|")) // non-negative quantity
            .addProperty(
                "category",
                p ->
                    p.withType(String.class)
                        .withDescription("The category or type of the item")
                        .withValidator("not-null, not-empty"))
            .addProperty(
                "location",
                p ->
                    p.withType(String.class)
                        .withDescription("The storage location within the warehouse")
                        .withValidator("not-null, not-empty"))
            .addProperty(
                "unitPrice",
                p ->
                    p.withType(Double.class)
                        .withDescription("The price per unit of this item")
                        .withValidator("not-null, range=0|")) // non-negative price
            .addProperty(
                "expiryDate",
                p ->
                    p.withType(String.class)
                        .withDescription("The expiry date of the item, if applicable"))
            .addProperty(
                "isFragile",
                p -> p.withType(Boolean.class).withDescription("Indicates if the item is fragile"))
            .build();
    var inventorySchema =
        DictionarySchema.create()
            .addProperty(
                "inventoryId",
                p ->
                    p.withType(String.class)
                        .withDescription("The unique identifier for the inventory")
                        .withValidator("not-null, not-empty, not-blank"))
            .addProperty(
                "location",
                p ->
                    p.withType(String.class)
                        .withDescription("The location of the warehouse")
                        .withValidator("not-null, not-empty"))
            .addProperty(
                "items",
                p ->
                    p.withType(Dictionary.class)
                        .withDescription("The total number of items in the inventory")
                        .withSchema(itemSchema))
            .addProperty(
                "lastUpdated",
                p ->
                    p.withType(String.class)
                        .withDescription("The date when the inventory was last updated"))
            .addProperty(
                "manager",
                p ->
                    p.withType(String.class)
                        .withDescription("The manager responsible for the inventory")
                        .withValidator("not-null, not-empty"))
            .build();

    FileUtil.clean(new File("web"));

    new ConfigDocGenerator()
        .withSchema("Item", itemSchema)
        .withSchema("Inventory", inventorySchema)
        .generate(new File("web"));
  }
}
