package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Setting;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable(keyNamingStyle = Configurable.NamingStyle.TRAIN_CASE)
public class WorldMap {
    @Setting
    public Location centralLocation;

    @Setting
    public Map<String, Location> buildings = new HashMap<>();

    @Setting
    public List<Location> allHomes = new ArrayList<>();

    @Setting
    public Region centerRegions;

    @Configurable(keyNamingStyle = Configurable.NamingStyle.SNAKE_CASE)
    public static class Region {
        @Setting
        public List<Location> highlightedPos = new ArrayList<>();
    }
}
