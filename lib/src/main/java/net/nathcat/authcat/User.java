package net.nathcat.authcat;

import net.nathcat.sql.DBType;

/**
 * Represents an AuthCat user type
 *
 */
public class User implements DBType {
  public int id;
  public String username;
  public String fullName;
  public String password;
  public String email;
  public String pfpPath;
  public int verified;

  public boolean isVerified() {
    return verified == 1;
  }
}
