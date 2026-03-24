package computerArchitecture;
import java.util.*;

public class LRU {

    public static void main(String[] args) {

        int[] referenceString = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2};
        int frames = 3;

        HashMap<Integer, Integer> memory = new HashMap<>();
        LinkedList<Integer> usageOrder = new LinkedList<>();

        int pageFaults = 0;
        long startTime = System.nanoTime();

        for (int page : referenceString) {

            if (!memory.containsKey(page)) {
                pageFaults++;

                if (memory.size() == frames) {
                    int lruPage = usageOrder.removeFirst();
                    memory.remove(lruPage);
                }
            } else {
               
                usageOrder.remove((Integer) page);
            }
            
            memory.put(page, page);
            usageOrder.addLast(page);

            System.out.println("Memory: " + usageOrder);
        }
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        System.out.println("\nLRU Page Replacement Results");
        System.out.println("Total Page Faults: " + pageFaults);
        System.out.println("Execution Time (ns): " + executionTime);
    }
}