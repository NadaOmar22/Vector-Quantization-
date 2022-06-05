package vector_quantization;

import java.util.Scanner;

public class VectorQuantization 
{
    public static void main(String[] args)
    {        
        Compression compressObj = new Compression("Image.jpg");
        System.out.println("Enter the Width Vector: ");
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();

        System.out.println("Enter the Hight Vector: ");
        int height=in.nextInt();

        System.out.println("Enter the number of codeBlocks: ");
        int numCodeBlocks=in.nextInt();

        if(width != height )
        {
           int number = Math.min(width,height);
           compressObj.makeCompression(number, number, numCodeBlocks);
        }
        else
        {
            compressObj.makeCompression(width, height, numCodeBlocks);
        }

        compressObj.printCodeMatrix();

        System.out.println("-------------------------***********************************-------------------------");

        //Decompress

        Decompression decompress = new Decompression(compressObj.getCodeMatrix(), compressObj.averageList);
        decompress.makeDecompression();

        decompress.printDecompressMatrix();
    }
}

/*
Test Cases 
Width : 2     Hight : 2    number of codeBlocks: 6
Width : 2     Hight : 2    number of codeBlocks: 4
Width : 2     Hight : 4    number of codeBlocks: 4
Width : 4     Hight : 2    number of codeBlocks: 4
Width : 4     Hight : 4    number of codeBlocks: 4
Width : 4     Hight : 4    number of codeBlocks: 6
Width : 3     Hight : 3    number of codeBlocks: 6
*/