package edu.jsu.mcis.cs408.project2;

public class Word {

    private final int row, column, box;
    private final WordDirection direction;
    private final String word, clue;

    public Word(String[] fields) {

        this.row = Integer.parseInt(fields[0]);
        this.column = Integer.parseInt(fields[1]);
        this.box = Integer.parseInt(fields[2]);
        this.word = fields[4];
        this.clue = fields[5];

        switch (fields[3]) {
            case "A":
                this.direction = WordDirection.ACROSS;
                break;
            case "D":
                this.direction = WordDirection.DOWN;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + fields[3]);
        }

    }

    public int getRow() { return row; }

    public int getColumn() { return column; }

    public int getBox() { return box; }

    public WordDirection getDirection() { return direction; }

    public String getWord() { return word; }

    public String getClue() { return clue; }

    public boolean isAcross() {
        return direction.equals(WordDirection.ACROSS);
    }

    public boolean isDown() {
        return direction.equals(WordDirection.DOWN);
    }

}