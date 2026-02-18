package net.nathcat.authcat;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import net.nathcat.authcat.credentials.CookieSet;
import net.nathcat.authcat.credentials.NamePassSet;
import net.nathcat.authcat.exceptions.InvalidResponse;

public class AuthCat {
  private static class AuthResponse {
    public String status;
    public User user;
  }

  private static final String AUTHCAT_ENDPOINT = "https://data.nathcat.net/sso";

  private final IHttpProvider http;

  public AuthCat() {
    http = new SEHttpProvider();
  }

  public AuthCat(IHttpProvider http) {
    this.http = http;
  }

  public AuthResult tryLogin(NamePassSet credentials) throws InvalidResponse {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    Gson gson = new Gson();

    HttpResponse ac = http.post(AUTHCAT_ENDPOINT + "/try-login.php", headers, credentials);
    if (ac.code == 200) {
      AuthResponse response = gson.fromJson(ac.body, AuthResponse.class);
      if (response.status.equals("success"))
        return new AuthResult(response.user);
      else
        return new AuthResult(false);

    } else {
      throw new InvalidResponse("AuthCat returned code " + ac.code);
    }
  }

  public AuthResult tryLogin(CookieSet cookie) throws InvalidResponse {
    Gson gson = new Gson();
    Map<String, String> headers = new HashMap<>();
    headers.put("Cookie", "AuthCat-SSO=" + cookie.cookie);

    HttpResponse ac = http.get(AUTHCAT_ENDPOINT + "/get-session.php", headers);

    if (ac.code == 200) {
      if (ac.body.equals("[]"))
        return new AuthResult(false);
      else {
        AuthResponse response = gson.fromJson(ac.body, AuthResponse.class);
        return new AuthResult(response.user);
      }
    } else
      throw new InvalidResponse("AuthCat returned code " + ac.code);
  }
}
