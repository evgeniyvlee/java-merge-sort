package ru.cft.team.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cft.team.sort.MergeSort;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Class presenting merge sort
 * @param <T> type of data
 * @author Evgeniy Lee
 */
public abstract class MergeSorter<T> {

    protected MergeSort<T> sorter;
    private final static Logger log = LoggerFactory.getLogger(MergeSorter.class);
    private final static int MAX_TEMP_FILES = 1024;

    /**
     * Sort files
     * @param unsortedFiles list of files for sorting
     * @return list of sorted files
     */
    public List<File> sort(List<File> unsortedFiles) {
        List<File> sortedFiles = new ArrayList<>();
        long blockSize = 0;
        for (File file : unsortedFiles) {
            blockSize = calculateBlockSize(file);
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                List<T> dataBlock = new ArrayList<>();
                long currentBlockSize = 0;
                while (br.ready()) {
                    currentBlockSize = 0;
                    dataBlock.clear();
                    while ((currentBlockSize < blockSize) && (br.ready())) {
                        String data = br.readLine();
                        if (isValid(data)) {
                            dataBlock.add(convert(data));
                            currentBlockSize += data.length();
                        } else {
                            log.warn("Неверный формат данных: " + data);
                        }
                    }
                    sorter.sort(dataBlock);
                    File sortedFile = write(dataBlock);
                    sortedFiles.add(sortedFile);
                }
            } catch (Exception e) {
                log.error("Ошибка чтения файла " + file.getName());
            }
        }
        return sortedFiles;
    }

    /**
     * Merge sorted files
     * @param sortedFiles list of sorted files
     * @param out result file
     * @throws IOException if error occurs
     */
    public void merge(List<File> sortedFiles, File out) throws IOException {

        PriorityQueue<SortedFile> queue = new PriorityQueue(
            sortedFiles.size(),
            (Comparator<SortedFile>) (o1, o2) -> sorter.comparator().compare(convert(o1.data), convert(o2.data))
        );

        for (File file : sortedFiles) {
            SortedFile sortedFile = new SortedFile(file);
            queue.add(sortedFile);
        }

        try (BufferedWriter fbw = new BufferedWriter(new FileWriter(out))) {
            while(queue.size() > 0) {
                SortedFile buffer = queue.poll();
                T element = convert(buffer.pop());
                fbw.write(convert(element));
                fbw.newLine();

                if(buffer.isEmpty) {
                    buffer.br.close();
                    buffer.file.delete();
                } else {
                    queue.add(buffer);
                }
            }
        } finally {
            for(SortedFile bfb : queue ) bfb.close();
        }
    }

    protected abstract T convert(String s);

    protected abstract String convert(T t);

    protected abstract boolean isValid(String data);

    private File write(List<T> dataBlock) {
        File tmpFile = null;
        BufferedWriter bw = null;

        try {
            tmpFile = File.createTempFile("mergeSort", "file");
            bw = new BufferedWriter(new FileWriter(tmpFile));
            for (T element : dataBlock) {
                bw.write(convert(element));
                bw.newLine();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            close(bw);
        }
        return tmpFile;
    }

    private void close(BufferedWriter bw) {
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    private long calculateBlockSize(File file) {
        long blockSize = file.length() / MAX_TEMP_FILES;
        long freeMemory = Runtime.getRuntime().freeMemory();
        if (blockSize < freeMemory / 2) {
            blockSize = freeMemory / 2;
        } else {
            if (blockSize >= freeMemory) {
                log.error("Возможно переполнение памяти.");
            }
        }
        return blockSize;
    }
}

class SortedFile {
    File file;
    BufferedReader br;
    boolean isEmpty;
    String data;

    public SortedFile(File file) throws IOException {
        this.file = file;
        br = new BufferedReader(new FileReader(file));
        read();
    }

    private void read() throws IOException {
        if (br.ready()) {
            data = br.readLine();
            isEmpty = false;
        } else {
            isEmpty = true;
            data = null;
        }
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void close() throws IOException {
        if (br != null) {
            br.close();
        }
    }

    public String pop() throws IOException {
        String result = data;
        read();
        return result;
    }
}