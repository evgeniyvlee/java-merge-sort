package ru.cft.team.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record MergeSort<T>(Comparator<T> comparator) {

    public void sort(List<T> a) {
        int low = 0;
        int high = a.size() - 1;

        List<T> aux = new ArrayList<>(a);

        for (int m = 1; m <= high - low; m = 2 * m) {
            for (int i = low; i < high; i += 2 * m) {
                int start = i;
                int mid = Integer.min(i + m - 1, high);
                int end = Integer.min(i + 2 * m - 1, high);
                merge(a, aux, start, mid, end);
            }
        }
    }

    private void merge(List<T> a, List<T> aux, int start, int mid, int end) {
        int k = start, i = start, j = mid + 1;

        while (i <= mid && j <= end) {
            if (comparator.compare(a.get(i), a.get(j)) < 0) {
                aux.set(k++, a.get(i++));
            } else {
                aux.set(k++, a.get(j++));
            }
        }

        while (i <= mid) {
            aux.set(k++, a.get(i++));
        }

        for (i = start; i <= end; i++) {
            a.set(i, aux.get(i));
        }
    }
}
