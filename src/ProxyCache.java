
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import sun.misc.VM;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Elahe
 */
public class ProxyCache {

  private static int port = 7512;
  private static ServerSocket serverSocket;

  public static void main(String args[]) {
    new ProxyCache().begin();
  }

  public void begin() {
    try {
      serverSocket = new ServerSocket(port);

      Socket client = null;

      while (true) {
        client = serverSocket.accept();

        Socket server = null;
        HttpRequest request;
        HttpResponse response;

        BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        request = new HttpRequest(fromClient);

        System.out.println("making socket for URI: [" + request.URI + "]");
        server = new Socket(request.URI, 80);
        DataOutputStream toServer = new DataOutputStream(server.getOutputStream());

        toServer.writeBytes(request.toString());
        System.out.println("sent request to server: request is:\n" + request.toString() + "end of req----------");

        DataInputStream fromServer = new DataInputStream(server.getInputStream());
        response = new HttpResponse(fromServer);
        System.out.println("response from server: " + response + "end of response----------");
        //JOptionPane.showMessageDialog(null, response);

        save(response);
        System.out.println("response saved");

        DataOutputStream toClient = new DataOutputStream(client.getOutputStream());
        toClient.writeBytes(response.toString());   //send headers

        toClient.write(response.getBody());   //send body

        client.close();
        server.close();

      }

    } catch (Exception ex) {
      System.err.println("proxy: error!!!");
      ex.printStackTrace();

    }

  }

  private void save(HttpResponse response) {
    PrintStream printToFile = null;

    try {
      String fileName = "1.txt";
      File file = new File(fileName);

      int numOfBytes = response.getBody().length;
      printToFile = new PrintStream(file);

      printToFile.print(response.toString()); //header
      //printToFile.print(new String(response.getBody())); //body

    } catch (FileNotFoundException ex) {
      System.err.println("error in saving response!");
      Logger.getLogger(ProxyCache.class.getName()).log(Level.SEVERE, null, ex);

    } finally {
      printToFile.close();

    }
  }

}
