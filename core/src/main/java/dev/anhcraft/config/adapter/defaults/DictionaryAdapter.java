package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.SettingFlag;
import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.context.Context;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DictionaryAdapter implements TypeAnnotator<Dictionary> {
  @Override
  public @Nullable Dictionary complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof Dictionary) {
      if (SettingFlag.has(
          ctx.getFactory().getDenormalizer().getSettings(), SettingFlag.Denormalizer.DEEP_CLONE))
        return Dictionary.copyOf((Dictionary) value, true);
      return (Dictionary) value;
    }
    return null;
  }
}
