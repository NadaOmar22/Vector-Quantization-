package vector_quantization;

import java.util.ArrayList;

public class Compression 
{
    public ImageProcessor image;
    public ArrayList<Imageblock> averageList = new ArrayList<>();
    private ArrayList<Imageblock> splittingList = new ArrayList<>();

    public String[][] result;

    Compression(String imageFile) 
    {
        image = new ImageProcessor(imageFile);
    }

    public String[][] makeCompression(int blockWidth, int blockHeight, int codeBlockSize)
    {
        //divide the allImage to blocks (vectors)
        image.makeImageProcessor();
        image.divideImage(blockWidth, blockHeight);//image.printBlocks();

        //fisrt average(send all blocks)
        averageList.add(calculateAverage(image.blocks));//im.printCoordinates();

        //that will get the code blocks.
        if (codeBlockSize != 1) 
        {
            for (int it = 0; it < Math.round(codeBlockSize / 2); it++) 
            {
                for (int i = 0; i < averageList.size(); i++) 
                {
                    splittingList.add(splittingGreater(averageList.get(i)));
                    splittingList.add(splittingSmaller(averageList.get(i)));
                }
                averageList.clear();
                averageList = new ArrayList<>();

                for (int i = 0; i < splittingList.size(); i++) 
                {
                    ArrayList<Imageblock> nearstBlocks = nearestBlock(i);
                    if (!nearstBlocks.isEmpty())
                    {
                        averageList.add(calculateAverage(nearstBlocks));
                    }
                }
                splittingList.clear();
                splittingList = new ArrayList<>();
            }
        }

        averageList = CorrectCodeBlocks();
        splittingList = averageList;
        assignCodeToBookCode();
        assignCodesToOriginalVectors();
        modify();

        return result;
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

    public void assignCodeToBookCode()
    {
        for (int i = 0; i < averageList.size(); i++) 
        {
            averageList.get(i).code = convert_To_Binary(i);
        }
    }

    public void modify() 
    {
        result = new String[image.stepsNum][image.stepVectors];

        int count = 0;
        for (int i = 0; i < image.stepsNum; i++)
        {
            for (int j = 0; j <image.stepVectors; j++) 
            {
                result[i][j] = image.blocks.get(count).code;
                count++;
            }
        }
    }

    public void assignCodesToOriginalVectors() 
    {
        for (int j = 0; j < averageList.size(); j++) 
        {
            ArrayList<Imageblock> near = nearestBlock(j);

            for (int i = 0; i < near.size(); i++) 
            {
                near.get(i).code = averageList.get(j).code;
            }
        }
    }

    public ArrayList<Imageblock> nearestBlock(int id) 
    {
        ArrayList<Imageblock> nearest = new ArrayList<>();

        for (int count = 0; count < image.blocks.size(); count++) 
        {
            ArrayList<Integer> diff = new ArrayList<>();
            Imageblock block = image.blocks.get(count);

            for (int i = 0; i < splittingList.size(); i++) 
            {
                diff.add(getDiffernce(block, splittingList.get(i)));
            }

            //get the min of diff
            int index = getMinDiff(diff);

            if (index == id) 
            {
                nearest.add(block);
            }
        }

        return nearest;
    }

    int getDiffernce(Imageblock target, Imageblock block) 
    {
        int result = 0;
        for (int i = 0; i < target.getHeight(); i++) 
        {
            for (int j = 0; j < target.getWidth(); j++) 
            {
                result += (target.blockCoordinates[i][j] - block.blockCoordinates[i][j]) * (target.blockCoordinates[i][j] - block.blockCoordinates[i][j]);
            }
        }
        return result;
    }

    int getMinDiff(ArrayList<Integer> diff) 
    {
        int index = -1;
        if (!diff.isEmpty()) 
        {
            int min = diff.get(0);
            for (int i = 0; i < diff.size(); i++) 
            {
                if (diff.get(i) <= min) 
                {
                    min = diff.get(i);
                    index = i;
                }
            }
        }
        return index;
    }

    public Imageblock calculateAverage(ArrayList<Imageblock> blocks) 
    {
        Imageblock result = new Imageblock(blocks.get(0).getWidth(), blocks.get(0).getHeight());

        result.blockCoordinates = new int[blocks.get(0).getHeight()][blocks.get(0).getWidth()];

        for (int count = 0; count < blocks.size(); count++) 
        {
            for (int i = 0; i < blocks.get(count).getHeight(); i++) 
            {
                for (int j = 0; j < blocks.get(count).getWidth(); j++) 
                {
                    result.blockCoordinates[i][j] += blocks.get(count).blockCoordinates[i][j];
                }
            }
        }

        for (int i = 0; i < result.getHeight(); i++) 
        {
            for (int j = 0; j < result.getWidth(); j++) 
            {
                result.blockCoordinates[i][j] = result.blockCoordinates[i][j] / blocks.size();
            }
        }
        return result;
    }

    //right spliting
    public Imageblock splittingGreater(Imageblock targetBlock) 
    {
        Imageblock result = new Imageblock(targetBlock.getWidth(), targetBlock.getHeight());

        result.blockCoordinates = new int[targetBlock.getHeight()][targetBlock.getWidth()];
        for (int i = 0; i < targetBlock.getHeight(); i++) 
        {
            for (int j = 0; j < targetBlock.getWidth(); j++)
            {
                int value = targetBlock.blockCoordinates[i][j];
                result.blockCoordinates[i][j] = value + 1;
            }
        }
        return result;
    }

    //leftSpiliting
    public Imageblock splittingSmaller(Imageblock targetBlock) 
    {
        Imageblock result = new Imageblock(targetBlock.getWidth(), targetBlock.getHeight());
        result.blockCoordinates = new int[targetBlock.getHeight()][targetBlock.getWidth()];
        for (int i = 0; i < targetBlock.getHeight(); i++) 
        {
            for (int j = 0; j < targetBlock.getWidth(); j++) 
            {
                int value = targetBlock.blockCoordinates[i][j];
                result.blockCoordinates[i][j] = value - 1;
            }
        }
        return result;
    }

    private ArrayList<Imageblock> CorrectCodeBlocks() 
    {
        //AverageList is the codeBlocks
        ArrayList<Imageblock> nextCodeBlocks = null;
        while (true) {
            int difference = 0;
            splittingList = averageList;
            nextCodeBlocks = new ArrayList<>();

            for (int i = 0; i < averageList.size(); i++) 
            {
                ArrayList<Imageblock> nearstBlocks = nearestBlock(i);
                if (!nearstBlocks.isEmpty()) 
                {
                    nextCodeBlocks.add(calculateAverage(nearstBlocks));
                }
            }

            //check to stop
            for (int i = 0; i < averageList.size(); i++) 
            {
                for (int row = 0; row < averageList.get(0).getHeight(); row++) 
                {
                    for (int col = 0; col < averageList.get(0).getWidth(); col++) 
                    {
                        difference += Math.abs(averageList.get(i).blockCoordinates[row][col] - nextCodeBlocks.get(i).blockCoordinates[row][col]);
                    }
                }
            }
            difference = difference / averageList.size();

            if (difference < 0.01) 
            {
                break;
            } 
            else
            {
                averageList = nextCodeBlocks;
            }
        }
        return nextCodeBlocks;
    }

    void printCodeMatrix() 
    {
        System.out.println("----------------The Compress Code----------");
        for (int i = 0; i < result.length; i++) 
        {
            for (int j = 0; j < result[0].length / 2; j++) 
            {
                System.out.print(" " + result[i][j]);
            }
            System.out.println();
        }
    }

    String[][] getCodeMatrix() 
    {
        return result;
    }
}