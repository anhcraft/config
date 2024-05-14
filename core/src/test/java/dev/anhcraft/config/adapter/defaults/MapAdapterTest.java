package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.type.TypeToken;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MapAdapterTest {
  private static Context context;
  private static MapAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new MapAdapter();
  }

  @Test
  public void testSimplify1D() throws Exception {
    Map<Integer, String> vehicle = new HashMap<>();
    vehicle.put(0, "Car");
    vehicle.put(1, "Truck");
    vehicle.put(2, "Motorcycle");
    Object simplified = adapter.simplify(context, Map.class, vehicle);
    assertInstanceOf(Dictionary.class, simplified);
    assertEquals(
        Dictionary.of(
            Map.of(
                "0", "Car",
                "1", "Truck",
                "2", "Motorcycle")),
        simplified);
  }

  @Test
  public void testSimplify1DCompoundKey() throws Exception {
    Map<int[], String> location = new HashMap<>();
    location.put(new int[] {0, 0}, "Home");
    location.put(new int[] {0, 1}, "Office");
    location.put(new int[] {1, 0}, "School");
    assertNull(adapter.simplify(context, Map.class, location));
  }

  @Test
  public void testSimplify2D() throws Exception {
    Map<String, Map<String, Integer>> stock = new HashMap<>();
    stock.put("Bob", Map.of("Apple", 10, "Banana", 20));
    stock.put("Alice", Map.of("Apple", 5, "Banana", 15));
    Object simplified = adapter.simplify(context, Map.class, stock);
    assertInstanceOf(Dictionary.class, simplified);
    assertEquals(
        Dictionary.of(
            Map.of(
                "Bob",
                    Dictionary.of(
                        Map.of(
                            "Apple", 10,
                            "Banana", 20)),
                "Alice",
                    Dictionary.of(
                        Map.of(
                            "Apple", 5,
                            "Banana", 15)))),
        simplified);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testComplexify() throws Exception {
    Dictionary dict =
        Dictionary.of(
            Map.of(
                "0", "Car",
                "1", "Truck",
                "2", "Motorcycle"));
    Map<Integer, String> vehicle =
        adapter.complexify(context, dict, new TypeToken<HashMap<Integer, String>>() {});
    assertInstanceOf(Map.class, vehicle);
    assertEquals("Car", vehicle.get(0));
    assertEquals("Truck", vehicle.get(1));
    assertEquals("Motorcycle", vehicle.get(2));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testComplexify2D() throws Exception {
    Dictionary dict =
        Dictionary.of(
            Map.of(
                "Bob",
                    Dictionary.of(
                        Map.of(
                            "Apple", "10",
                            "Banana", "20")),
                "Alice", Dictionary.of(Map.of("Apple", 5, "Banana", "15.0001"))));
    Map<String, Map<String, Integer>> stock =
        adapter.complexify(context, dict, new TypeToken<Map<String, Map<String, Integer>>>() {});
    assertInstanceOf(LinkedHashMap.class, stock);
    assertInstanceOf(LinkedHashMap.class, stock.get("Bob"));
    assertEquals(new LinkedHashMap<>(Map.of("Apple", 10, "Banana", 20)), stock.get("Bob"));
    assertInstanceOf(LinkedHashMap.class, stock.get("Alice"));
    assertEquals(new LinkedHashMap<>(Map.of("Apple", 5, "Banana", 15)), stock.get("Alice"));
  }

  @Test
  public void testComplexifyUseHashMap() throws Exception {
    assertInstanceOf(
        HashMap.class,
        adapter.complexify(
            context,
            Dictionary.of(Map.of("foo", "bar")),
            new TypeToken<HashMap<String, String>>() {}));
  }

  @Test
  public void testComplexifyUseTreeMap() throws Exception {
    assertInstanceOf(
        TreeMap.class,
        adapter.complexify(
            context,
            Dictionary.of(Map.of("foo", "bar")),
            new TypeToken<TreeMap<String, String>>() {}));
  }

  @Test
  public void testComplexifyUseLinkedHashMap() throws Exception {
    assertInstanceOf(
        LinkedHashMap.class,
        adapter.complexify(
            context, Dictionary.of(Map.of("foo", "bar")), new TypeToken<Map<String, String>>() {}));
  }
}
