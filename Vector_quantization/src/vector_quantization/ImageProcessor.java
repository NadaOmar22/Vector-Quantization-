package vector_quantization;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

class ImageProcessor 
{
    private File imageFile;
    public int widthImage;
    public int heightImage;
    private int blockWidth;
    private int blockHeight;
    private BufferedImage image;
    public int[][] coordinates;    
    public int stepVectors;
    public int stepsNum;
    public  ArrayList<Imageblock> blocks = new ArrayList<>();


    public ImageProcessor(String imageFile)
    {
        this.imageFile = new File(imageFile);
    }

    //convert image to matrix 2d arrayList
    public void makeImageProcessor() 
    {
        image = null;
        try 
        {
            image = ImageIO.read(imageFile);
        }
        catch (IOException e) 
        {
            System.out.println("Error");
        }

        widthImage = image.getWidth();
        heightImage = image.getHeight();

        coordinates=new int[heightImage][widthImage];

        //convert image to matrix pixels
        for (int x = 0; x < heightImage; x++) 
        {
            for (int y = 0; y < widthImage; y++) 
            {
                int rgb=image.getRGB(y,x);
                int redPart = (rgb >> 16) & 0xff;
                coordinates[x][y]=redPart;
            }
        }
    }

    //divide the image to blocks
    public void divideImage(int blockWidth, int blockHeigt) 
    {     
        //block size
        this.blockHeight = blockHeigt;
        this.blockWidth = blockWidth;

        //get the number of rows and columns from the image height and wight
        int rowsNum = heightImage;
        int colsNum = widthImage;

        //get the number of steps
        stepsNum = rowsNum / blockHeight;
        //get the number of vectors in each step
        stepVectors = colsNum / blockWidth;

        int rowStart = 0;
        int rowEnd = 0;
        int colStart;
        int colEnd;
        int blockscounter = 0;
        
        //iterate to the number of steps.
        for (int it0 = 0; it0 < stepsNum; it0++) 
        {
            rowEnd = rowStart + blockHeight - 1;
            colStart = 0;
            colEnd = 0;
            for (int it1 = 0; it1 < stepVectors; it1++) 
            {
                int blockRow = 0;
                int blockCol = 0;
                colEnd = colStart + blockWidth - 1;
                Imageblock block = new Imageblock(blockWidth,blockHeigt);
                block.blockCoordinates=new int[blockHeigt][blockWidth];
                for (int i = rowStart; i <= rowEnd; i++) 
                {
                    for (int j = colStart; j <=colEnd; j++)
                    {
                        block.blockCoordinates[blockRow][blockCol]= coordinates[i][j];
                        blockCol++;
                    }
                    blockCol = 0;
                    blockRow++;
                }
                blocks.add(block);
                colStart += blockWidth;
            }
            rowStart += blockHeight;
        }
    }

    public void printBlocks() 
    {
        System.out.println("The number of blocks "+blocks.size());
        System.out.println("The wight is "+widthImage);
        System.out.println("The heightImage "+heightImage);
        for (int i = 0; i < blocks.size(); i++) 
        {
            System.out.println("code : " +blocks.get(i).code );
            blocks.get(i).printCoordinates();
        }
    }
}