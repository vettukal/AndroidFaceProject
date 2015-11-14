package iiitd.ac.in.androidfaceproject;

/**
 * Created by Sanchari on 13-11-2015.
 */
public class Matrix {
    Cell[][] grid=new Cell[3][3];
    /*manually add 9 cells in the grid like below-
    1 5 3
    6 0 7
    4 8 2*/
    public Matrix(int eachWidth,int eachHeight)
    {
        System.out.println("cool reached Matrix class!");
        grid[0][0]=new Cell(0,0,false,1);
        grid[0][1]=new Cell(eachWidth,0,false,5);
        grid[0][2]=new Cell(eachWidth+eachWidth,0,false,3);
        grid[1][0]=new Cell(0,eachHeight,false,6);
        grid[1][1]=new Cell(eachWidth,eachHeight,false,0);
        grid[1][2]=new Cell(eachWidth+eachWidth,eachHeight,false,7);
        grid[2][0]=new Cell(0,eachHeight+eachHeight,false,4);
        grid[2][1]=new Cell(eachWidth,eachHeight+eachHeight,false,8);
        grid[2][2]=new Cell(eachWidth+eachWidth,eachHeight+eachHeight,false,2);
    }
    public Cell getCell(int id)
    {
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                if (id == grid[i][j].getId())
                    return grid[i][j];
        return null;
    }
}
