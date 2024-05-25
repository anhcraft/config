package dev.anhcraft.config;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.context.*;
import dev.anhcraft.config.context.injector.PropertyDescriptionInjector;
import dev.anhcraft.config.meta.Describe;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ConfigNormalizerPropertyDescriptionTest {
  public static class ChatLog {
    public ChatLog(UUID sender, String message, long timestamp) {
      this.sender = sender;
      this.message = message;
      this.timestamp = timestamp;
    }

    @Describe("The sender of the message")
    private UUID sender;

    @Describe("The content")
    private String message;

    @Describe("The timestamp of the message")
    private long timestamp;
  }

  private static ConfigFactory factory;

  @BeforeAll
  public static void setUp() {
    factory =
        ConfigFactory.create()
            .provideContext(
                new ContextProvider() {
                  @Override
                  @NotNull public Context provideNormalizationContext(@NotNull ConfigFactory factory) {
                    return new InjectableContext(factory).inject(new PropertyDescriptionInjector());
                  }
                })
            .build();
  }

  @Test
  public void testNormalizeWithComment() throws Exception {
    var message = "Hello";
    var uuid = UUID.randomUUID();
    var time = System.currentTimeMillis();
    Dictionary dict =
        (Dictionary) factory.getNormalizer().normalize(new ChatLog(uuid, message, time));
    assertEquals(uuid.toString(), dict.get("sender"));
    assertEquals("The sender of the message", dict.get("#sender"));
    assertEquals(message, dict.get("message"));
    assertEquals("The content", dict.get("#message"));
    assertEquals(time, dict.get("timestamp"));
    assertEquals("The timestamp of the message", dict.get("#timestamp"));
  }
}
