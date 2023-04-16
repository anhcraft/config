package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Validation;

import java.util.List;
import java.util.UUID;

@Configurable
public class PlayerInfo {
    @Description("The player")
    @Validation(notNull = true)
    public UUID player;

    @Description("The score points gained")
    public int points;

    public List<? extends String> homes;
}
