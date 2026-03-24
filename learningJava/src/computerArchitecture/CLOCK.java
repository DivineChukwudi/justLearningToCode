package computerArchitecture;

public class CLOCK {

    public static void main(String[] args) {

        int[] referenceString = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2};
        int frames = 3;

        int[] memory = new int[frames];
        int[] referenceBit = new int[frames];

        
        for (int i = 0; i < frames; i++) {
            memory[i] = -1;
            referenceBit[i] = 0;
        }

        int pointer = 0;
        int pageFaults = 0;

        long startTime = System.nanoTime();

        for (int page : referenceString) {

            boolean found = false;

            
            for (int i = 0; i < frames; i++) {
                if (memory[i] == page) {
                    referenceBit[i] = 1; 
                    found = true;
                    break;
                }
            }

            
            if (!found) {
                pageFaults++;

                while (true) {
                    
                    if (referenceBit[pointer] == 0) {
                        memory[pointer] = page;
                        referenceBit[pointer] = 1;
                        pointer = (pointer + 1) % frames;
                        break;
                    } else {
                        // Give second chance
                        referenceBit[pointer] = 0;
                        pointer = (pointer + 1) % frames;
                    }
                }
            }

            
            System.out.print("Memory: ");
            for (int i = 0; i < frames; i++) {
                System.out.print(memory[i] + "(" + referenceBit[i] + ") ");
            }
            System.out.println();
        }

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        System.out.println("\nClock Page Replacement Results");
        System.out.println("Total Page Faults: " + pageFaults);
        System.out.println("Execution Time (ns): " + executionTime);
    }
}