package com.dummy.ratelimiter;

public class TokenBucketLimiter {
    public static void main(String[] args) throws InterruptedException {

        BasicTokenBucket basicTokenBucket = new BasicTokenBucket(10, 10);
        basicTokenBucket.allowRequest(5);
        basicTokenBucket.allowRequest(3);
        basicTokenBucket.allowRequest(4);
        basicTokenBucket.allowRequest(4);
        Thread.sleep(800);
        basicTokenBucket.allowRequest(4);
        basicTokenBucket.allowRequest(4);

    }

    static class BasicTokenBucket {
        private final long maxBucketSize;
        private final long refillRate;

        private double currentBucketSize;
        private long lastRefillTimestamp;

        public BasicTokenBucket(long maxBucketSize, long refillRate) {
            this.maxBucketSize = maxBucketSize;
            this.refillRate = refillRate;

            this.currentBucketSize = maxBucketSize;
            this.lastRefillTimestamp = System.nanoTime();
        }

        public synchronized boolean allowRequest(int tokens) {
            refill();
            if(currentBucketSize > tokens) {
                currentBucketSize -= tokens;
                System.out.println("Success");
                return true;
            }
            System.out.println("Failed");
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            double tokensToAdd = (now-lastRefillTimestamp)*refillRate/1e9;
            currentBucketSize = Math.min(currentBucketSize+tokensToAdd, maxBucketSize);
            lastRefillTimestamp = now;
        }
    }

}
