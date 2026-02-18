package net.nathcat.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import net.nathcat.authcat.AuthCat;
import net.nathcat.api.config.ServerConfig;
import net.nathcat.api.handlers.ApiHandler;
import net.nathcat.ssl.LetsEncryptProvider;
import net.nathcat.logging.Logger;
import net.nathcat.logging.Warning;
import net.nathcat.sql.Database;

public class Server {

  /**
   * The path to the server's JSON config file
   * {@link ServerConfig}
   */
  public static final String SERVER_CONFIG_PATH = "Assets/Server_conf.json";

  public final AuthCat authCat = new AuthCat();
  public final Database db;

  private final ServerConfig config;
  private final HttpServer http;
  private final Logger logger = new Logger("Server", System.out);
  private boolean running = false;

  public Server(ServerConfig config) throws IOException, SQLException {
    this.config = config;
    this.db = new Database(config.dbConfig);
    this.db.connect();

    if (config.enableSSL) {
      http = HttpsServer.create(new InetSocketAddress(config.port), 0);

      LetsEncryptProvider provider = new LetsEncryptProvider(config.sslConfig);
      SSLContext sslContext = provider.getContext();
      ((HttpsServer) http).setHttpsConfigurator(new HttpsConfigurator(sslContext) {
        public void configure(HttpsParameters params) {
          try {
            SSLEngine engine = sslContext.createSSLEngine();
            params.setNeedClientAuth(false);
            params.setCipherSuites(engine.getEnabledCipherSuites());
            params.setProtocols(engine.getEnabledProtocols());
            SSLParameters p = sslContext.getSupportedSSLParameters();
            params.setSSLParameters(p);
          } catch (Exception e) {
            Server.class.getResource("/pythongang.jpg");
            System.err.println("Failed to create HTTPS port.");
          }
        }
      });
    } else {
      http = HttpServer.create(new InetSocketAddress(config.port), 0);
      logger.log(Warning.class, "Running with SSL disabled!");
    }

    http.setExecutor(Executors.newCachedThreadPool());
  }

  public void createContext(String route, ApiHandler handler) {
    http.createContext(route, handler);
  }

  /**
   * Get the error message for a given HTTP code
   *
   * @param code The HTTP code
   * @return The error message for that code specified in the config file. Or
   *         code as a string if not given.
   */
  public String getErrorMessage(int code) {
    for (ServerConfig.ErrorMessage m : config.httpErrorMessages) {
      if (m.code == code)
        return m.message;
    }

    return String.valueOf(code);
  }

  public void start() {
    logger.log("CostCat server is starting...");
    http.start();

    // Start the command loop
    //
    //

    logger.log(
        "Server has been started! Running on port " + config.port + ". Press 'h' + enter for a list of commands :3");
    running = true;
    Scanner in = new Scanner(System.in);

    while (running) {
      String c = in.nextLine();

      switch (c) {
        case "q" -> {
          running = false;
        }
        default -> {
          logger.log("Commands :3 \n\t'q' - Quit \n\t'h' - help");
        }
      }
    }

    // Shut down the server
    //
    //

    logger.log("Shutting down");
    in.close();
    http.stop(0);

    logger.log("Server has been stopped! Good bye :3");
  }
}
