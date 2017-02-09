import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MyCounter {

    private static Map<String,AtomicInteger> counter = new HashMap<>();

    public static synchronized void increase(String key) {
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
    }

    public static synchronized void count_branching_executed() {
        int size = counter.size();
        int executed = 0;
        for(Map.Entry<String,AtomicInteger> entry : counter.entrySet()){
            if(entry.getValue().get() != 0){
                executed += 1;
            }
        }
        System.out.println("Executed : " + executed);
        System.out.println("Branch : " + size);
    }
}