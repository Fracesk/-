package FinalSocket;

import java.io.*;
import java.net.Socket;

public class Consumer implements Serializable{
    protected int port;
    protected String tag;


    public Consumer(int port, String tag) {
        this.port = port;
        this.tag = tag;
    }

    public static void main(String[] args)  {
        try (Socket socket = new Socket("127.0.0.1",3001)){
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(new Consumer(3001, "long"));
            outputStream.flush();
            Message message = null;
            while(true){
                message = (Message) inputStream.readObject();
                if(Long.parseLong(message.content) == -1L){
                    outputStream.writeObject("I have complete!");
                    System.out.println("OK");
                    break;
                }
                consume(Long.parseLong(message.content));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    public static void consume(long value){
       isPrime(value);
        if(isPrime(value)){
            System.out.println(value + "is Prime");
        }
        else{
            System.out.println(value + "isn't Prime");
        }
    }

    public static boolean isPrime(long poll){
        for(int i = 2; i <= Math.sqrt(poll); i++){
            if(poll % i == 0) {
                return false;
            }
        }
        return true;
    }

}
