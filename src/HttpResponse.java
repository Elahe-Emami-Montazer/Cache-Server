
import java.io.*;
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
public class HttpResponse {

  private final static int BUF_SIZE = 8192;
  //private final static int MAX_OBJECT_SIZE = 100000000;

  private byte[] body;
  private String statusLine = "";
  private String headers = "";
  private int length = 0;

  public HttpResponse(DataInputStream fromServer) {
    try {
      String line = fromServer.readLine();

      boolean gotStatusLine = false;
      while (line.length() != 0) {
        if (!gotStatusLine) {
          statusLine = line;
          System.out.println("response: statusLine: " + statusLine);
          gotStatusLine = true;

        } else {
          headers += line + "\r\n";

        }

        if ((line.startsWith("Content-length")) || (line.startsWith("Content-Length"))) {
          String[] tmp = line.split(" ");
          length = Integer.parseInt(tmp[1]);

          //System.out.println("response: length = " + length);
        }
        line = fromServer.readLine();

      }

    } catch (IOException ex) {
      System.err.println("Error reading headers from server: ");
      Logger.getLogger(HttpResponse.class.getName()).log(Level.SEVERE, null, ex);

    }

    try {
      int bytesRead = 0;
      if (length != -1)
        body = new byte[length];
      
      byte buf[] = new byte[BUF_SIZE];
      boolean loop = false;

      if (length == 0) {
        loop = true;
        body = new byte[1000000000];
      }

      System.out.println("***length: " + length);
      while ((bytesRead < length) || ((loop) && (fromServer.available()>0))) {
        //System.out.println("in while");
        int bRead = fromServer.read(buf);

        //System.arraycopy(buf, 0, body, bytesRead, bRead);
        for (int i = 0; i < bRead; i++) {
          body[bytesRead + i] = buf[i];

        }

        bytesRead += bRead;

      }
      length = bytesRead;
      System.out.println("length after body: " + length);

    } catch (IOException e) {
      System.out.println("Error reading response body: " + e);

    }

  }

  public byte[] getBody() {
    byte[] ret = new byte[length];
    ret = body;
    return ret;
  }

  @Override
  public String toString() {
    String res = "";
    res += statusLine + "\r\n";
    res += headers;
    res += "\r\n";

    return res;
  }

}
