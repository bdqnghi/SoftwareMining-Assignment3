import fj.Hash;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MyCounter {

    private static Map<String,AtomicInteger> counter = new HashMap<>();

    public static synchronized void increase(String key , int count) {
        if(!counter.containsKey(key)){
            counter.put(key,new AtomicInteger());
        }
        else{
            counter.get(key).incrementAndGet();
        }

    }

    public static synchronized void report(String key) {
        if(counter.get(key)!= null){
            System.out.println(counter.get(key) + "," + key);
        }
        counter.remove(key);
    }
}