import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.ContextProvider;
import dev.anhcraft.config.context.InjectableContext;
import dev.anhcraft.config.context.injector.PropertyDescriptionInjector;
import dev.anhcraft.config.json.JsonWriter;
import dev.anhcraft.config.json.JsonWriterOptions;
import dev.anhcraft.config.meta.Describe;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class JsonWriterTest {
  public static class Store {
    public List<Product> products;
  }

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
    Store store = new Store();
    store.products =
        List.of(
            new Product("1", "Organic Avocado", 1.99),
            new Product("2", "Artisanal Cheese", 8.99),
            new Product("3", "Gourmet Chocolate Bar", 4.99),
            new Product("4", "Fresh Salmon Fillet", 12.99),
            new Product("5", "Homemade Granola", 6.99));

    ConfigFactory factory =
        ConfigFactory.create()
            .provideContext(
                new ContextProvider() {
                  @Override
                  public @NotNull Context provideNormalizationContext(
                      @NotNull ConfigFactory factory) {
                    return new InjectableContext(factory).inject(new PropertyDescriptionInjector());
                  }
                })
            .build();

    StringWriter stringWriter = new StringWriter();
    BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
    JsonWriter serializer = new JsonWriter(bufferedWriter, JsonWriterOptions.DEFAULT);
    serializer.serialize(factory.getNormalizer().normalize(store));
    bufferedWriter.flush();
  }
}
