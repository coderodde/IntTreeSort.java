package com.github.coderodde.util;

import java.util.Arrays;
import java.util.Random;

public class Benchmark {

    private static final int WARMUP_INT_ARRAY_LENGTH = 100_000;
    private static final int WARMUP_INT_UPPER_BOUND = 1_000;
    private static final int RANDOM_DATA_LENGTH = 1_000_000;
    private static final int DEGENERATE_DATA_LENGTH = 2 << 14;
    
    public static void main(String[] args) {
        final long seed = 1724998148175L; System.currentTimeMillis();
        System.out.println("seed = " + seed);
        final Random random = new Random(seed);
        
        System.out.println("Warming up...");
        
        long startTime = System.currentTimeMillis();
        warmup(random);
        long endTime = System.currentTimeMillis();
        
        System.out.printf("Warmed up in %d milliseconds.", endTime - startTime);
    
        System.out.println("Benchmarking on random data:");
        
        benchmarkOnRandomIntData(random);
        benchmarkOnDegenerateData(random);
    }
    
    private static void warmup(final Random random) {
        final int[] array1 = getWarmupArray(random);
        final int[] array2 = array1.clone();
        final int[] array3 = array1.clone();
        
        IntTreeSortV1.sort(array1);
        IntTreeSortV2.sort(array2);
        
        System.out.println();
    }
    
    private static int[] getWarmupArray(final Random random) {
        final int[] array = new int[WARMUP_INT_ARRAY_LENGTH];
        
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(WARMUP_INT_UPPER_BOUND);
        }
        
        return array;
    }
    
    private static int[] getRandomIntArray(final Random random) {
        final int[] array = new int[RANDOM_DATA_LENGTH];
        
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }
        
        return array;
    }
    
    
    private static void benchmarkOnRandomIntData(final Random random) {
        final int[] array1 = getRandomIntArray(random);
        final int[] array2 = array1.clone();
        
        long startTime = System.currentTimeMillis();
        
        IntTreeSortV1.sort(array1);
        
        long endTime = System.currentTimeMillis();
        
        System.out.printf("V1: %d milliseconds.\n",
                          endTime - startTime);
        
        startTime = System.currentTimeMillis();
        
        IntTreeSortV2.sort(array2);
        
        endTime = System.currentTimeMillis();
        
        System.out.printf("V2: %d milliseconds.\n",
                          endTime - startTime);
        
        System.out.printf("Algorithms agree: %b.\n", 
                          Arrays.equals(array1, array2));
        
        System.out.printf("V1 sorted: %b.\n", isSorted(array1));
        System.out.printf("V2 sorted: %b.\n", isSorted(array2));
        
        System.out.println();
    }
    
    private static boolean isSorted(final int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        
        return true;
    }
    
    private static void benchmarkOnDegenerateData(final Random random) {
        final int[] array1 = getDegenerateData(random);
        final int[] array2 = array1.clone();
        
        
        long startTime = System.currentTimeMillis();
        
        IntTreeSortV1.sort(array1);
        
        long endTime = System.currentTimeMillis();
        
        System.out.printf("V1: %d milliseconds.\n",
                          endTime - startTime);
        
        startTime = System.currentTimeMillis();
        
        IntTreeSortV2.sort(array2);
        
        endTime = System.currentTimeMillis();
        
        System.out.printf("V2: %d milliseconds.\n",
                          endTime - startTime);
        
        System.out.printf("Algorithms agree: %b.\n", 
                          Arrays.equals(array1, array2));
        
        System.out.printf("V1 sorted: %b.\n", isSorted(array1));
        System.out.printf("V2 sorted: %b.\n", isSorted(array2));
        
        System.out.println();
    }
    
    private static int[] getDegenerateData(final Random random) {
        
        final int[] array = new int[DEGENERATE_DATA_LENGTH];
        final int mask = ~(array.length - 1);
        
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt() & mask;
        }
        
        return array;
    }
}
