package com.dummy.ratelimiter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.lang.Thread.sleep;

class Resource {
    public double getResource() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Math.random();
    }
}

public class BasicRateLimiter {

    public static void main(String[] args) throws InterruptedException {

        Resource resource = new Resource();

        RateLimiterRegistry registry = RateLimiterRegistry.of();
        RateLimiter rateLimiterService1 = registry.getRateLimiter("default1");
        RateLimiter rateLimiterService2 = registry.getRateLimiter("default2");

        while (true) {
            Supplier<Object> resourceSupplier1 = RateLimiter.decorate(rateLimiterService1, () -> resource.getResource());
            System.out.println("Resource S1: " + resourceSupplier1.get());

            Supplier<Object> resourceSupplier2 = RateLimiter.decorate(rateLimiterService2, () -> resource.getResource());
            System.out.println("Resource S2: " + resourceSupplier2.get());
        }

//        ExecutorService threadPool = Executors.newFixedThreadPool(4);
//        IntStream.of(1,2,3,4,5).forEach(e -> {
//            threadPool.submit(() -> {
//                Supplier<Object> resourceSupplier = RateLimiter.decorate(rateLimiter, () -> resource.getResource());
//                System.out.println("Resource : " + resourceSupplier.get());
//            });
//        });
    }

    static class RateLimiterRegistry {

        Map<String, RateLimiter> register = new HashMap<>();

        public static RateLimiterRegistry of() {
            return new RateLimiterRegistry();
        }

        public RateLimiter getRateLimiter(String service) {
            if (!register.containsKey(service)) {
                synchronized (this) {
                    if (!register.containsKey(service)) {
                        RateLimiter rateLimiter = RateLimiter.create(3, 10, TimeUnit.SECONDS);
                        register.put(service, rateLimiter);
                    }
                }
            }
            return register.get(service);
        }
    }

    static class RateLimiter {

        private int maxPermits;
        private Semaphore semaphore;
        private long timePeriod;
        private TimeUnit timeUnit;
        private ScheduledExecutorService scheduler;

        private RateLimiter(int permits, long timePeriod, TimeUnit timeUnit) {
            this.semaphore = new Semaphore(permits);
            this.maxPermits = permits;
            this.timePeriod = timePeriod;
            this.timeUnit = timeUnit;
        }

        public static RateLimiter create(int permits, long timePeriod, TimeUnit timeUnit) {
            RateLimiter limiter = new RateLimiter(permits, timePeriod, timeUnit);
            limiter.schedulePermitReplenishment();
            return limiter;
        }

        public void schedulePermitReplenishment() {
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleWithFixedDelay(() -> {
                semaphore.release(maxPermits - semaphore.availablePermits());
            }, 0, timePeriod, timeUnit);
        }

        public static Supplier decorate(RateLimiter rateLimiter, Supplier supplier) {
            return () -> {
                boolean hasAcquiredResource = rateLimiter.tryAcquire();
                if (hasAcquiredResource) {
                    rateLimiter.onSuccess();
                    Object response = supplier.get();
                    return Optional.of(response);
                } else {
                    rateLimiter.onFailure();
                }
                return Optional.empty();
            };
        }

        private boolean tryAcquire() {
//            System.out.println("Acquire: " + semaphore.availablePermits());
            return semaphore.tryAcquire();
        }

        private void release() {
//            System.out.println("Release: " + semaphore.availablePermits());
            semaphore.release();
        }

        public void stop() {
            scheduler.shutdownNow();
        }

        private void onSuccess() {
            System.out.println("Acquired Resource");
        }

        private void onFailure() {
            System.out.println("Failed to acquire Resource");
        }
    }
}
