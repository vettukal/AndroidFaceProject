package iiitd.ac.in.androidfaceproject;

/**
 * Created by Sanchari on 13-11-2015.
 */
public class Cell {
    public Cell(int left,int top,int right,int bottom,boolean occupied,int id)
    {
        System.out.println("Cool in cell constructor");
        this.left=left;
        this.top=top;
        this.right=right;
        this.bottom=bottom;
        this.occupied=occupied;
        this.id=id;
    }

    public int getTop() {
        return top;
    }


    public int getLeft() {
        return left;
    }


    public int getRight() {
        return right;
    }


    public int getBottom() {
        return bottom;
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
    /*
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
    public void setLeft(int left) {
        this.left = left;
    }
    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
    public void setTop(int top) {
        this.top = top;
    }

    public void setRight(int right)
    {
        this.right = right;
    }
    */
    private int top,left,bottom,right,id;
    private boolean occupied;
}
