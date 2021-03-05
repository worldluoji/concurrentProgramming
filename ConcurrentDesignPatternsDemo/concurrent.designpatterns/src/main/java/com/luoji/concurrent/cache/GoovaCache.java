package com.luoji.concurrent.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class GoovaCache {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Cache<String, String> c = CacheBuilder.newBuilder()
                                    .maximumSize(1000)
                                    .expireAfterAccess(5, TimeUnit.SECONDS)
                                    .build();
        c.put("loginUser", "luojiayi");
        System.out.println(c.getIfPresent("loginUser"));
        Thread.sleep(5000);
        System.out.println(c.getIfPresent("loginUser"));
    }
}
