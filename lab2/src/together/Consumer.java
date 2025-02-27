package together;

public class Consumer implements Runnable{
    protected Cache cacher;

    public Consumer(Cache cacher) {
        this.cacher = cacher;
    }

    public void consume(long value){
//       isPrime(value);
        if(isPrime(value)){
            System.out.println(value + "Yes");
        }
        else{
            System.out.println(value + "No");
        }
    }

    public boolean isPrime(long poll){
        for(int i = 2; i <= Math.sqrt(poll); i++){
            if(poll % i == 0) {
                return false;
            }
        }
        return true;
    }
    @Override
    public void run() {
        long start = System.nanoTime();
        while (true) {
            try{
                synchronized (cacher){
                    while(cacher.cache.isEmpty()){
                        cacher.wait();
                    }
                    long head = cacher.cache.peek();
                    if(head == -1L){
                        break;
                    }
                    consume(cacher.cache.poll());
                    cacher.notifyAll();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        long end = System.nanoTime();
        System.out.println( "Cosumer:" + (end - start) / 1_000_000 + "ms");
    }
}
