package net.nathcat.authcat.credentials;

import net.nathcat.sql.DBType;

public class NamePassSet implements CredentialSet, DBType {
  public final String username;
  public final String password;

  public NamePassSet(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
