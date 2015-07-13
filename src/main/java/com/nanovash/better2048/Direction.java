package com.nanovash.better2048;

import javax.swing.SpringLayout;
import java.util.List;

public enum Direction {

    UP(true, true, false, TileCanvas.verticLocs, TileCanvas.horizLocs, SpringLayout.NORTH),
    DOWN(false, false, false, TileCanvas.verticLocs, TileCanvas.horizLocs, SpringLayout.NORTH),
    LEFT(true, true, true, TileCanvas.horizLocs, TileCanvas.verticLocs, SpringLayout.WEST),
    RIGHT(false, false, true, TileCanvas.horizLocs, TileCanvas.verticLocs, SpringLayout.WEST);

    boolean loopArg1;
    boolean shouldBeBigger;
    boolean reversed;
    List<List<Tile>> mainList;
    List<List<Tile>> sideList;
    String direction;

    Direction(boolean loopArg1, boolean shouldBeBigger, boolean reversed, List<List<Tile>> mainList, List<List<Tile>> sideList, String direction) {
        this.loopArg1 = loopArg1;
        this.shouldBeBigger = shouldBeBigger;
        this.reversed = reversed;
        this.mainList = mainList;
        this.sideList = sideList;
        this.direction = direction;
    }

    public int getArg1() {
        if(loopArg1)
            return 0;
        else
            return TileCanvas.canvasLength - 1;
    }

    public int getArg2() {
        if(loopArg1)
            return TileCanvas.canvasLength - 1;
        else
            return 0;
    }

    public boolean firstLoopContinue(int i) {
        if(shouldBeBigger)
            return i <= TileCanvas.canvasLength - 1;
        else
            return i >= 0;
    }

    public int firstLoopUpdate(int i) {
        return i + (shouldBeBigger ? 1 : -1);
    }

    public boolean secondLoopContinue(int i) {
        if(shouldBeBigger)
            return i >= getArg1();
        else
            return i <= getArg1();
    }

    public int secondLoopUpdate(int i) {
        return i + (shouldBeBigger ? -1 : 1);
    }

    public void place(Tile t, int x, int y) {
        if(reversed)
            Better2048.game.positionTile(t, y, x);
        else
            Better2048.game.positionTile(t, x, y);
    }

    public int positionChange(int where) {
        return where + (shouldBeBigger ? -1 : 1);
    }

    public int getNeededSize() {
        if(!shouldBeBigger)
            return Better2048.game.getPreferredSize().height;
        else
            return Better2048.game.getPreferredSize().width;
    }
}