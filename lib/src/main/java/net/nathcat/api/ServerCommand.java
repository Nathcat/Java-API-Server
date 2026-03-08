package net.nathcat.api;

import java.util.regex.Pattern;

public abstract class ServerCommand {
  public final Pattern commandString;

  public ServerCommand(Pattern commandString) {
    this.commandString = commandString;
  }

  /**
   * Check if the input matches the command string syntax
   *
   * @param in The input
   */
  public boolean matches(String in) {
    return commandString.matcher(in).matches();
  }

  abstract void run(Server instance);

  abstract String helpMessage();
}
