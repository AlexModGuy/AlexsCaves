package com.github.alexmodguy.alexscaves.client.gui.book;

public class BookLink {
    private int lineNumber;
    private int characterStartsAt;
    private String displayText;
    private String linksTo;

    private boolean hovered = false;

    public BookLink(int lineNumber, int characterStartsAt, String displayText, String linksTo) {
        this.lineNumber = lineNumber;
        this.characterStartsAt = characterStartsAt;
        this.displayText = displayText;
        this.linksTo = linksTo;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getCharacterStartsAt() {
        return characterStartsAt;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getLinksTo() {
        return linksTo;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }
}
