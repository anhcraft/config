package dev.anhcraft.config.bukkit.adapter;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.SchemalessDictionary;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.type.SimpleTypes;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;

public class ConfigurationSerializableAdapter implements TypeAdapter<ConfigurationSerializable> {
  public static final ConfigurationSerializableAdapter INSTANCE = new ConfigurationSerializableAdapter();

  private SchemalessDictionary serialize(Context ctx, Map<String, Object> map) throws Exception {
    SchemalessDictionary dictionary = new SchemalessDictionary();
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      Object val = entry.getValue();
      if (val instanceof Map) {
        //noinspection unchecked
        val = serialize(ctx, (Map<String, Object>) val);
      } else if (!SimpleTypes.test(val)) {
        val = ctx.simplify(ctx, val.getClass(), val);
      }
      dictionary.put(entry.getKey().toLowerCase(), val);
    }
    return dictionary;
  }

  @Override
  public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<? extends ConfigurationSerializable> sourceType, @NotNull ConfigurationSerializable value) throws Exception {
    return serialize(ctx, value.serialize());
  }

  @Override
  public @Nullable ConfigurationSerializable complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof Dictionary)
      //noinspection unchecked
      return ConfigurationSerialization.deserializeObject(((Dictionary) value).unwrap(),
        (Class<? extends ConfigurationSerializable>) ComplexTypes.erasure(targetType));
    return null;
  }
}
