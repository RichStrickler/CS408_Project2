package edu.jsu.mcis.cs408.project2;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CrosswordViewModel extends ViewModel {

    private DatabaseHandler savedKeys;

    private static final int WORD_DATA_FIELDS = 6;
    private static final int WORD_HEADER_FIELDS = 2;
    public static final char BLOCK_CHAR = '*';
    public static final char BLANK_CHAR = ' ';

    private static final String TAG = "CrosswordViewModel";

    private final MutableLiveData<HashMap<String, Word>> words = new MutableLiveData<>();

    private final MutableLiveData<char[][]> letters = new MutableLiveData<>();
    private final MutableLiveData<int[][]> numbers = new MutableLiveData<>();

    private final MutableLiveData<Integer> puzzleWidth = new MutableLiveData<>();
    private final MutableLiveData<Integer> puzzleHeight = new MutableLiveData<>();

    private final MutableLiveData<String> cluesAcross = new MutableLiveData<>();
    private final MutableLiveData<String> cluesDown = new MutableLiveData<>();

    // Initialize Shared Model

    public void init(Context context) {
        savedKeys = new DatabaseHandler(context.getApplicationContext(), null, null, 1);
        if (words.getValue() == null) {
            loadWords(context);
            loadDatabase();
        }

    }

    // Add Word to Grid

    public void addWordToGrid(String key) {

        // Get word from collection (look up using the given key)

        Word word = Objects.requireNonNull(words.getValue()).get(key);

        // Was the word found in the collection?

        if (word != null) {

            // If so, get properties (row, column, and the word itself)

            int row = word.getRow();
            int column = word.getColumn();
            String w = word.getWord();

            // Add word to Letters array, one character at a time

            char[] letterSet = w.toCharArray();
            char[][] lArray = letters.getValue();

            for (int i = 0; i < w.length(); ++i) {
                int columnAdjusted = column + i;
                int rowAdjusted = row + i;

                if (word.getDirection().toString().equals("A")){
                    lArray[row][columnAdjusted] = letterSet[i];
                }
                if (word.getDirection().toString().equals("D")){
                    lArray[rowAdjusted][column] = letterSet[i];

                }

            }
            letters.setValue(lArray);
            savedKeys.addKey(key.toString());
        }

    }

    public boolean gameComplete(){
        boolean gameState = true;
        for (Map.Entry<String, Word> e : Objects.requireNonNull(words.getValue()).entrySet()) {
            Word word = Objects.requireNonNull(words.getValue()).get(e.getKey());

            int row = word.getRow();
            int column = word.getColumn();
            String w = word.getWord();

            // Add word to Letters array, one character at a time

            char[][] lArray = letters.getValue();

            for (int i = 0; i < w.length(); ++i) {
                int columnAdjusted = column + i;
                int rowAdjusted = row + i;

                if (word.getDirection().toString().equals("A")){
                    if(lArray[row][columnAdjusted] == BLANK_CHAR){gameState = false;};
                }
                if (word.getDirection().toString().equals("D")){
                    if(lArray[rowAdjusted][column] == BLANK_CHAR){gameState = false;};

                }

            }

        }
        return gameState;


    }

    // Add all words to grid (for testing purposes only!)

    public void addAllWordsToGrid() {
        for (Map.Entry<String, Word> e : Objects.requireNonNull(words.getValue()).entrySet()) {
            addWordToGrid( e.getKey() );
        }
    }

    // Load game data from puzzle file ("puzzle.csv")

    private void loadDatabase(){
            ArrayList<String> keys = savedKeys.getAllKeysAsList();
            if (savedKeys != null) {
                for (int i = 0; i < keys.size(); ++i) {
                    addWordToGrid(keys.get(i).toString());
                }
            }
    }

    private void loadWords(Context context) {

        HashMap<String, Word> map = new HashMap<>();
        StringBuilder clueAcrossBuffer = new StringBuilder();
        StringBuilder clueDownBuffer = new StringBuilder();

        // Open puzzle file

        int id = R.raw.puzzle;
        BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(id)));

        try {

            String line = br.readLine();
            String[] fields = line.trim().split("\t");

            // Is first row of puzzle file a valid header?

            if (fields.length == WORD_HEADER_FIELDS) {

                // If so, get puzzle height and width from header

                int height = Integer.parseInt(fields[0]);
                int width = Integer.parseInt(fields[1]);

                // Initialize letter and number arrays

                char[][] lArray = new char[height][width];
                int[][] nArray = new int[height][width];

                for (int i = 0; i < height; ++i) {
                    for (int j = 0; j < width; ++j) {
                        lArray[i][j] = BLOCK_CHAR;
                        nArray[i][j] = 0;
                    }
                }

                // Read game data (remainder of puzzle file)

                while ((line = br.readLine()) != null) {

                    // Get word fields from next row

                    fields = line.trim().split("\t");

                    // Is this a valid word?

                    if (fields.length == WORD_DATA_FIELDS) {

                        // If so, initialize new word

                        Word word = new Word(fields);

                        // Get row and column

                        int row = word.getRow();
                        int column = word.getColumn();

                        // Add box number

                        nArray[row][column] = word.getBox();

                        // Clear grid squares

                        for (int i = 0; i < word.getWord().length(); ++i) {
                            int columnAdjusted = column + i;
                            int rowAdjusted = row + i;

                            if (word.getDirection().toString().equals("A")){
                                lArray[row][columnAdjusted] = BLANK_CHAR;
                            }
                            if (word.getDirection().toString().equals("D")){
                                lArray[rowAdjusted][column] = BLANK_CHAR;

                            }

                        }

                        // Append Clue to StringBuilder (either clueAcrossBuffer or clueDownBuffer)
                        switch(word.getDirection().toString()){
                            case "A":
                                clueAcrossBuffer.append(word.getBox()).append(": ").append(word.getClue()).append("\n");
                                break;

                            case "D":
                                clueDownBuffer.append(word.getBox()).append(": ").append(word.getClue()).append("\n");
                                break;
                        }

                        // Create unique key; add word to collection

                        String key = word.getBox() + word.getDirection().toString();
                        map.put(key, word);

                    }

                }

                // Initialize MutableLiveData Members

                words.setValue(map);

                puzzleHeight.setValue(height);
                puzzleWidth.setValue(width);

                letters.setValue(lArray);
                numbers.setValue(nArray);

                cluesAcross.setValue(clueAcrossBuffer.toString());
                cluesDown.setValue(clueDownBuffer.toString());

            }

            br.close();

        }
        catch (Exception e) { Log.e(TAG, e.toString()); }

    }

    // Getter Methods

    public int getNumber(int row, int column) {

        int number = 0;
        Word word = null;

        for (Map.Entry<String, Word> e : Objects.requireNonNull(words.getValue()).entrySet()) {
            word = Objects.requireNonNull(words.getValue()).get(e.getKey());

            if(word.getColumn() == column && word.getRow() == row){
                number = word.getBox();
            }

        }
        return number;
    }

    public String getWord(int box, String direction) {
        String wordString = "";
        Word word = null;

        for (Map.Entry<String, Word> e : Objects.requireNonNull(words.getValue()).entrySet()) {
            word = Objects.requireNonNull(words.getValue()).get(e.getKey());

            if(e.getKey().equals(box + direction)){
                wordString = word.getWord();
            }
        }
        return wordString;
    }

    public LiveData<char[][]> getLetters() { return letters; }

    public LiveData<int[][]> getNumbers() { return numbers; }

    public LiveData<String> getCluesAcross() { return cluesAcross; }

    public LiveData<String> getCluesDown() { return cluesDown; }

    public LiveData<Integer> getPuzzleWidth() { return puzzleWidth; }

    public LiveData<Integer> getPuzzleHeight() { return puzzleHeight; }

    public int getBoxNumber(int row, int column) {
        return Objects.requireNonNull(numbers.getValue())[row][column];
    }

}