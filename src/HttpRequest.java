
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Elahe
 */
public class HttpRequest {

  private String method;
  String URI;
  String version;
  private String headers = "";
  private String host = "";
  private int port = -1;

  public HttpRequest(BufferedReader from) {
    String firstLine = "";

    try {
      firstLine = from.readLine();
      System.out.println("FIRSTlINE: " + firstLine);
      
      String[] tmp = firstLine.split(" ");
      method = tmp[0];
      if (method.equalsIgnoreCase("connect")) {
        method = "GET";
      }
      URI = tmp[1];
      System.out.println("tmp[1]: " + URI);
      if (URI.startsWith("/")) {
        URI = tmp[1].substring(1);
      }
      
      if (URI.contains(":")) {
        URI = URI.substring(0, URI.indexOf(":"));
      }
      
      version = tmp[2];

      System.out.println("request: method: " + method + " URI is: " + URI + " version: " + version);
    } catch (Exception ex) {
      System.err.println("request: error in getting request firstLine.");
      Logger.getLogger(HttpRequest.class.getName()).log(Level.SEVERE, null, ex);

    }

    try {
      String line = from.readLine();
      while (line.length() != 0) {
        headers += line + "\r\n";

        if (line.startsWith("Host:")) {
          String[] tmp = line.split(" ");

          if (tmp[1].indexOf(':') > 0) {
            String[] tmp2 = tmp[1].split(":");
            host = tmp2[0];
            port = Integer.parseInt(tmp2[1]);

          } else {
            host = tmp[1];
            port = 80;

          }
        }
        line = from.readLine();

      }

    } catch (IOException e) {
      System.err.println("request: Error reading from socket: " + e);

    }
    //System.out.println("request: Host to contact is: " + host + " at port " + port);

  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  @Override
  public String toString() {
    String req = "";

    req = method + " " + "/" + " " + version + "\r\n";
    req += "Host: " + URI + "\r\n";
    //req += headers;

    req += "Connection: close" + "\r\n";
    req += "\r\n";

    return req;
  }

}
