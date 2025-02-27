import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        int port = 3000;
        try ( ServerSocket serverSocket = new ServerSocket(port) ){
        System.out.println("socket is waiting for connection");
        while(true) {
            Socket socket = serverSocket.accept();
            executorService.submit(() -> {
                InputStream in = null;
                try {
                    in = socket.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                byte[] input = new byte[1024];
                while (true) {
                    int len = 0;
                    try {
                        len = in.read(input);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (len == -1) break;
                    String request = new String(input, 0, len, StandardCharsets.UTF_16LE);
                    String response = null;
                    if (request.equals("gettime")) {
                        try {
                            response = gettime(socket);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (request.startsWith("test ")) {
                        try {
                            response = test(socket, request);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (request.startsWith("auth ")) {
                        try {
                            response = auth(socket, request);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (request.startsWith("auth2 ")) {
                        try {
                            response = auth2(socket, request);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        response = "unknow";
                    }
                    OutputStream out = null;
                    try {
                        out = socket.getOutputStream();
                        out.write(response.getBytes(StandardCharsets.UTF_16LE));
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }
    public static String gettime(Socket socket) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String response = sdf.format(new Date());
        return response;
    }
    public static String test(Socket socket, String request) throws IOException {
        String response = request.substring(5).trim();
        return response;
    }
    public static String auth(Socket socket, String request) throws IOException{
        String response = request.substring(5).trim().equals("202225310323") ? "ok" : "error";
        return response;
    }
    public static String auth2(Socket socket, String request) throws IOException{
        String response = request.substring(5).trim().equals("202225310323,王梓杰") ? "ok" : "error";
        return response;
    }
}