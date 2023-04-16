package configs;

import dev.anhcraft.config.annotations.Configurable;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class WorldMap {
    public Location central;

    public Map<String, Location> buildings = new HashMap<>();

    public List<Location> homes = new ArrayList<>();
}
