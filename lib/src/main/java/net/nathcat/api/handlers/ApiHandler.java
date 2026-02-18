package net.nathcat.api.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.nathcat.authcat.AuthResult;
import net.nathcat.authcat.User;
import net.nathcat.authcat.credentials.CookieSet;
import net.nathcat.api.Server;
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

  private static final Pattern authCookiePattern = Pattern.compile("\\s*AuthCat-SSO=(?<value>[^;]*);?");

  protected final Server server;
  protected final Logger logger;
  protected final String[] validMethods;

  protected ApiHandler(Server server, String loggerName, String[] validMethods) {
    this.server = server;
    this.logger = new Logger(loggerName, System.out);
    this.validMethods = validMethods;
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

  protected void writeOk(HttpExchange ex) throws IOException {
    String s = "OK";
    ex.sendResponseHeaders(200, s.length());
    OutputStream os = ex.getResponseBody();
    os.write(s.getBytes());
    os.close();
  }

  /**
   * Transform a URI query string into a map of parameters to values.
   * 
   * @param query The query string
   * @return A map of parameter names to their values within the query string.
   */
  private static Map<String, String> queryToMap(String query) {
    Map<String, String> res = new HashMap<>();

    for (String s : query.split("&")) {
      String[] pv = s.split("=");
      if (pv.length > 1) {
        res.put(pv[0], pv[1]);
      } else {
        res.put(pv[0], "");
      }
    }

    return res;
  }

  /**
   * Handles authentication with AuthCat before passing off to a sub handler
   *
   */
  @Override
  public void handle(HttpExchange ex) throws IOException {
    logger.log(ex.getRemoteAddress().toString() + " -> " + ex.getRequestMethod());

    Headers headers = ex.getRequestHeaders();

    if (ex.getRequestMethod().equals("OPTIONS")) {
      corsHeaders(headers);
      writeOk(ex);
      return;
    }

    if (!Arrays.stream(validMethods).anyMatch(ex.getRequestMethod()::equals)) {
      writeError(ex, 405);
      return;
    }

    String cookies = headers.getFirst("Cookie");
    if (cookies == null) {
      writeError(ex, 403);
      return;
    }

    Map<String, String> getParams = queryToMap(ex.getRequestURI().getQuery());

    // Find the auth cookie
    Matcher m = authCookiePattern.matcher(cookies);
    if (m.find()) {

      try {
        // Attempt authentication with authcat token
        AuthResult result = server.authCat.tryLogin(new CookieSet(m.group("value")));

        if (result.result) {
          // If authentication was successful, pass this to the sub handle method
          handle(ex, result.user, getParams);

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

  abstract public void handle(HttpExchange ex, User user, Map<String, String> getParams) throws IOException;

  /**
   * Called instead of <code>handle</code> when the HTTP method is OPTIONS.
   */
  public void corsHeaders(Headers headers) throws IOException {
    return;
  }
}
