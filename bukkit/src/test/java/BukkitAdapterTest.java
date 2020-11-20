import configs.WorldMap;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.bukkit.adapters.LocationAdapter;
import dev.anhcraft.config.bukkit.struct.YamlConfigSection;
import dev.anhcraft.config.struct.ConfigSection;
import org.bukkit.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Consumer;

public class BukkitAdapterTest extends TestPlatform {
    @Test
    public void location1() throws Exception {
        WorldMap wm = new WorldMap();
        wm.central = new Location(null, 0.01, 50, 0.01);
        wm.homes.add(new Location(null, 34.05, 50, -128.72));
        wm.homes.add(new Location(null, 65.12, 57.3, 98.203));
        wm.homes.add(new Location(null, -111.77, 63, -27.1));
        wm.buildings.put("Market", new Location(null, 90.4, 60, 23.32));
        wm.buildings.put("Park", new Location(null, -32, 55, 40));
        Consumer<ConfigSerializer> cb = (serializer) -> {
            LocationAdapter adapter = new LocationAdapter();
            adapter.inlineSerialization(true);
            serializer.registerTypeAdapter(Location.class, adapter);
        };
        ConfigSection cs = Objects.requireNonNull(serialize(WorldMap.class, wm, cb).asSection());
        wm = deserialize(WorldMap.class, ((YamlConfigSection) cs).getBackend());
        ConfigSection configSection2 = Objects.requireNonNull(serialize(WorldMap.class, wm, cb).asSection());
        Assertions.assertEquals(cs.stringify(), configSection2.stringify());
    }

    @Test
    public void location2() throws Exception {
        WorldMap wm = new WorldMap();
        wm.homes.add(new Location(null, -111.77, 40.32, -27.1));
        wm.homes.add(new Location(null, 108, 37, -10.2));
        wm.homes.add(new Location(null, 72, 32.5, 91));
        wm.buildings.put("Tower", new Location(null, 96, 60.2, 87));
        wm.buildings.put("Park", new Location(null, -32, 55, 40));
        Consumer<ConfigSerializer> cb = (serializer) -> {
            LocationAdapter adapter = new LocationAdapter();
            adapter.inlineSerialization(false);
            serializer.registerTypeAdapter(Location.class, adapter);
        };
        ConfigSection cs = Objects.requireNonNull(serialize(WorldMap.class, wm, cb).asSection());
        wm = deserialize(WorldMap.class, ((YamlConfigSection) cs).getBackend());
        ConfigSection configSection2 = Objects.requireNonNull(serialize(WorldMap.class, wm, cb).asSection());
        Assertions.assertEquals(cs.stringify(), configSection2.stringify());
    }
}
