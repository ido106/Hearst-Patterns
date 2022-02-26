

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Ido Aharon
 * will get 2 arguments: (1) the absolute path to the directory of the corpus and (2) a lemma.
 * Your program will search all the possible hypernyms of the input lemma and print them to the console as follows
 */
public class DiscoverHypernym {
    /**
     * will get 2 arguments: (1) the absolute path to the directory of the corpus and (2) a lemma.
     * Your program will search all the possible hypernyms of the input lemma and print them to the console
     * @param args (1) the absolute path to the directory of the corpus and (2) a lemma.
     */
    public static void main(String[] args) {
        CreateHypernymDatabase hd = new CreateHypernymDatabase();
        // args0 is the file destination path
        hd.createDB(args[0], hd);
        // create the data base
        Map<String, Map<String, Integer>> db = hd.getDatabase();

        // the lemma is args1
        String hyponym = args[1];
        Map<String, Integer> result = new TreeMap<>();

        // create new db
        Set<String> hyponymKeySet = db.keySet();
        for (String s : hyponymKeySet) {
            if (db.get(s).containsKey(hyponym)) {
                result.put(s, db.get(s).get(hyponym));
            }
        }

        // if the lemma doesnt appear in the corpus
        if (result.isEmpty()) {
            System.out.println("The lemma doesn't appear in the corpus.");
            return;
        }

        // at last, sort the map and print it
        Map<String, Integer> sortedMap = CreateHypernymDatabase.valueSort(result);
        Set<String> sm = sortedMap.keySet();
        for (String s : sm) {
            // hypernym1: (x)
            System.out.println(s + ": (" + result.get(s) + ")");
        }
    }
}
