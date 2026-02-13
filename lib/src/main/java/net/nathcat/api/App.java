package net.nathcat.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import com.google.gson.JsonSyntaxException;

import net.nathcat.api.config.ServerConfig;

public class App {
  public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException {
    Gson gson = new Gson();
    ServerConfig config = gson.fromJson(new InputStreamReader(new FileInputStream(new File(Server.SERVER_CONFIG_PATH))),
        ServerConfig.class);

    Server server = new Server(config);
    server.start();
  }
}
