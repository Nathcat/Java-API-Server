package net.nathcat.authcat.credentials;

public class CookieSet implements CredentialSet {
  public final String cookie;

  public CookieSet(String cookie) {
    this.cookie = cookie;
  }
}
