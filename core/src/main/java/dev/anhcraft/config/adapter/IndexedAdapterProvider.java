package dev.anhcraft.config.adapter;

import java.io.Serializable;
import java.util.*;
import org.jetbrains.annotations.NotNull;

/**
 * An indexed adapter provider is an enhanced version of {@link SimpleAdapterProvider} that partially indexes type
 * adapters for supertypes. For example, if there is a type adapter for a type, type adapters of its supertypes are
 * marked as unavailable unless there is one provided. The index order is deterministic.
 */
public class IndexedAdapterProvider extends SimpleAdapterProvider {
  public IndexedAdapterProvider(@NotNull LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters) {
    super(discoverAdapters(typeAdapters));
  }

  private static LinkedHashMap<Class<?>, TypeAdapter<?>> discoverAdapters(
      LinkedHashMap<Class<?>, TypeAdapter<?>> origin) {
    ClassGraph classGraph = new ClassGraph();
    for (Map.Entry<Class<?>, TypeAdapter<?>> e : origin.entrySet()) {
      classGraph.decorate(e.getKey(), e.getValue());
    }
    classGraph.fill();
    classGraph.decoration.remove(Serializable.class); // exclude to prevent wrong adapter selection
    return classGraph.decoration;
  }

  private static class ClassGraph {
    public LinkedHashSet<Class<?>> roots = new LinkedHashSet<>();
    public LinkedHashMap<Class<?>, TypeAdapter<?>> decoration = new LinkedHashMap<>();
    public LinkedHashMap<Class<?>, LinkedHashSet<Class<?>>> parentToChild = new LinkedHashMap<>();

    public void decorate(Class<?> node, TypeAdapter<?> value) {
      decoration.put(node, value);
      discover(node, new HashSet<>());
    }

    private void discover(Class<?> node, Set<Class<?>> visited) {
      if (visited.contains(node)) return;
      Set<Class<?>> nodes = new LinkedHashSet<>();
      Class<?> superclass = node.getSuperclass();
      if (superclass != null) {
        parentToChild.computeIfAbsent(superclass, k -> new LinkedHashSet<>()).add(node);
        nodes.add(superclass);
        discover(superclass, visited);
      }
      for (Class<?> inf : node.getInterfaces()) {
        parentToChild.computeIfAbsent(inf, k -> new LinkedHashSet<>()).add(node);
        nodes.add(inf);
        discover(inf, visited);
      }
      visited.add(node);
      if (nodes.isEmpty()) roots.add(node);
    }

    public void fill() {
      for (Class<?> root : roots) {
        decoration.put(root, decoration.get(root));
        fill(root, decoration.get(root));
      }
    }

    private void fill(Class<?> parent, TypeAdapter<?> value) {
      Set<Class<?>> children = parentToChild.get(parent);
      if (children == null) return;
      for (Class<?> child : children) {
        TypeAdapter<?> local = decoration.get(child);
        if (local != null) fill(child, local);
        else {
          decoration.put(child, value);
          fill(child, value);
        }
      }
    }
  }
}
