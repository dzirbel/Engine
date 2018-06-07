package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a simple implementation of common list utilities.
 * Utilities provided by this class typically use binary searches to locate elements in sorted
 *  lists.
 */
public class ListUtil
{
    /**
     * Gets the index of the given element in the given list using a binary search.
     * The list is searched from the start of the list to the end, so this function is equivalent
     *  to the expression
     * <pre>
     * ListUtil.get(element, list, 0, list.size() - 1)
     * </pre>
     *
     * @param element - the element for which to search
     * @param list - the list in which to search
     * @return the index of the given element, or -1 if it does not exist in the list
     * @see #get(Comparable, ArrayList, int, int)
     */
    public static <T extends Comparable<T>> int get(T element, List<T> list)
    {
        return get(element, list, 0, list.size() - 1);
    }

    /**
     * Gets the index of the given element in the given list using a binary search.
     * The list is searched from the given start (low) to end (high) indexes.
     * Use {@link #get(Comparable, ArrayList)} to search the entire list.
     *
     * @param element - the element for which to search
     * @param list - the sorted list in which to search
     * @param low - the first index to search
     *  (i.e. use {@code 0} to search from the beginning of the list)
     * @param high - the last index to search
     *  (i.e. use {@code list.size() - 1} to search to the end of the list)
     * @return the index of the given element, or -1 if it does not exist in the list
     * @see #get(Comparable, ArrayList)
     */
    public static <T extends Comparable<T>> int get(T element, List<T> list, int low, int high)
    {
        int mid;
        while (high >= low)
        {
            mid = (low + high) / 2;
            int comparison = list.get(mid).compareTo(element);
            if (comparison > 0)         // mid > element
            {
                high = mid - 1;
            }
            else if (comparison < 0)    // mid < element
            {
                low = mid + 1;
            }
            else                        // mid = element
            {
                return mid;
            }
        }

        return -1;
    }

    /**
     * Determines whether the given list contains the given element using a binary search.
     * This is equivalent to the expression
     * <pre>
     * ListUtil.get(element, list) != -1
     * </pre>
     *
     * @param element - the element for which to search
     * @param list - the sorted list in which to search
     * @return true if the given element is contained in the given list, false otherwise
     * @see #get(Comparable, ArrayList)
     */
    public static <T extends Comparable<T>> boolean contains(T element, List<T> list)
    {
        return get(element, list) != -1;
    }

    /**
     * Gets the index at which to add the given element in the given list using a binary search.
     * The element is added at an index such that the list remains ordered in ascending order,
     *  but is not guaranteed to be added in any particular location when other elements have equal
     *  comparisons.
     * Note: this method does not add the element but returns the index at which it should be
     *  added.
     * <br>
     * <br>
     * Examples:<ul>
     *  <li>for the list (1,2,2,3,5) and element 2, the add index could be either 1, 2, or 3</li>
     *  <li>for the list (-1, 3, 10) and element 20, the add index is 3</li>
     * </ul>
     *
     * @param element - the element to be added
     * @param list - the sorted list to which the element will be added
     * @return the index at which to add the given element (as with
     *  {@code list.add(index, element)})
     */
    public static <T extends Comparable<T>> int getAddIndex(T element, List<T> list)
    {
        if (list.size() == 0)
        {
            return 0;
        }

        int low = 0;
        int high = list.size() - 1;
        int mid;

        while (high >= low)
        {
            mid = (low + high) / 2;
            int comparison = list.get(mid).compareTo(element);
            if (comparison > 0)         // mid > element
            {
                if (mid == 0)
                {
                    return mid;         // element < first element < all other elements
                                // thus, add at index 0
                }
                else if (list.get(mid - 1).compareTo(element) < 0)
                {
                    return mid;         // mid > element and mid - 1 < element
                                // thus, add at mid
                }
                high = mid - 1;
            }
            else if (comparison < 0)    // mid < element
            {
                if (mid == list.size() - 1)
                {
                    return mid + 1;     // element > last element > all other elements
                                    // thus add at the last index (list.size())
                }
                else if (list.get(mid + 1).compareTo(element) > 0)
                {
                    return mid + 1;     // element > mid, mid + 1 > element
                                    // thus, add at mid + 1
                }
                low = mid + 1;
            }
            else
            // mid = element
            {
                return mid;
            }
        }

        return -1; // should never happen unless the list is not sorted
    }

    /**
     * Adds the given element to the given list such that the list remains ordered.
     * This is equivalent the statement
     * <pre>
     * list.add(ListUtil.getAddIndex(element, list), element)
     * </pre>
     *
     * @param element - the element to be added
     * @param list - the sorted list to which the element should be added
     */
    public static <T extends Comparable<T>> void add(T element, List<T> list)
    {
        list.add(getAddIndex(element, list), element);
    }

    /**
     * Removes the given element from the given list.
     * This is equivalent to the statement
     * <pre>
     * list.remove(element)
     * </pre>
     * but typically executes more quickly.
     *
     * @param element - the element to remove
     * @param list - the sorted list from which the element should be removed
     * @return true if the list contains the element and it was removed, false otherwise
     */
    public static <T extends Comparable<T>> boolean remove(T element, List<T> list)
    {
        int index = get(element, list);
        if (index == -1)
        {
            return false;
        }
        list.remove(index);
        return true;
    }

    /**
     * Finds a partition of the given list and sorts it.
     *
     * @param list - the list to be sorted
     * @param left - the left (lower) bound of the segment to sort
     * @param right - the right (upper) bound of the segment to sort
     * @return
     */
    private static <T extends Comparable<T>> int partition(List<T> list, int left, int right)
    {
        int i = left, j = right;
        T temp;
        T pivot = list.get((left + right)/2);

        while (i <= j)
        {
            while (list.get(i).compareTo(pivot) < 0)
            {
                i++;
            }
            while (list.get(j).compareTo(pivot) > 0)
            {
                j--;
            }
            if (i <= j)
            {
                temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
                i++;
                j--;
            }
        }

        return i;
    }

    /**
     * Sorts a segment of the given list using a recursive implementation of the quicksort
     *  algorithm.
     * For example, to sort the entire list use {@code 0} as the left boundary of the segment and
     *  {@code list.size() - 1} as the right boundary.
     *
     * @param list - the list to be sorted
     * @param left - the left (lower) boundary of the segment
     * @param right - the right (upper) boundary of the segment
     */
    public static <T extends Comparable<T>> void sort(List<T> list, int left, int right)
    {
        if (list.size() < 2)
        {
            return;
        }

        int index = partition(list, left, right);
        if (left < index - 1)
        {
            sort(list, left, index - 1);
        }
        if (index < right)
        {
            sort(list, index, right);
        }
    }

    /**
     * Sorts the given list using a recursive implementation of the quicksort algorithm.
     * This is equivalent to the statement
     * <pre>
     * ListUtil.sort(list, 0, list.size() - 1)
     * </pre>
     *
     * @param list - the list to be sorted
     */
    public static <T extends Comparable<T>> void sort(List<T> list)
    {
        sort(list, 0, list.size() - 1);
    }
}
