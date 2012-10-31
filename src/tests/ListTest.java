package tests;

import java.util.ArrayList;

import utils.ListUtil;

/**
 * Tests functions of the {@link ListUtil}.
 * 
 * @author zirbinator
 */
public class ListTest
{
    /**
     * Runs the list test.
     * 
     * @param args - command-line arguments, ignored
     */
    public static void main(String[] args)
    {
        new ListTest().test();
    }
    
    /**
     * Runs a test of the functions in {@link ListUtil}.
     */
    public void test()
    {
        int count = 100000;
        ArrayList<Integer> nums = new ArrayList<Integer>();
        
        // add random integers without sorting
        long before = System.nanoTime();;
        for (int i = 0; i < count; i++)
        {
            nums.add(new Integer((int) (Math.random()*100)));
        }
        long after = System.nanoTime();
        
        System.out.println("Normal adding: " + (after - before)/1000000);
        
        // sort the random, unsorted integers
        before = System.nanoTime();
        ListUtil.sort(nums);
        after = System.nanoTime();
        
        System.out.println("Sorting: " + (after - before)/1000000);
        
        // check the sorting
        for (int i = 0; i < nums.size() - 1; i++)
        {
            if (nums.get(i) > nums.get(i + 1))
            {
                System.out.println("SORT ERROR");
            }
        }
        
        nums.clear();
        
        // add random integers while sorting them
        before = System.nanoTime();
        for (int i = 0; i < count; i++)
        {
            ListUtil.add(new Integer((int) (Math.random()*100)), nums);
        }
        after = System.nanoTime();
        
        System.out.println("Sorted adding: " + (after - before)/1000000);
        
        // check the sorting
        for (int i = 0; i < nums.size() - 1; i++)
        {
            if (nums.get(i) > nums.get(i + 1))
            {
                System.out.println("SORT ERROR");
            }
        }
        
        // get random integers
        before = System.nanoTime();
        for (int i = 0; i < count; i++)
        {
            Integer toFind = new Integer((int) (Math.random()*100));
            if (ListUtil.get(toFind, nums) == -1)
            {
                System.out.println("Not Found: " + toFind);
            }
        }
        
        for (int i = 0; i < count; i++)
        {
            Integer toFind = new Integer((int) (Math.random()*100) + 150);
            if (ListUtil.get(toFind, nums) != -1)
            {
                System.out.println("Erroneously Found: " + toFind);
            }
        }
        
        after = System.nanoTime();
        
        System.out.println("Getting: " + (after - before)/1000000);
    }
}
