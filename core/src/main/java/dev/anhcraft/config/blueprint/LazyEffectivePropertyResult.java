package dev.anhcraft.config.blueprint;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
class LazyEffectivePropertyResult {
  private final ReflectSchemaScanner scanner;
  private final Class<?> type;

  private volatile ReflectSchemaScanner.PropertyScanResult propertyScanResult;

  LazyEffectivePropertyResult(ReflectSchemaScanner scanner, Class<?> type) {
    this.scanner = scanner;
    this.type = type;
  }

  public ReflectSchemaScanner.PropertyScanResult getPropertyListResult() {
    ReflectSchemaScanner.PropertyScanResult result = propertyScanResult;
    if (result == null) {
      synchronized (this) {
        result = propertyScanResult;
        if (result == null) propertyScanResult = result = scanEffectiveList();
      }
    }
    return result;
  }

  private ReflectSchemaScanner.PropertyScanResult scanEffectiveList() {
    Deque<Field> fieldDeque = new ArrayDeque<>();

    Class<?> clazz = type;
    while (clazz != null && clazz != Object.class) {
      Field[] fields = clazz.getDeclaredFields();
      for (int i = fields.length - 1; i >= 0; i--) {
        if (scanner.encapsulationEnforced && !canAccess(type, fields[i])) continue;
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
        if (scanner.encapsulationEnforced && !canAccess(type, methods[i])) continue;
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
