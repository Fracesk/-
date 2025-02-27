import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        int port = 55533;
        ServerSocket server = new ServerSocket(port);
        System.out.println("server将一直等待连接");
        Socket socket = server.accept();
        System.out.println("客户端已连接");
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
        String request = "getdatetime";
        out.print(request);
        String response = in.readLine();
        System.out.println("get message from client:" + response);
        socket.close();
    }
}