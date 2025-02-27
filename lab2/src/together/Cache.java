package together;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Cache {
    protected BlockingQueue<Long> cache;

    public Cache(int max_size) {
        this.cache = new ArrayBlockingQueue<>(max_size);
    }
}
