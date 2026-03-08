package net.nathcat.api.exceptions;

import net.nathcat.api.ServerCommand;

public class CommandAlreadyRegistered extends RuntimeException {
  private final Class<? extends ServerCommand> c;

  public CommandAlreadyRegistered(Class<? extends ServerCommand> c) {
    this.c = c;
  }

  @Override
  public String toString() {
    return "The command " + this.c.getName() + " is already registered!";
  }
}
