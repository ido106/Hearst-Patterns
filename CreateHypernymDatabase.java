// IDO AHARON ID 319024600

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ido Aharon
 * Your program will read all the files in the directory,
 * find and aggregate hypernym relations that match the Hearst patterns using
 * regular expressions, and save them in a txt file.
 */
public class CreateHypernymDatabase {
    // create the map for hypernym hyponym
    private Map<String, Map<String, Integer>> np;

    /**
     * Constructor.
     */
    public CreateHypernymDatabase() {
        this.np = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Class to represent value comparator.
     * @param <K> the k value
     * @param <V> the v value
     * @param map the map
     * @return the result
     */
    public static <K, V extends Comparable<V>> Map<K, V> valueSort(final Map<K, V> map) {
        /**
         * Static Method with return type Map and extending comparator class which compares values
         * associated with two keys.
         */
        Comparator<K> valueComparator = new Comparator<K>() {

            /**
             * return comparison results of values of two keys
             * @param k1
             * @param k2
             * @return
             */
            public int compare(K k1, K k2) {
                int comp = map.get(k2).compareTo(map.get(k1));
                if (comp == 0) {
                    return 1;
                } else {
                    return comp;
                }
            }

        };

        // SortedMap created using the comparator
        Map<K, V> sorted = new TreeMap<K, V>(valueComparator);

        sorted.putAll(map);

        return sorted;
    }

    /**
     * the main method to run the program.
     * @param args two arguments: (1) the path to the directory of the corpus and (2) the path to the output file.
     * @throws IOException if
     */
    public static void main(String[] args) throws IOException {
        // create the object
        CreateHypernymDatabase hd = new CreateHypernymDatabase();

        // create the destination folder to write to, in args1
        final File toFile = new File(args[1]);

        // create the file writer
        BufferedWriter fileWriter = null;
        try {
            // overwrites file if already exist
            fileWriter = new BufferedWriter(new FileWriter(toFile));
        } catch (IOException e) {
            System.out.println("something went wrong with the file writing");
        }
        // create the hypernym hyponym db, send the args0 as the files destination folder
        hd.createDB(args[0], hd);
        // write the file
        hd.writeToFile(fileWriter);
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e2) {
                System.out.println("something went wrong with the writer closing");
            }
        }
    }

    /**
     * create the hypernym hyponym database.
     * @param hd the db
     * @param filesPath the destination
     */
    public void createDB(String filesPath, CreateHypernymDatabase hd) {
        // create all regexes and patterns

        String regex1 = "<np>([^>]+)</np> ?,? such as <np>([^>]+)</np> "
                + "((,? ?<np>([^>]+)</np> ?)*,? ?((and)|(or))? ?<np>([^>]+)</np>)?";

        String regex2 = "such <np>([^>]+)</np> as <np>([^>]+)</np> "
                + "((,? ?<np>([^>]+)</np> ?)*,? ?((and)|(or))? ?<np>([^>]+)</np>)?";

        String regex3 = "<np>([^>]+)</np> ?,? including <np>([^>]+)</np> "
                + "((,? ?<np>([^>]+)</np> ?)*,? ?((and)|(or))? ?<np>([^>]+)</np>)?";

        String regex4 = "<np>([^>]+)</np> ?,? especially <np>([^>]+)</np> "
                + "((,? ?<np>([^>]+)</np> ?)*,? ?((and)|(or))? ?<np>([^>]+)</np>)?";

        String regex5 = "<np>([^>]+)</np> ?,? which is((( an example)|( a kind)|( a class))? of)? <np>([^>]+)</np>";
        Pattern p5 = Pattern.compile(regex5);

        String firstFourRegex = "(" + regex1 + ")" + "|" + "(" + regex2 + ")" + "|" + "(" + regex3
                + ")" + "|" + "(" + regex4 + ")";
        Pattern firstFourPattern = Pattern.compile(firstFourRegex);

        // Creating a File object for directory
        File directoryPath = new File(filesPath);

        // List of all files and directories
        File[] filesList = directoryPath.listFiles();

        // create the buffered reader
        BufferedReader reader = null;
        for (File currentFile : filesList) {
            try {
                reader = new BufferedReader(new FileReader(currentFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher1 = firstFourPattern.matcher(line);
                    Matcher matcher2 = p5.matcher(line);
                    // search for first pattern
                    while (matcher1.find()) {
                        hd.addFirstToDatabase(line.substring(matcher1.start(), matcher1.end()));
                    }
                    // search for second pattern
                    while (matcher2.find()) {
                        hd.addSecondToDatabase(line.substring(matcher2.start(), matcher2.end()));
                    }
                }
            } catch (IOException e) {
                System.out.println("something went wrong with the file reading");
            }
        }
        // delete every hypernym that has less than 3 hyponyms
        hd.deleteLessThanThree();

        // close the reader at the end of the process
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e1) {
                System.out.println("something went wrong with the reader closing");
            }
        }
    }

    /**
     * method to add the string to database. the first is the hyponym, and the second is the hypernym.
     * @param s the substring from line
     */
    private void addSecondToDatabase(String s) {
        // copy the substring
        String temp = s;
        // create a new pattern
        String regex = "<np>([^>]+)</np>";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(temp);
        // the first np is the hyponym
        matcher.find();
        String value = getNameFromNp(temp.substring(matcher.start(), matcher.end()));
        // the second is the hypernym
        matcher.find();
        String key = getNameFromNp(temp.substring(matcher.start(), matcher.end()));
        // check if the hypernym is already exist
        if (this.np.containsKey(key)) {
            // check if the hyponym is already exist
            if (this.np.get(key).containsKey(value)) {
                this.np.get(key).put(value, this.np.get(key).get(value) + 1);
            } else {
                // if the hyponym doesnt exist
                this.np.get(key).put(value, 1);
            }
        } else {
            // if the hypernym doesnt exist
            Map<String, Integer> newMap = new TreeMap<>();
            newMap.put(value, 1);
            this.np.put(key, newMap);
        }
    }

    /**
     * method to add the string to database. first np is the hypernym, and the others are hyponyms.
     * @param s the substring from line
     */
    private void addFirstToDatabase(String s) {
        // copy the substring
        String temp = s;
        // create a new pattern
        String regex = "<np>([^>]+)</np>";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(temp);
        matcher.find();
        String key = getNameFromNp(temp.substring(matcher.start(), matcher.end()));
        while (matcher.find()) {
            String value = getNameFromNp(temp.substring(matcher.start(), matcher.end()));
            // check if the hypernym is already exist
            if (this.np.containsKey(key)) {
                // check if the hyponym is already exist
                if (this.np.get(key).containsKey(value)) {
                    this.np.get(key).put(value, this.np.get(key).get(value) + 1);
                } else {
                    // if the hyponym doesnt exist
                    this.np.get(key).put(value, 1);
                }
            } else {
                // if the hypernym doesnt exist
                Map<String, Integer> newMap = new TreeMap<>();
                newMap.put(value, 1);
                this.np.put(key, newMap);
            }
        }
    }

    /**
     * get the name from the string.
     * @param s the string
     * @return the name
     */
    private String getNameFromNp(String s) {
        return s.substring(4, (s.length()) - 5);
    }

    /**
     * At the end of the process, group the hyponyms of the same hypernyms.
     * (also called co-hyponyms), ignore hypernyms that have less than 3 hyponyms
     */
    private void deleteLessThanThree() {
        // make the new map that will replace the current
        Map<String, Map<String, Integer>> temp =
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        // copy all details from the current map
        temp.putAll(this.np);
        Set<String> keySet = this.np.keySet();
        for (String key : keySet) {
            // if you found a hypernym with less than 3 hyponyms, delete it.
            if (this.np.get(key).size() < 3) {
                temp.remove(key);
            }
        }
        // set the new map
        this.np = temp;
    }

    /**
     * method to get the database.
     * @return the data base
     */
    public Map<String, Map<String, Integer>> getDatabase() {
        return this.np;
    }

    /**
     * Write to file the required information.
     * @param fileWriter the file writer
     */
    public void writeToFile(BufferedWriter fileWriter) {
        Set<String> keySet = this.np.keySet();
        // search every hypernym
        for (String hypernym : keySet) {
            // Calling the method valueSort
            Map<String, Integer> sortedMap = valueSort(this.np.get(hypernym));

            // flag to first element
            boolean first = true;

            // add the hypernym to the beginning of the string
            String lineToWrite = "";
            lineToWrite += hypernym;
            lineToWrite += ":";

            // get the hyponyms map
            Map<String, Integer> allHyponyms = this.np.get(hypernym);
            Set<String> hyponymKeySet = sortedMap.keySet();

            for (String hyponym : hyponymKeySet) {
                // if its the first element, do not add the comma
                if (!first) {
                    lineToWrite += ",";
                }
                first = false;

                lineToWrite += " ";
                lineToWrite += hyponym;
                lineToWrite += " (";
                int sum = allHyponyms.get(hyponym);
                lineToWrite += sum;
                lineToWrite += ")";
            }
            // write nextline
            lineToWrite += "\r\n";

            try {
                fileWriter.write(lineToWrite);
            } catch (IOException e) {
                System.out.println("Something went wrong with the file writing");
            }
        }
    }
}