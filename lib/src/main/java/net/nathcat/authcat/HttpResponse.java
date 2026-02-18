package net.nathcat.authcat;

public class HttpResponse {
  public final int code;
  public final String body;

  public HttpResponse(int code, String body) {
    this.code = code;
    this.body = body;
  }
}
