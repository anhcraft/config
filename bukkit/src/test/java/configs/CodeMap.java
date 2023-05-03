package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Constant;
import dev.anhcraft.config.annotations.Optional;

import java.util.HashMap;
import java.util.Map;

@Configurable
public class CodeMap {
    public Map<Character, String> table1 = new HashMap<>();

    public Map<Byte, String> table2 = new HashMap<>();

    @Constant
    public String[] reserved;

    @Optional
    public String hash = "1001";
}
