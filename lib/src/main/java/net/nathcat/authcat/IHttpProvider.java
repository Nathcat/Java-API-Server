package net.nathcat.authcat;

import java.util.Map;

import net.nathcat.sql.DBType;

public interface IHttpProvider {
  HttpResponse get(String uri, Map<String, String> headers);

  HttpResponse post(String uri, Map<String, String> headers, DBType body);
}
