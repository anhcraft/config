import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.json.JsonSerializer;
import dev.anhcraft.config.meta.Describe;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.StringWriter;

public class CompatibleJsonCommentTest {
  public static class Product {
    public Product(String id, String name, double price) {
      this.id = id;
      this.name = name;
      this.price = price;
    }
    @Describe("The id of the product")
    public String id;
    @Describe("The name of the product")
    public String name;
    @Describe("The price of the product")
    public double price;
  }

  @Test
  public void testSerializeJsonWithCompatibleComment() throws Exception {
    Product product = new Product("1", "Product 1", 1000.0);

    ConfigFactory factory = ConfigFactory.create().build();

    StringWriter stringWriter = new StringWriter();
    BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
    JsonSerializer serializer = new JsonSerializer(bufferedWriter, JsonSerializer.Options.DEFAULT);
    serializer.serialize(factory.getNormalizer().normalize(product));
    bufferedWriter.flush();
    System.out.println(stringWriter);
  }
}
