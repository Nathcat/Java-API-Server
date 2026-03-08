package net.nathcat.api;

import net.nathcat.api.handlers.ApiHandler;

public final class ContextPair {
  public final ApiHandler handler;
  public final String path;

  public ContextPair(String path, ApiHandler handler) {
    this.handler = handler;
    this.path = path;
  }
}
