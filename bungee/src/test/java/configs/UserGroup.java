package configs;

import dev.anhcraft.config.annotations.Configurable;

@Configurable
public class UserGroup {
    public String name;

    public String[] permissions;
}
