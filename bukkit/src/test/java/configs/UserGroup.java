package configs;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.PostHandler;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Virtual;

@Configurable
public class UserGroup {
    @Setting
    @Virtual
    public String id;

    @Setting
    public String name;

    @Setting
    public String[] permissions;

    private String perm;

    @PostHandler
    private void handle(ConfigDeserializer deserializer) {
        perm = String.join(",", permissions);
    }

    public String getPerm() {
        return perm;
    }
}
