package common;

import dev.anhcraft.config.meta.Alias;
import dev.anhcraft.config.meta.Validate;

public class Product {
  public Product(String id, String name, double price) {
    this.id = id;
    this.name = name;
    this.price = price;
  }

  @Validate("not-null")
  public String id;

  @Validate("not-null")
  @Alias("label")
  public String name;

  @Validate("range=0|")
  @Alias("cost")
  public double price;
}
