package together;

import java.util.Queue;
import java.util.Random;
public class Producer implements Runnable{
    protected Cache cacher;
    protected Random producer;

    static int produceNumber = 0;
    static int max_number = 10_000_00;
    public Producer(Cache cacher) {
        this.cacher = cacher;
        producer = new Random();
    }

    public void produce(){
        long production = 2_000_000_000L + (long)( producer.nextDouble()*(Long.MAX_VALUE- 2_000_000_000) );
        cacher.cache.add(production);
        produceNumber++;
    }

    @Override
    public void run() {
        //long start = System.nanoTime();
        while(true){
            try{
                synchronized (cacher){
                    while(cacher.cache.remainingCapacity() == 0){
                        cacher.wait();
                    }
                    if(produceNumber >= max_number){
                        cacher.cache.put(-1L);
                        break;
                    }
                    produce();
                    cacher.notifyAll();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        //long end = System.nanoTime();
        //System.out.println("Produce:" + (end - start) / 1_000_000 + "ms");
    }
}
