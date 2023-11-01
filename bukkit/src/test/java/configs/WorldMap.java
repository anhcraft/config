package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Exclude;
import org.bukkit.Location;

import java.util.*;

@Configurable(keyNamingStyle = Configurable.NamingStyle.TRAIN_CASE)
public class WorldMap {
    @Exclude
    public Location centralLocation;

    public LinkedHashMap<String, Location> buildings = new LinkedHashMap<>();

    public List<Location> allHomes = new ArrayList<>();

    public Region centerRegions;

    @Configurable(keyNamingStyle = Configurable.NamingStyle.SNAKE_CASE)
    public static class Region {
        public List<Location> highlightedPos = new ArrayList<>();
    }
}
