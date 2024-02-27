package dev.anhcraft.config.context;

import dev.anhcraft.config.ConfigFactory;
import java.util.function.Function;

/**
 * A function that creates a new {@link Context} compatible to the given {@link ConfigFactory}.
 */
public interface ContextProvider extends Function<ConfigFactory, Context> {}
