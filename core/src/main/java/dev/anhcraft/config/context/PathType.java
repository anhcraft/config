package dev.anhcraft.config.context;

public enum PathType {
  /**
   * The path is made of field names.
   */
  FIELD,

  /**
   * The path is made of property's primary name.
   */
  PRIMARY,

  /**
   * The path is made of property's setting.<br>
   * Note: not recommended within denormalization context
   * @see PropertyScope#getSetting()
   */
  SETTING
}
