import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeResolver<T> {

    public final Type capture() {
        Type superclass = getClass().getGenericSuperclass();
        return ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }
}
