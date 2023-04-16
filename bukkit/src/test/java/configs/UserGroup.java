package configs;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.PostHandler;
import dev.anhcraft.config.annotations.Virtual;

@Configurable
public class UserGroup {
    @Virtual
    public String id;

    public String name;

    public String[] permissions;

    private String perm;

    @PostHandler
    private void handle(ConfigDeserializer deserializer) {
        if (permissions != null) perm = String.join(",", permissions);
    }

    public String getPerm() {
        return perm;
    }
}
