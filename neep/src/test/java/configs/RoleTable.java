package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Setting;

import java.util.Map;

@Configurable
public class RoleTable {
    @Setting
    public Map<String, UserGroup> groups;

    @Setting
    public boolean inheritable;
}
