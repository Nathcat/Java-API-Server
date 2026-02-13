package net.nathcat.api.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.nathcat.authcat.AuthResult;
import net.nathcat.api.Server;
import net.nathcat.api.db.User;
import net.nathcat.logging.Error;
import net.nathcat.logging.Logger;
import net.nathcat.sql.Utils;

public abstract class ApiHandler implements HttpHandler {
  public static class SuccessResponse {
    public String status = "success";
  }

  public static class FailResponse {
    public String status = "fail";
    public String message;

    public FailResponse(String m) {
      this.message = m;
    }
  }

  private static final Pattern authCookiePattern = Pattern.compile("\\s*AuthCat-SSO=(?<value>[^;]);");

  protected final Server server;
  protected final Logger logger;

  protected ApiHandler(Server server, String loggerName) {
    this.server = server;
    this.logger = new Logger(loggerName, System.out);
  }

  protected void writeError(HttpExchange ex, int code) throws IOException {
    String msg = server.getErrorMessage(code);
    ex.sendResponseHeaders(code, msg.length());
    OutputStream os = ex.getResponseBody();
    os.write(msg.getBytes());
    os.close();
  }

  protected void writeJson(HttpExchange ex, Object obj) throws IOException {
    Gson gson = new Gson();
    String json = gson.toJson(obj);
    ex.sendResponseHeaders(200, json.length());
    OutputStream os = ex.getResponseBody();
    os.write(json.getBytes());
    os.close();
  }

  /**
   * Handles authentication with AuthCat before passing off to a sub handler
   *
   */
  @Override
  public void handle(HttpExchange ex) throws IOException {
    Headers headers = ex.getRequestHeaders();
    String cookies = headers.getFirst("Cookie");
    if (cookies == null) {
      writeError(ex, 403);
      return;
    }

    // Find the auth cookie
    Matcher m = authCookiePattern.matcher(cookies);
    if (m.find()) {
      try {
        // Attempt authentication with authcat token
        AuthResult result = server.authCat.loginWithCookie(m.group("value"));
        if (result.result) {
          // If authentication was successful, pass this to the sub handle method
          User user = Utils.mapToDBType(result.user, User.class);
          handle(ex, user);

        } else {
          writeError(ex, 403);
          return;
        }
      } catch (Exception e) {
        writeError(ex, 500);
        logger.log(Error.class, e.toString());
      }
    } else {
      writeError(ex, 403);
      return;
    }
  }

  abstract public void handle(HttpExchange ex, User user) throws IOException;
}
