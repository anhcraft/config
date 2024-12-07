package dev.anhcraft.config.internal.blueprint;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class LazyEffectivePropertyResult {
  private final ReflectSchemaScannerImpl scanner;
  private final Class<?> type;
  private final boolean encapsulationEnforced;

  private volatile ReflectSchemaScannerImpl.PropertyScanResult propertyScanResult;

  public LazyEffectivePropertyResult(
      ReflectSchemaScannerImpl scanner, Class<?> type, boolean encapsulationEnforced) {
    this.scanner = scanner;
    this.type = type;
    this.encapsulationEnforced = encapsulationEnforced;
  }

  public ReflectSchemaScannerImpl.PropertyScanResult getPropertyListResult() {
    ReflectSchemaScannerImpl.PropertyScanResult result = propertyScanResult;
    if (result == null) {
      synchronized (this) {
        result = propertyScanResult;
        if (result == null) propertyScanResult = result = scanEffectiveList();
      }
    }
    return result;
  }

  private ReflectSchemaScannerImpl.PropertyScanResult scanEffectiveList() {
    Deque<Field> fieldDeque = new ArrayDeque<>();

    Class<?> clazz = type;
    while (clazz != null && clazz != Object.class) {
      Field[] fields = clazz.getDeclaredFields();
      for (int i = fields.length - 1; i >= 0; i--) {
        if (encapsulationEnforced && !canAccess(type, fields[i])) continue;
        fieldDeque.addFirst(fields[i]);
      }
      clazz = clazz.getSuperclass();
    }

    return scanner.scanPropertyList(
        fieldDeque,
        () -> scanner.scanNormalizers(collectMethods()),
        () -> scanner.scanDenormalizers(collectMethods()));
  }

  private Collection<Method> collectMethods() {
    Deque<Method> methodDeque = new ArrayDeque<>();

    Class<?> clazz = type;
    while (clazz != null && clazz != Object.class) {
      Method[] methods = clazz.getDeclaredMethods();
      for (int i = methods.length - 1; i >= 0; i--) {
        if (encapsulationEnforced && !canAccess(type, methods[i])) continue;
        methodDeque.addFirst(methods[i]);
      }
      clazz = clazz.getSuperclass();
    }

    return methodDeque;
  }

  private static boolean canAccess(Class<?> clazz, Field field) {
    if (Modifier.isPrivate(field.getModifiers())) return clazz == field.getDeclaringClass();
    return true;
  }

  private static boolean canAccess(Class<?> clazz, Method method) {
    if (Modifier.isPrivate(method.getModifiers())) return clazz == method.getDeclaringClass();
    return true;
  }
}
