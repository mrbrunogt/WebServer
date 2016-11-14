import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WebServer{

  public static void main(String[] args) throws IOException{

    ServerSocket webserver = new ServerSocket(8000);

    Socket socket = webserver.accept();
      if(socket.isConnected()){
        System.out.println("The computer " +socket.getInetAddress() +"has connected to the server");

        BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Requisition: ");
        String line = buffer.readLine();
        String[] reqData = line.split(" ");
        String method = reqData[0];
        String filePath = reqData[1];
        String protocol = reqData[2];

        while(!line.isEmpty()){
            System.out.println(line);
            line = buffer.readLine();
        }

        if(filePath.equals("/")){
          filePath = "index.html";
        }

        File file = new File(filePath.replaceFirst("/", ""));

        String status = protocol +"200 OK \r\n";

        if(!file.exists()){
          status = protocol +"404 Not Found\r\n";
          file = new File("404.html");
        }

        byte[] content = Files.readAllBytes(file.toPath());

        SimpleDateFormat formater = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        String dateFormated = formater.format(date) +"GMT";
        String header = status
          +"Location: http://localhost:8000/\r\n"
          +"Date: " +dateFormated +"\r\n"
          +"Server: MyServer/1.0\r\n"
          +"Content-Type: text/html\r\n"
          +"Content-Length: " +content.length +"\r\n"
          +"Connection: close\r\n"
            +"\r\n";

        OutputStream response = socket.getOutputStream();
        response.write(header.getBytes());
        response.write(content);
        response.flush();
      }
  }
}
