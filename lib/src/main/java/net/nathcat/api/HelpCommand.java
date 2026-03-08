package net.nathcat.api;

import java.util.regex.Pattern;

/**
 * Output information about all the currently registered server commands
 *
 */
public class HelpCommand extends ServerCommand {
  public HelpCommand() {
    super(Pattern.compile("^h$"));
  }

  @Override
  public void run(Server instance) {
    StringBuilder sb = new StringBuilder();
    sb.append("Commands :3");

    for (ServerCommand command : instance.getCommands()) {
      sb.append("\n\t" + command.commandString.toString() + "\n\t\t" + command.helpMessage());
    }

    instance.logger.log(sb.toString());
  }

  @Override
  public String helpMessage() {
    return "Help command. Displays information about the currently resgistered commands.";
  }
}
