package dev.anhcraft.config.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
final class TypeImpl {

  static final class WildcardTypeImpl implements WildcardType {
    private final Type[] upperBounds;
    private final Type[] lowerBounds;

    public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
      this.upperBounds = upperBounds;
      this.lowerBounds = lowerBounds;
    }

    @Override
    public Type[] getUpperBounds() {
      return upperBounds;
    }

    @Override
    public Type[] getLowerBounds() {
      return lowerBounds;
    }
  }

  static final class GenericArrayTypeImpl implements GenericArrayType {
    private final Type componentType;

    public GenericArrayTypeImpl(Type componentType) {
      this.componentType = componentType;
    }

    @Override
    public Type getGenericComponentType() {
      return componentType;
    }
  }

  static final class ParameterizedTypeImpl implements ParameterizedType {
    private final Type ownerType;
    private final Type rawType;
    private final Type[] actualTypeArguments;

    public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... actualTypeArguments) {
      this.ownerType = ownerType;
      this.rawType = rawType;
      this.actualTypeArguments = actualTypeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
      return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
      return rawType;
    }

    @Override
    public Type getOwnerType() {
      return ownerType;
    }
  }
}
