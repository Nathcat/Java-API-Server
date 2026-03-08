package net.nathcat.api;

import net.nathcat.api.handlers.ApiHandler;

public final class ContextPair {
  public final Class<? extends ApiHandler> handler;
  public final String path;

  public ContextPair(String path, Class<? extends ApiHandler> handler) {
    this.handler = handler;
    this.path = path;
  }
}
