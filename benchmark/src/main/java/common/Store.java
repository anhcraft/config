package common;

import dev.anhcraft.config.meta.Optional;

import java.util.List;
import java.util.UUID;

public class Store {
  @Optional
  public List<Product> products = List.of();

  @Optional
  public List<Transaction> transactions = List.of();

  public static Store createDummy() {
    Store store = new Store();
    store.products = List.of(
      new Product("p1", "Cookie", 100.0),
      new Product("p2", "Chocolate Bar", 76.2),
      new Product("p3", "Ice Cream", 50.0),
      new Product("p4", "Strawberry Cake", 20.0),
      new Product("p5", "Cupcake", 15.2)
    );
    store.transactions = List.of(
      new Transaction("t1", UUID.randomUUID(), List.of(
        new Transaction.TransactionDetail("p1", 80, 10),
        new Transaction.TransactionDetail("p5", 15.2, 7)
      ).toArray(new Transaction.TransactionDetail[0])),
      new Transaction("t2", UUID.randomUUID(), List.of(
        new Transaction.TransactionDetail("p3", 45, 20)
      ).toArray(new Transaction.TransactionDetail[0])),
      new Transaction("t3", UUID.randomUUID(), List.of(
        new Transaction.TransactionDetail("p4", 20, 5),
        new Transaction.TransactionDetail("p2", 76.2, 4)
      ).toArray(new Transaction.TransactionDetail[0]))
    );
    return store;
  }
}
