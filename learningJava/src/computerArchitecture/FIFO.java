package computerArchitecture;  

import java.util.LinkedList;
import java.util.Queue;

public class FIFO {

    public static void main(String[] args) {

        int[] referenceString = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2};
        int frames = 3;

        Queue<Integer> memory = new LinkedList<>();
        int pageFaults = 0;

        long startTime = System.nanoTime();

        for (int page : referenceString) {

            
            if (!memory.contains(page)) {
                pageFaults++;

                
                if (memory.size() == frames) {
                    memory.poll();
                }

                
                memory.add(page);
            }

            
            System.out.println("Memory: " + memory);
        }

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        System.out.println("\nFIFO Page Replacement Results");
        System.out.println("Total Page Faults: " + pageFaults);
        System.out.println("Execution Time (ns): " + executionTime);
    }
}