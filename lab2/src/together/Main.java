package together;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int Produce_Number = 1_000_000;
        //实验一
        Game(1,1,Produce_Number);
        //实验二
        Game(1,2,Produce_Number);
        //实验三
        Game(1,4,Produce_Number);

    }
    public static void Game(int producerNumber, int consumerNumber,int produce_number) throws InterruptedException {
        Cache cacher = new Cache(1000);
        Producer.max_number = produce_number;
        Producer.produceNumber = 0;
        Thread[] producers = new Thread[producerNumber];
        Thread[] comsumers = new Thread[consumerNumber];

        long start_time = System.nanoTime();
        for(int i = 0; i < producerNumber; i++){
            producers[i] = new Thread(new Producer(cacher));
        }
        for(int i = 0; i < consumerNumber; i++){
            comsumers[i] = new Thread(new Consumer(cacher));
        }

        for(Thread p : producers){
            p.start();
        }
        for(Thread c : comsumers){
            c.start();
        }
        for(Thread p : producers){
            p.join();
        }
        for(Thread c : comsumers){
            c.join();
        }
        long end_time = System.nanoTime();
        System.out.println("Complete:" + (end_time - start_time) / 1_000_000 + "ms");

    }
}