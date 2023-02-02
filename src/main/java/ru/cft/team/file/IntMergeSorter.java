package ru.cft.team.file;

import ru.cft.team.sort.MergeSort;

/**
 * Class presenting merge sort for integer data
 * @author Evgeniy Lee
 */
public class IntMergeSorter extends MergeSorter<Integer> {

    public IntMergeSorter(MergeSort<Integer> sorter) {
        this.sorter = sorter;
    }

    @Override
    protected Integer convert(String s) {
        return Integer.parseInt(s);
    }

    @Override
    protected String convert(Integer integer) {
        return integer.toString();
    }

    @Override
    protected boolean isValid(String data) {
        try {
            convert(data);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
