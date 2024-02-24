package dev.anhcraft.config.context;

/**
 * Represents a scope within an array.<br>
 * If the previous scope is a {@link PropertyScope}, it means the process is in a 1D array. However, if the previous
 * scope is an {@link ElementScope}, it means the process is in a multidimensional array.
 */
public class ElementScope implements Scope {
    private final int index;

    public ElementScope(int index) {
        if (index < 0)
            throw new IllegalArgumentException("index cannot be negative");
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
