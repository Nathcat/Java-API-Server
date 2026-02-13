package net.nathcat.logging;

public class Error implements ISeverity {
  /**
   * Formats for an info log. The message will be in italics, and red, and
   * prefixed with
   * "ERR: "
   *
   * @param msg The message to format
   * @return The formatted message
   */
  @Override
  public String format(String msg) {
    return "ERR: \033[3;31m" + msg + "\033[0m";
  }
}
