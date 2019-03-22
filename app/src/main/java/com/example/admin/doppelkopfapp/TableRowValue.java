package com.example.admin.doppelkopfapp;

public class TableRowValue {

    enum Appearance {
        REGULAR, INACTIVE, WINNER, LOSER
    }

    private String text;
    private Appearance appearance;
    private boolean solo;

    public TableRowValue(String text, Appearance appearance, boolean solo){
        this.text = text;
        this.appearance = appearance;
        this.solo = solo;
    }

    public TableRowValue(String text, Appearance appearance) {
        this(text, appearance, false);
    }

    public TableRowValue(String text) {
        this(text, Appearance.REGULAR);
    }

    public String getText() {
        return text;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public boolean isSolo() {
        return solo;
    }
}
