package net.nathcat.api;

/**
 * A module which can be used by the server.
 * Contains all the functionality required for said module, and the required
 * information to interface with the server.
 *
 */
public interface Module {
  Class<? extends ServerCommand>[] getCommands();

  String basePath();

  ContextPair[] contexts();
}
