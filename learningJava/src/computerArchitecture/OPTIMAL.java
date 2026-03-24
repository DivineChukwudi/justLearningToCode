package computerArchitecture;

import java.util.*;

public class OPTIMAL {

    public static void main(String[] args) {

        int[] referenceString = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2};
        int frames = 3;

        ArrayList<Integer> memory = new ArrayList<>();
        int pageFaults = 0;

        long startTime = System.nanoTime();

        for (int i = 0; i < referenceString.length; i++) {

            int page = referenceString[i];

            
            if (memory.contains(page)) {
                System.out.println("Memory: " + memory);
                continue;
            }

            
            pageFaults++;

            
            if (memory.size() < frames) {
                memory.add(page);
            } else {
                int indexToReplace = -1;
                int farthest = i + 1;

                
                for (int j = 0; j < memory.size(); j++) {
                    int currentPage = memory.get(j);
                    int k;

                    for (k = i + 1; k < referenceString.length; k++) {
                        if (referenceString[k] == currentPage) {
                            break;
                        }
                    }

                    
                    if (k == referenceString.length) {
                        indexToReplace = j;
                        break;
                    }

                    
                    if (k > farthest) {
                        farthest = k;
                        indexToReplace = j;
                    }
                }

                memory.set(indexToReplace, page);
            }

            
            System.out.println("Memory: " + memory);
        }

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        System.out.println("\nOptimal Page Replacement Results");
        System.out.println("Total Page Faults: " + pageFaults);
        System.out.println("Execution Time (ns): " + executionTime);
    }
}