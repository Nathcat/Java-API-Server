package net.nathcat.authcat;

public class AuthResult {
  public final boolean result;
  public final User user;

  protected AuthResult(boolean result) {
    this.result = result;
    this.user = null;
  }

  protected AuthResult(User user) {
    this.result = true;
    this.user = user;
  }
}
