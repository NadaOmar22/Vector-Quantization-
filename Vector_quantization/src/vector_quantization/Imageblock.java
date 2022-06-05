package vector_quantization;

public class Imageblock 
{
    public int[][] blockCoordinates;
    private int width;
    private int height;
    public String code;

    public Imageblock() 
    {

    }

    Imageblock(int width, int height) 
    {
        this.width = width;
        this.height = height;
        this.blockCoordinates = new int[width][height];
    }

    int getWidth() 
    {
        return width;
    }

    int getHeight() 
    {
        return height;
    }

    void setWidth(int width) 
    {
        this.width = width;
    }

    void setHeight(int height) 
    {
        this.height = height;
    }

    //to test
    void printCoordinates() 
    {
        for (int i = 0; i < height; i++) 
        {
            for (int j = 0; j < width; j++) 
            {
                System.out.print(blockCoordinates[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}