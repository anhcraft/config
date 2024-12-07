module dev.anhcraft.config {
  exports dev.anhcraft.config;
  exports dev.anhcraft.config.adapter;
  exports dev.anhcraft.config.blueprint;
  exports dev.anhcraft.config.context;
  exports dev.anhcraft.config.error;
  exports dev.anhcraft.config.meta;
  exports dev.anhcraft.config.type;
  exports dev.anhcraft.config.util;
  exports dev.anhcraft.config.validate;
  exports dev.anhcraft.config.internal.blueprint;

  requires jdk.unsupported;
  requires org.jetbrains.annotations;
}
