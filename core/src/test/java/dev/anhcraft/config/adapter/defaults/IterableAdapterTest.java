package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.type.TypeToken;
import java.util.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IterableAdapterTest {
  private static Context context;
  private static IterableAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new IterableAdapter();
  }

  @Test
  public void testSimplify1D() throws Exception {
    List<String> food =
        List.of("Pizza", "Burger", "Salad", "Sushi", "Pasta", "Ice Cream", "Tacos", "Steak");
    Object[] simplified = (Object[]) adapter.simplify(context, List.class, food);
    assertArrayEquals(food.toArray(), simplified);
  }

  @Test
  public void testSimplify2D() throws Exception {
    List<List<String>> food =
        List.of(
            List.of("Salad", "Sushi", "Fruit Salad"),
            List.of("Pizza", "Pasta"),
            List.of("Ice Cream"));
    Object[] simplified = (Object[]) adapter.simplify(context, List.class, food);
    assertArrayEquals(
        new String[][] {
          {"Salad", "Sushi", "Fruit Salad"},
          {"Pizza", "Pasta"},
          {"Ice Cream"}
        },
        simplified);
  }

  @Test
  public void testComplexifyNotArray() throws Exception {
    assertNull(adapter.complexify(context, new Dictionary(), List.class));
  }

  @Test
  public void testComplexifyScalar() throws Exception {
    assertEquals(List.of(1), adapter.complexify(context, 1, new TypeToken<List<Integer>>() {}));
    assertEquals(
        List.of("foo"), adapter.complexify(context, "foo", new TypeToken<List<String>>() {}));
    assertEquals(
        List.of('a'), adapter.complexify(context, 'a', new TypeToken<List<Character>>() {}));
    assertEquals(
        List.of(5.000f), adapter.complexify(context, 5.000f, new TypeToken<List<Float>>() {}));
  }

  @Test
  public void testComplexifyList() throws Exception {
    assertEquals(
        List.of(1, 2, 3),
        adapter.complexify(context, new int[] {1, 2, 3}, new TypeToken<List<Integer>>() {}));
    assertEquals(
        List.of(List.of(5, 0, 0), List.of(1, 0, 2), List.of(-3, 2, 4)),
        adapter.complexify(
            context,
            new int[][] {
              {5, 0, 0},
              {1, 0, 2},
              {-3, 2, 4}
            },
            new TypeToken<List<List<Integer>>>() {}));
  }

  @Test
  public void testComplexifySet() throws Exception {
    assertEquals(
        Set.of(1, 2, 3),
        adapter.complexify(context, new int[] {1, 2, 3}, new TypeToken<Set<Integer>>() {}));
    assertEquals(
        Set.of(Set.of(5, 0), Set.of(1, 0, 2), Set.of(-3, 2, 4)),
        adapter.complexify(
            context,
            new int[][] {
              {5, 0, 0},
              {1, 0, 2},
              {-3, 2, 4}
            },
            new TypeToken<Set<Set<Integer>>>() {}));
  }

  @Test
  public void testComplexifyUseLinkedList() throws Exception {
    assertInstanceOf(
        LinkedList.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<LinkedList<Integer>>() {}));
  }

  @Test
  public void testComplexifyUseArrayDeque() throws Exception {
    assertInstanceOf(
        ArrayDeque.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<ArrayDeque<Integer>>() {}));
    assertInstanceOf(
        ArrayDeque.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<Deque<Integer>>() {}));
  }

  @Test
  public void testComplexifyUsePriorityQueue() throws Exception {
    assertInstanceOf(
        PriorityQueue.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<PriorityQueue<Integer>>() {}));
    assertInstanceOf(
        PriorityQueue.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<Queue<Integer>>() {}));
  }

  @Test
  public void testComplexifyUseTreeSet() throws Exception {
    assertInstanceOf(
        TreeSet.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<TreeSet<Integer>>() {}));
  }

  @Test
  public void testComplexifyUseLinkedHashSet() throws Exception {
    assertInstanceOf(
        LinkedHashSet.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<LinkedHashSet<Integer>>() {}));
  }

  @Test
  public void testComplexifyUseHashSet() throws Exception {
    assertInstanceOf(
        HashSet.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<HashSet<Integer>>() {}));
  }

  @Test
  public void testComplexifyUseStack() throws Exception {
    assertInstanceOf(
        Stack.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<Stack<Integer>>() {}));
  }

  @Test
  public void testComplexifyUseVector() throws Exception {
    assertInstanceOf(
        Vector.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<Vector<Integer>>() {}));
  }

  @Test
  public void testComplexifyUseArrayList() throws Exception {
    assertInstanceOf(
        ArrayList.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<ArrayList<Integer>>() {}));
    assertInstanceOf(
        ArrayList.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<List<Integer>>() {}));
    assertInstanceOf(
        ArrayList.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<Collection<Integer>>() {}));
    assertInstanceOf(
        ArrayList.class,
        adapter.complexify(context, new int[] {0}, new TypeToken<Iterable<Integer>>() {}));
  }
}
