package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Setting;

@Configurable
public class UserGroup {
    @Setting
    public String name;

    @Setting
    public String[] permissions;
}
