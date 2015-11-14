package iiitd.ac.in.androidfaceproject;

/**
 * Created by Sanchari on 13-11-2015.
 */
public class Cell {
    public Cell(int left,int top,boolean occupied,int id)
    {
        this.left=left;
        this.top=top;
        this.occupied=occupied;
        this.id=id;
    }
    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    private int top,left,id;
    private boolean occupied;
}
