package ru.cft.team.file;

import ru.cft.team.sort.MergeSort;

/**
 * Class presenting merge sort for string data
 * @author Evgeniy Lee
 */
public class StrMergeSorter extends MergeSorter<String> {

    public StrMergeSorter(MergeSort<String> sorter) {
        this.sorter = sorter;
    }

    @Override
    protected String convert(String s) {
        return s;
    }

    @Override
    protected boolean isValid(String data) {
        if ((data == null) || (data.isBlank()) || (data.contains(" "))) {
            return false;
        }
        return true;
    }
}
