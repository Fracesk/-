package FinalSocket;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
public class Producer {
    static int produceNumber = 0;
    static int max_number = 1000000;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("127.0.0.1",3000);
        Random producer = new Random();
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        while(true){
            outputStream.writeObject(produce(producer));
            if(produceNumber == max_number) {
                LocalDateTime currentTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                long production = -1;
                outputStream.writeObject(new Message(produceNumber,currentTime.format(formatter),"long",String.valueOf(production)));
                String reply;
                while(true){
                    reply = (String)(inputStream.readObject());
                    if( reply.equals("I have accepted") ){
                        break;
                    }
                }
                break;
            }
        }

    }

    public static Message produce(Random producer){
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        long production = 2_000_000_000L + (long)( producer.nextDouble()*(Long.MAX_VALUE- 2_000_000_000) );
        System.out.println(production);
        Message message = new Message(produceNumber + 1,currentTime.format(formatter),"long",String.valueOf(production));
        produceNumber++;
        return message;
    }

}
