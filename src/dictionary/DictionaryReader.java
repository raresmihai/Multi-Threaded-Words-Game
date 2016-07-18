package dictionary;

import java.io.*;

/**
 * Created by rares on 21.04.2016.
 */
public class DictionaryReader {
    Trie dictionary = new Trie();

    public DictionaryReader() {
        readWords();
    }

    void readWords() {
        File file = new File("src/dictionary/wordsList.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String word = null;
        try {
            while( (word = br.readLine())!= null ){
                if(word.length() > 2 && !word.contains("'")) {
                    dictionary.insert(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Trie getDictionary() {
        return dictionary;
    }
}
