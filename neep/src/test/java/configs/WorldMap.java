package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Setting;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class WorldMap {
    @Setting
    public Location central;

    @Setting
    public Map<String, Location> buildings = new HashMap<>();

    @Setting
    public List<Location> homes = new ArrayList<>();
}
