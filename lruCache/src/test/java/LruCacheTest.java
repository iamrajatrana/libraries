import com.dummy.cache.Cache;
import com.dummy.cache.LRUCache;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class LruCacheTest {

    @Test
    void createCache_WhenGetSize_ThenIsEmpty() {
        LRUCache<String, String> cache = LRUCache.create(2);
        assertEquals(0, cache.size());
    }

    @Test
    public void addSomeDataToCache_WhenGetData_ThenIsEqualWithCacheElement() {
        LRUCache<String, String> lruCache = LRUCache.create(3);
        lruCache.put("1", "test1");
        lruCache.put("2", "test2");
        lruCache.put("3", "test3");
        assertEquals("test1", lruCache.get("1").get());
        assertEquals("test2", lruCache.get("2").get());
        assertEquals("test3", lruCache.get("3").get());
    }

    @Test
    public void addDataToCacheToTheNumberOfSize_WhenAddOneMoreData_ThenLeastRecentlyDataWillEvict() {
        LRUCache<String, String> lruCache = LRUCache.create(3);
        lruCache.put("1", "test1");
        lruCache.put("2", "test2");
        lruCache.put("3", "test3");
        lruCache.put("4", "test4");
        System.out.println("LRU Size :" + lruCache.size());
        assertFalse(lruCache.get("1").isPresent());
    }

    @Test
    public void runMultiThreadTask_WhenPutDataInConcurrentToCache_ThenNoDataLost() throws Exception {
        final int size = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        Cache<Integer, Integer> cache = LRUCache.create(size);
        CountDownLatch countDownLatch = new CountDownLatch(size);
        try {
            IntStream.range(0, 50).<Runnable>mapToObj(key -> () -> {
                cache.put(key, key);
                countDownLatch.countDown();
            }).forEach(executorService::submit);
            countDownLatch.await();
        } finally {
            executorService.shutdown();
        }
        assertEquals(cache.size(), size);
    }
}
