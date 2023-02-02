package ru.cft.team;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cft.team.file.IntMergeSorter;
import ru.cft.team.file.MergeSorter;
import ru.cft.team.file.StrMergeSorter;
import ru.cft.team.sort.MergeSort;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Main class for calling marge sort
 * @author Evgeniy Lee
 */
public class Main {

    private static final Options options = new Options();
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final Comparator<String> stringComparator;
    private static final Comparator<Integer> integerComparator;

    static {
        options.addOption("a", false, "ascending sort");
        options.addOption("d", false, "descending sort");
        options.addOption("s", false, "string data");
        options.addOption("i", false, "number data");

        stringComparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };

        integerComparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        };
    }

    public static void main(String[] args) throws IOException {
        List<File> in = new ArrayList<>();
        CommandLineParser parser = new DefaultParser();
        MergeSorter sorter = null;

        try {
            CommandLine cmd = parser.parse(options, args);

            boolean isDescendingSorting = cmd.hasOption("d") && !cmd.hasOption("a");
            boolean isIntegerData = cmd.hasOption("i") && !cmd.hasOption("s");
            boolean isStringData = cmd.hasOption("s") && !cmd.hasOption("i");
            List<String> filenames = cmd.getArgList();

            if (isIntegerData) {
                if (isDescendingSorting) {
                    sorter = new IntMergeSorter(new MergeSort<Integer>(integerComparator.reversed()));
                } else {
                    sorter = new IntMergeSorter(new MergeSort<Integer>(integerComparator));
                }
            }

            if (isStringData) {
                if (isDescendingSorting) {
                    sorter = new StrMergeSorter(new MergeSort<String>(stringComparator.reversed()));
                } else {
                    sorter = new StrMergeSorter(new MergeSort<String>(stringComparator));
                }
            }

            File out = new File(filenames.get(0));

            for (int i = 1; i < filenames.size(); i++) {
                in.add(new File(filenames.get(i)));
            }

            if ((sorter != null) && !filenames.isEmpty() && !in.isEmpty()) {
                List<File> sortedFiles = sorter.sort(in);
                sorter.merge(sortedFiles, out);
            } else {
                log.error("Не заданы параметра запуска программы.");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
