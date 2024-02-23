package dev.anhcraft.config.context;

public class ElementScope implements Scope {
    private final int index;

    public ElementScope(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
