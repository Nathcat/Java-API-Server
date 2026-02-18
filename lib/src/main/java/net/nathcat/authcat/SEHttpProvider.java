package net.nathcat.authcat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.google.gson.Gson;

import net.nathcat.sql.DBType;

public class SEHttpProvider implements IHttpProvider {

  @Override
  public net.nathcat.authcat.HttpResponse get(String uri, Map<String, String> headers) {
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest.Builder b = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .GET();

      for (String key : headers.keySet()) {
        b.setHeader(key, headers.get(key));
      }

      HttpRequest request = b.build();

      HttpResponse<String> r = client.send(request, HttpResponse.BodyHandlers.ofString());

      return new net.nathcat.authcat.HttpResponse(r.statusCode(), r.body());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public net.nathcat.authcat.HttpResponse post(String uri, Map<String, String> headers, DBType body) {
    try {
      Gson gson = new Gson();

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest.Builder b = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)));

      for (String key : headers.keySet()) {
        b.setHeader(key, headers.get(key));
      }

      HttpRequest request = b.build();

      HttpResponse<String> r = client.send(request, HttpResponse.BodyHandlers.ofString());

      return new net.nathcat.authcat.HttpResponse(r.statusCode(), r.body());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
