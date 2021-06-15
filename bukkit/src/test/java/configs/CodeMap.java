package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Consistent;
import dev.anhcraft.config.annotations.Setting;

import java.util.HashMap;
import java.util.Map;

@Configurable
public class CodeMap {
    @Setting
    public Map<Character, String> table1 = new HashMap<>();

    @Setting
    public Map<Byte, String> table2 = new HashMap<>();

    @Setting
    @Consistent
    public String[] reserved;
}
