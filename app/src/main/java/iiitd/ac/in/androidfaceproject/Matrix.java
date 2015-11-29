package iiitd.ac.in.androidfaceproject;

/**
 * Created by Sanchari on 13-11-2015.
 */
public class Matrix {
    Cell[][] grid = null;
    int eachWidth, eachHeight;

    /*manually add cells in the grid like below-
    9:::		   4:::	      3:::
    1 5 3		   0 1		  0 1
    6 0 7		   2 3		    2
    4 8 2

    */
    public Matrix(int width, int height) {
        System.out.println("cool reached Matrix class!layout number=" + Constants.layout_num);

        //if 3 pic layout is selected
        if (Constants.layout_num == 3) {
            eachWidth = width / 2;
            eachHeight = height / 2;
            grid = new Cell[2][2]; //1st column is merged
            grid[0][0] = new Cell(0, 0, 2*eachWidth,eachHeight,false, 0);
            grid[0][1] = null;
            grid[1][0] = new Cell(0,eachHeight, eachWidth,2*eachHeight,false, 1);
            grid[1][1] = new Cell(eachWidth, eachHeight, 2*eachWidth,2*eachHeight,false, 2);
        }
        //if 4 pic layout is selected
        else if (Constants.layout_num == 4) {
            eachWidth = width / 2;
            eachHeight = height / 2;
            grid = new Cell[2][2]; //1st column is merged
            grid[0][0] = new Cell(0, 0, eachWidth,eachHeight,false, 0);
            grid[1][0] = new Cell(0, eachHeight,eachWidth,2*eachHeight, false, 1);
            grid[0][1] = new Cell(eachWidth, 0, 2*eachWidth,eachHeight,false, 2);
            grid[1][1] = new Cell(eachWidth, eachHeight, 2*eachWidth,2*eachHeight,false, 3);
        }


        //if 9 pic layout is selected
        else if (Constants.layout_num == 9) {
            grid=new Cell[3][3];
            eachWidth = width / 3;
            eachHeight = height / 3;
            grid[0][0] = new Cell(0, 0, eachWidth,eachHeight,false, 1);
            grid[0][1] = new Cell(eachWidth, 0,2*eachWidth, eachHeight,false, 5);
            grid[0][2] = new Cell(eachWidth + eachWidth, 0,3*eachWidth, eachHeight,false, 3);
            grid[1][0] = new Cell(0, eachHeight, eachWidth,2*eachHeight,false, 6);
            grid[1][1] = new Cell(eachWidth, eachHeight, 2*eachWidth,2*eachHeight,false, 0);
            grid[1][2] = new Cell(eachWidth + eachWidth, eachHeight, 3*eachWidth,2*eachHeight,false, 7);
            grid[2][0] = new Cell(0, eachHeight + eachHeight, eachWidth,3*eachHeight,false, 4);
            grid[2][1] = new Cell(eachWidth, eachHeight + eachHeight,2*eachWidth,3*eachHeight, false, 8);
            grid[2][2] = new Cell(eachWidth + eachWidth, eachHeight + eachHeight,3*eachWidth,3*eachHeight, false, 2);

        }
        else
            System.out.println("Cool:some problem in selecting layout!!");
    }

    public Cell getCell(int id) {
        int range=0;
        if(Constants.layout_num==3 || Constants.layout_num==4)
            range=2;
        else if(Constants.layout_num==9)
            range=3;
        else {
            System.out.println("Layout num not valid!");
            return null;
        }
        for (int i = 0; i < range; i++)
            for (int j = 0; j < range; j++)
                if(grid[i][j]==null)
                    System.out.println("reached the null grid!");
                else{
                if (id == grid[i][j].getId())
                    return grid[i][j];
                }
        return null;
    }
}
