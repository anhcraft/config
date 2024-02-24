package dev.anhcraft.config.context;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.adapter.TypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class Context implements TypeAdapter<Object> {
    private final ConfigFactory factory;
    private final Deque<Scope> scopes = new ArrayDeque<>();
    private String cachedPath;

    public Context(@NotNull ConfigFactory factory) {
        this.factory = factory;
    }

    public @NotNull ConfigFactory getFactory() {
        return factory;
    }

    public void enterScope(@NotNull Scope scope) {
        scopes.offerLast(scope);
        cachedPath = null;
    }

    public void exitScope() {
        scopes.pollLast();
        cachedPath = null;
    }

    public @NotNull Scope getScope(int backward) {
        if (backward < 0)
            throw new IllegalArgumentException("backward cannot be negative");
        if (backward >= scopes.size())
            throw new IndexOutOfBoundsException("backward must be smaller than size of scopes");
        if (backward == 0)
            return scopes.getLast();
        Iterator<Scope> it = scopes.descendingIterator();
        while (it.hasNext()) {
            Scope scope = it.next();
            if (backward-- == 0)
                return scope;
        }
        throw new IllegalStateException();
    }

    public int getScopeSize() {
        return scopes.size();
    }


    public @NotNull String getPath() {
        if (cachedPath == null) cachedPath = buildPath(PathType.FIELD, ".");
        return cachedPath;
    }

    public @NotNull String buildPath(@NotNull PathType type, @NotNull String pathSeparator) {
        StringBuilder sb = new StringBuilder();
        boolean wasProperty = false;
        for (Scope scope : scopes) {
            if (scope instanceof ElementScope) {
                sb.append('[').append(((ElementScope) scope).getIndex()).append(']');
            } else if (scope instanceof PropertyScope) {
                if (wasProperty)
                    sb.append(pathSeparator);
                PropertyScope ps = (PropertyScope) scope;
                switch (type) {
                    case FIELD:
                        sb.append(ps.getProperty().field().getName());
                        break;
                    case PRIMARY:
                        sb.append(ps.getProperty().name());
                        break;
                    case SETTING:
                        sb.append(ps.getSetting());
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
                wasProperty = true;
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return sb.toString();
    }

    @Override
    public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<Object> sourceType, @NotNull Object value) throws Exception {
        return factory.getNormalizer().normalize(ctx, sourceType, value);
    }

    @Override
    public @Nullable Object complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        return factory.getDenormalizer().denormalize(ctx, value, targetType);
    }
}
