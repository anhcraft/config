package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;

import java.util.List;
import java.util.UUID;

@Configurable
public class PlayerInfo {
    @Setting
    @Description("The player")
    @Validation(notNull = true)
    public UUID player;

    @Setting
    @Description("The score points gained")
    public int points;

    @Setting
    public List<? extends String> homes;
}
