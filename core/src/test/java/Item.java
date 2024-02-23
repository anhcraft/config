public class Item<T> {
    public Item(T value, int stack) {
        this.value = value;
        this.stack = stack;
    }

    public T value;
    public int stack;
}
