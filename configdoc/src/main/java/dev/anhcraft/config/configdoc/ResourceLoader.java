package dev.anhcraft.config.configdoc;

import dev.anhcraft.jvmkit.utils.IOUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class ResourceLoader {
  private final Map<String, String> resources = new HashMap<>();

  @NotNull public String get(String file) {
    return resources.computeIfAbsent(
        file,
        new Function<>() {
          @Override
          public String apply(String s) {
            try {
              return new String(IOUtil.readResource(getClass(), "/" + s), StandardCharsets.UTF_8);
            } catch (IOException e) {
              e.printStackTrace();
            }
            return null;
          }
        });
  }
}
