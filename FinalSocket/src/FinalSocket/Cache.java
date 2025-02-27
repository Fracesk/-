package FinalSocket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Cache implements Serializable {
    protected static BlockingQueue<Message> mes_cache = new ArrayBlockingQueue<>(100);
    protected static ArrayList<Consumer> subscribe_list = new ArrayList<>();
    protected static BlockingQueue<Message> input_txt = new ArrayBlockingQueue<>(10000);

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        clearFile();
        try (ServerSocket ProducerSercer = new ServerSocket(3000);
            ServerSocket ConsumerServer = new ServerSocket(3001)){
            System.out.println("Cache一直等待Producer连接！");
            while(true){
                Socket ProducerAccept = ProducerSercer.accept();
                Socket ConsumerAccept = ConsumerServer.accept();
                executorService.submit( () -> {
                    try {
                        System.out.println("与生产者和消费者连接成功");
                        ObjectInputStream produceReader = new ObjectInputStream(ProducerAccept.getInputStream());
                        ObjectOutputStream produceWriter = new ObjectOutputStream(ProducerAccept.getOutputStream());
                        ObjectInputStream consumeReader = new ObjectInputStream(ConsumerAccept.getInputStream());
                        ObjectOutputStream consumeWriter = new ObjectOutputStream(ConsumerAccept.getOutputStream());
                        //为消费者注册订阅
                        subscribe(consumeReader);
                        while(true){
                            accept_produce(produceReader);
                            Message consume = mes_cache.poll();
                            //每达到10000个数，就把这个数组给输入进文件里
                            /*
                            if(consume.mes_id % 10000 == 0 && consume.mes_id != 0) {
                                //以创建并清空文件的方式创建文件并写入
                                if (consume.mes_id == 10000) {
                                    try (FileOutputStream fileOut = new FileOutputStream("database.txt", false);
                                         ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                                        // 序列化对象数组
                                        objectOut.writeObject(consume);
                                        System.out.println("Objects written to file: " + (false ? "Appended" : "Overwritten"));
                                    }
                                } else {
                                    try (FileOutputStream fileOut = new FileOutputStream("database.txt", true);
                                         ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
                                        // 序列化对象数组
                                        objectOut.writeObject(consume);
                                        System.out.println("Objects written to file: " + (false ? "Appended" : "Overwritten"));
                                    }
                                }
                            }
                            */
                            for(Consumer i : subscribe_list){
                                if(i.tag.equals(consume.tag)){
                                    long number = Long.parseLong(consume.content);
                                    //如果消费品consume。content为-1
                                    if( number == -1L ){
                                        produceWriter.writeObject("I have accepted");
                                        produceWriter.flush();
                                        consumeWriter.writeObject(consume);
                                        consumeWriter.flush();
                                        String reply;
                                        while(true){
                                            reply = (String)consumeReader.readObject();
                                            if(reply.equals("I have complete!")){
                                                break;
                                            }
                                        }
                                        System.out.println("消费者发送来-1,已经完成任务！");
                                        break;
                                    }
                                    //否则，直接将消息体consume传给消费者
                                    else{
                                        consumeWriter.writeObject(consume);
                                    }
                                }
                            }

                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                break;
            }
        }catch (IOException e){
            throw new RuntimeException();
        }finally {
            executorService.shutdown();
        }

    }
    public static void subscribe(ObjectInputStream consumeReader) throws IOException, ClassNotFoundException {
        Consumer consumer = null;
        while(true){
            consumer = (Consumer) consumeReader.readObject();
            if(consumer!= null){
                break;
            }
            else{
                System.out.println("接受到null，继续等待");
            }
        }
        subscribe_list.add(consumer);
        for(Consumer i : subscribe_list){
            System.out.println("已注册消费者Port:" + i.port +"\tTag: " + i.tag);
        }
    }

    public static void accept_produce(ObjectInputStream produceReader) throws IOException, ClassNotFoundException, InterruptedException {
        Message message = null;
        while(true){
            //接收生产者的消息
            message = (Message) produceReader.readObject();
            if(message != null){
                break;
            }
            else{
                System.out.println("继续等待producer生产数据");
            }
        }
        mes_cache.put(message);
        if(input_txt.remainingCapacity() == 0){
            Thread inputThread = new Thread( () -> {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("database.txt", true))) {
                    while (!input_txt.isEmpty()) {
                        Message input = input_txt.poll();  // 获取并移除队列的元素
                        if (input != null) {
                            writer.write(input.toString());
                            writer.newLine();  // 每条消息换行
                        }
                    }
                    writer.flush();  // 刷新缓冲区，确保数据写入文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            inputThread.start();
            inputThread.join();
            Thread.sleep(500);
        }
        input_txt.put(message);
    }

    private static void clearFile() {
        try {
            File file = new File("database.txt");
            if (file.exists()) {
                // 如果文件存在，直接删除并重新创建
                file.delete();
            }
            // 重新创建一个新的空文件
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
