package net.nathcat.api.config;

import net.nathcat.sql.DBConfig;
import net.nathcat.ssl.configs.LetsEncryptConfig;

/**
 * Specifes config file format
 *
 */
public class ServerConfig {
  /**
   * Information required to represent a HTTP error message
   *
   */
  public class ErrorMessage {
    public int code;
    public String message;
  }

  public int port;
  public boolean enableSSL;
  public LetsEncryptConfig sslConfig;
  public DBConfig dbConfig;
  /**
   * Specify HTTP error messages in this array
   */
  public ErrorMessage[] httpErrorMessages;
}
