package net.nathcat.api;

import java.util.regex.Pattern;

/**
 * Stop the server
 *
 */
public class QuitCommand extends ServerCommand {
  public QuitCommand() {
    super(Pattern.compile("^q$"));
  }

  @Override
  public void run(Server instance) {
    instance.stop();
  }

  @Override
  public String helpMessage() {
    return "Stop the server.";
  }
}
