package configs;

import dev.anhcraft.config.annotations.Configurable;

import java.util.Map;

@Configurable
public class RoleTable {
    public Map<String, UserGroup> groups;

    public boolean inheritable;
}
