package Devices.DisplayObjects;

import javafx.scene.text.Text;

public class TextCmd {
    public final String pair;
    public final int s, f, c;
    public final boolean split;
    public final String left, right, text;
    public final Text styledFromMR; // copy font/fill from MR text node

    public TextCmd(String pair, int s, int f, int c, boolean split, String left, String right, String text, Text styledFromMR) {
        this.pair = pair;
        this.s = s;
        this.f = f;
        this.c = c;
        this.split = split;
        this.left = left;
        this.right = right;
        this.text = text;
        this.styledFromMR = styledFromMR;
    }
}
