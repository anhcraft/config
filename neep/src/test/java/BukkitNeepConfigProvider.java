import dev.anhcraft.config.NeepConfigSection;
import dev.anhcraft.config.bukkit.BukkitConfigProvider;
import dev.anhcraft.config.struct.ConfigSection;
import org.jetbrains.annotations.NotNull;

public class BukkitNeepConfigProvider extends BukkitConfigProvider {
    public static final BukkitNeepConfigProvider INSTANCE = new BukkitNeepConfigProvider();

    @Override
    public @NotNull ConfigSection createSection() {
        return new NeepConfigSection();
    }
}
