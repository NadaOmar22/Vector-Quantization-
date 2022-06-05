package vector_quantization;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

class Decompression 
{
    public String[][] compressedImage;

    public ArrayList<Imageblock> codeBook = new ArrayList();

    public ArrayList<int[][]> result;

    public int[][] R;

    public Decompression(String[][] compressedImage, ArrayList<Imageblock> codeBook)
    {
        this.result = new ArrayList<int[][]>();
        this.compressedImage = compressedImage;
        this.codeBook = codeBook;
    }

    public String convert_To_Binary(int value) 
    {
        String result = "";
        ArrayList<Integer> number = new ArrayList<>();
        if (value == 0) 
        {
            result = "0";
        } 
        else
        {
            while (value > 0) 
            {
                number.add(value % 2);
                value = value / 2;
            }
        }
        for (int i = number.size() - 1; i >= 0; i--) 
        {
            result += number.get(i);
        }

        return result;
    }

    public void assignCode() 
    {
        for (int i = 0; i < codeBook.size(); i++) 
        {
            codeBook.get(i).code = convert_To_Binary(i);
        }
    }

    public int[][] getCodeBookOfCode(String code) 
    {
        Imageblock result = null;

        for (int i = 0; i < codeBook.size(); i++) 
        {
            if (codeBook.get(i).code == null ? code == null : codeBook.get(i).code.equals(code)) 
            {
                result = codeBook.get(i);
                break;
            }
        }
        int[][] array = new int[result.getHeight()][result.getWidth()];
        if (result != null) 
        {
            for (int i = 0; i < result.blockCoordinates.length; i++) 
            {
                System.arraycopy(result.blockCoordinates[i], 0, array[i], 0, result.blockCoordinates[0].length);
            }
        }
        return array;
    }

    // create list of small matrices
    public void createListOfMatrices ()
    {
        int count = 0;
        for (int i = 0; i < compressedImage.length; i++) 
        {
            for (int j = 0; j < compressedImage[0].length; j++) 
            {
                if (compressedImage[i][j] == null) 
                {
                    continue;
                } 
                else 
                {
                    result.add(count, getCodeBookOfCode(compressedImage[i][j]));
                    count++;
                }
            }
        }
    }

    // take 2 matrices and make them on the same row
    public int[][] mergeMatrix(int a[][], int b[][]) 
    {
        int[][] c = new int[a.length][a[0].length + b[0].length];

        for (int i = 0; i < a.length; i++) 
        {
            int bb = -1;
            for (int j = 0; j < a[0].length + b[0].length; j++) 
            {
                if (j < a[0].length)
                {
                    c[i][j] = a[i][j];
                } 
                else 
                {
                    bb++;
                    if (bb >= 0 || bb < b[0].length) 
                    {
                        c[i][j] = b[i][bb];
                    }
                }
            }
        }
        return c;
    }

    // merge list of elements to make them in the same row
    public int[][] merge_list(ArrayList<int[][]> list) 
    {
        int[][] result2 = null;
        int[][] temp = null;
        if (list.size() >= 2) 
        {
            temp = mergeMatrix(list.get(0), list.get(1));
            for (int i = 2; i < list.size(); i++) 
            {
                temp = mergeMatrix(temp, list.get(i));
            }
        }
        result2 = temp;
        return result2;
    }

    // send list of matrices to try_merge function in recursive way
    // create R big matrix which will be used to get the original image
    public void createMatrix() 
    {
        int[][] c = null;

        R = new int[compressedImage.length*codeBook.get(0).getHeight()][compressedImage[0].length*codeBook.get(0).getWidth()];
        int[][] RR = null;
        int ii = 0;
        int jj = 0;

        int cc[][] = null;
        ArrayList<int[][]> temp = new ArrayList();
        int count = 0;
        while (count < result.size()) 
        {
            for (int j = 0; j < compressedImage[0].length; j++) 
            {
                if (count >= result.size()) 
                {
                    break;
                } 
                else 
                {
                    temp.add(result.get(count));
                }
                count++;
            }
            RR = merge_list(temp);

            temp.clear();

            for (int k = 0; k < RR.length; k++) 
            {
                jj = 0;
                for (int m = 0; m < RR[0].length; m++) 
                {
                    R[ii][jj] = RR[k][m];
                    jj++;
                }
                ii++;
            }
        }
    }

    public void convert_matrix_to_image()
    {
        int len = R.length;
        int wid = R[0].length;

        try {
            BufferedImage image;
            image = new BufferedImage(len, wid, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < len; x++) 
            {
                for (int y = 0; y < wid; y++) 
                {
                    int result=(R[x][y]<<16)|(R[x][y]<<8)|(R[x][y]);
                    image.setRGB(y,x,result);
                }
            }
            File output = new File("result.jpg");
            ImageIO.write(image, "jpg", output);
        } 
        catch (IOException e) 
        {
            
        }
    }

    public void makeDecompression()
    {
        assignCode();   // call convert_To_Binary function
        createListOfMatrices();  // call getCodeBookOfCode function
        createMatrix();     // call merge_list which call mergeMatrix
        convert_matrix_to_image();
    }

    void printDecompressMatrix()
    {
        System.out.println("The Decompress matrix  after make Decompress");
        for (int i = 0; i < R.length; i++) 
        {
            for (int j = 0; j < R[0].length; j++) 
            {
                System.out.print(" " + R[i][j]);
            }
            System.out.println();
        }
    }
}