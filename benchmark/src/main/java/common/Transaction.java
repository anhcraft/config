package common;

import dev.anhcraft.config.meta.Denormalizer;
import dev.anhcraft.config.meta.Exclude;
import dev.anhcraft.config.meta.Validate;

import java.util.UUID;

public class Transaction {
  public Transaction(String id, UUID customer, TransactionDetail[] details) {
    this.id = id;
    this.customer = customer;
    this.details = details;
  }

  @Validate("not-null")
  public String id;

  @Validate("not-null")
  public UUID customer;

  @Exclude
  public double totalWorth;

  @Validate("not-null, size=1|")
  public TransactionDetail[] details;

  @Denormalizer(value = "details", strategy = Denormalizer.Strategy.AFTER)
  private void setTotalWorth(TransactionDetail[] details) {
    this.totalWorth = 0;
    for (TransactionDetail detail : details) {
      this.totalWorth += detail.worth * detail.amount;
    }
  }

  public static class TransactionDetail {
    public TransactionDetail(String product, double worth, int amount) {
      this.product = product;
      this.worth = worth;
      this.amount = amount;
    }

    @Validate("not-null")
    public String product;

    @Validate("range=0|")
    public double worth;

    @Validate("range=1|")
    public int amount;
  }
}
