import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlotLandCalculator {


    private final int subdivideCost;

    public static void main(String[] args){
        PlotLandCalculator calculator = new PlotLandCalculator(50);
        Land.configPriceTable();
        Land bruteForceLand = calculator.bestPriceByBruteForce(new Land(3, 6));
        Land greedyLand = calculator.bestPriceByGreedyAlgorithm(new Land(3,6));
        System.out.println("Best Price of Dynamic Programming is " + calculator.bestPriceByDynamicProgramming(3,6));


        JFrame frame = new JFrame("Brute Force Approach");
        JPanel panel = new JPanel();
        panel.setSize(bruteForceLand.getWidth() * 50, bruteForceLand.getHeight() * 50);
        panel.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(bruteForceLand.getWidth() * 50, bruteForceLand.getHeight() * 50 + 100);
        JPanel layout = new JPanel();
        layout.setLayout(new BoxLayout(layout, BoxLayout.Y_AXIS));
        layout.add(calculator.printResult(panel, bruteForceLand, 50));
        layout.add(new JLabel("The overall price is " + bruteForceLand.getBestPrice()));
        frame.add(layout);
        frame.setVisible(true);

        JFrame greedyFrame = new JFrame("Greedy Algorithm Approach");
        JPanel greedyPanel = new JPanel();
        greedyPanel.setSize(greedyLand.getWidth() * 50, greedyLand.getHeight() * 50);
        greedyPanel.setLayout(null);
        greedyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        greedyFrame.setSize(greedyLand.getWidth() * 50, greedyLand.getHeight() * 50 + 100);
        JPanel greedyLayout = new JPanel();
        greedyLayout.setLayout(new BoxLayout(greedyLayout, BoxLayout.Y_AXIS));
        greedyLayout.add(calculator.printResult(greedyPanel, greedyLand, 50));
        greedyLayout.add(new JLabel("The overall price is " + greedyLand.getBestPrice()));
        greedyFrame.add(greedyLayout);
        greedyFrame.setVisible(true);

    }

    //Initialize subdivide cost;
    public PlotLandCalculator(int subdivideCost) {
        this.subdivideCost = subdivideCost;
    }

    //Print function for 2D array
    //Indicator for the title of row and column
    public static StringBuilder printTwoDimensionalArrayTable(Integer[][] twoDimensionalArray, boolean indicator) {
        StringBuilder result = new StringBuilder();
        result.append(" ");
        if (indicator) {
            for (int x = 0; x < twoDimensionalArray[0].length; x++) {
                result.append("  ").append(x + 1).append("  ");
            }
        }

        result.append("\n");
        for (int width = 0; width < twoDimensionalArray.length; width++) {
            if (indicator) {
                result.append(width + 1);
            }
            for (Integer length : twoDimensionalArray[width]) {
                result.append(" ").append(length).append(" ");
            }
            result.append("\n");
        }
        return result;
    }

    //Common Function
    //Algorithm for calculate total value of two lands and subtract subdivision cost
    private int getTotalValue(int firstPartPrice, int secondPartPrice, int subdivideLength){
        return (firstPartPrice + secondPartPrice - (subdivideLength * subdivideCost));
    }

    //Brute Force Approach
    public Land bestPriceByBruteForce(Land land){
        return bestPriceByBruteForceHelper(land);
    }

    private Land bestPriceByBruteForceHelper(Land land){
        //Initialize the best price by original price
        Land bestLand = land;
        int firstPartPrice;
        int secondPartPrice;
        int totalPrice;

        //horizontally subdivide
        //Calculate all possibilities of horizontally subdivision
        for(int x = 1; x < land.getWidth(); x++){
            Land newLand = new Land(land.getWidth(), land.getHeight());
            Land firstLand = bestPriceByBruteForceHelper(new Land(x, land.getHeight()));
            Land secondLand = bestPriceByBruteForceHelper(new Land(land.getWidth() - x, land.getHeight()));
            newLand.setFirstLand(firstLand);
            newLand.setSecondLand(secondLand);
            newLand.setSubdivision(new Direction(CutDirection.Horizontal, x, getTotalValue(firstLand.getBestPrice(),
                    secondLand.getBestPrice(), land.getHeight())));
            //Recurse the divided land to find the best value and compare the original price without subdivision
//            firstPartPrice = bestPriceByBruteForceHelper(x, length);
//            secondPartPrice = bestPriceByBruteForceHelper(width - x, length);
            //Calculate the best price of two lands
            //best price replaced by total price if total price is higher than best price
            if(newLand.getBestPrice() > bestLand.getBestPrice()){
                bestLand = newLand;
            }
        }
        //vertically subdivide
        //Calculate all possibilities of vertically subdivision
        for(int y = 1 ; y < land.getHeight(); y++){
            Land newLand = new Land(land.getWidth(), land.getHeight());
            //Recurse the divided land to find the best value and compare the original price without subdivision
            Land firstLand = bestPriceByBruteForceHelper(new Land(land.getWidth(), y));
            Land secondLand = bestPriceByBruteForceHelper(new Land(land.getWidth(), land.getHeight() - y));
            newLand.setFirstLand(firstLand);
            newLand.setSecondLand(secondLand);
            newLand.setSubdivision(new Direction(CutDirection.Vertical, y, getTotalValue(firstLand.getBestPrice(),
                    secondLand.getBestPrice(), land.getWidth())));
            //Calculate the best price of two lands
//            totalPrice = getTotalValue(firstPartPrice, secondPartPrice, land.getWidth());
            //best price replaced by total price if total price is higher than best price
            if(newLand.getBestPrice() > bestLand.getBestPrice()){
                bestLand = newLand;
            }
        }
        //Return the highest price of the land
        return bestLand;
    }

    public JPanel printResult(JPanel panel, Land land, int size){
        if(land.subdivision == null){
            JLabel label = new JLabel(land.getWidth() + "x" + land.getHeight());
            label.setSize(panel.getWidth(), panel.getHeight());
            label.setLocation(0,0);
            panel.add(label);
            return panel;
        }

        Land firstLand = land.getFirstLand();
        JPanel firstLandPanel = new JPanel();
        firstLandPanel.setLayout(null);
        firstLandPanel.setSize(firstLand.getWidth() * size, firstLand.getHeight() * size);
        firstLandPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        Land secondLand = land.getSecondLand();
        JPanel secondLandPanel = new JPanel();
        secondLandPanel.setLayout(null);
        secondLandPanel.setSize(secondLand.getWidth() * size, secondLand.getHeight() * size);
        secondLandPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        switch(land.getSubdivision().getCutDirection()){
            case Vertical:
                secondLandPanel.setLocation(0, firstLand.getHeight() * size);
                break;
            case Horizontal:
                secondLandPanel.setLocation(firstLand.getWidth() * size, 0);
                break;
        }
        panel.add(firstLandPanel);
        panel.add(secondLandPanel);
        printResult(firstLandPanel, land.firstLand, size);
        printResult(secondLandPanel, land.secondLand, size);
        return panel;
    }

    //Greedy Algorithm Approach
    public Land bestPriceByGreedyAlgorithm(Land land){
        //Get current best option
        //Direction contains the cut direction and the cut place
        Direction direction = greedyOptionForBestPrice(land.getWidth(), land.getHeight(), land.getLandPrice());
        //Read the cut direction
        switch (direction.getCutDirection()){
            //If vertical
            case Vertical -> {
                Land firstLand = bestPriceByGreedyAlgorithm(new Land(land.getWidth(), direction.getCutPlace()));
                Land secondLand = bestPriceByGreedyAlgorithm(new Land(land.getWidth(), land.getHeight() - direction.getCutPlace()));
                land.setFirstLand(firstLand);
                land.setSecondLand(secondLand);
                land.setSubdivision(direction);
                land.getSubdivision().setBestPrice(getTotalValue(firstLand.getBestPrice(), secondLand.getBestPrice(), land.getWidth()));
            }
            //If horizontal
            case Horizontal -> {
                Land firstLand = bestPriceByGreedyAlgorithm(new Land(direction.getCutPlace(), land.getHeight()));
                Land secondLand = bestPriceByGreedyAlgorithm(new Land(land.getWidth() - direction.getCutPlace(), land.getHeight()));
                land.setFirstLand(firstLand);
                land.setSecondLand(secondLand);
                land.setSubdivision(direction);
                land.getSubdivision().setBestPrice(getTotalValue(firstLand.getBestPrice(), secondLand.getBestPrice(), land.getWidth()));
            }
            //No further action if not require cutting
            case NotRequired -> {}
        }
        //Return the best price
        return land;
    }

    //Find greedy option for current situation
    //Almost same approach with brute force, but only process next step after measure of all possibilities.
    //No further action will take if no subdivision make more value than original land.
    public Direction greedyOptionForBestPrice(int width, int height,  int bestPrice){
        CutDirection cutDirection = CutDirection.NotRequired;
        int subdivisionPlace = 0;
        Land firstLand;
        Land secondLand;
        int totalPrice;
        //horizontal subdivision
        for(int x = 1; x < width; x++){
            firstLand = new Land(x, height);
            secondLand = new Land(width - x, height);
            totalPrice = getTotalValue(firstLand.getLandPrice(), secondLand.getLandPrice(), height);
            if(totalPrice > bestPrice){
                cutDirection = CutDirection.Horizontal;
                subdivisionPlace = x;
                bestPrice = totalPrice;
            }
        }
        //vertical subdivision
        for(int y = 1; y < height; y++){
            firstLand = new Land(width, y);
            secondLand = new Land(width, height - y);
            totalPrice = getTotalValue(firstLand.getLandPrice(), secondLand.getLandPrice(), width);
            if(totalPrice > bestPrice){
                cutDirection = CutDirection.Vertical;
                subdivisionPlace = y;
                bestPrice = totalPrice;
            }
        }

        return new Direction(cutDirection, subdivisionPlace, bestPrice);
    }

    //Exact Approach (Dynamic Programming Approach)
    public int bestPriceByDynamicProgramming(int width, int length){
        //Initialize the table
        Integer[][] dynamicProgrammingTable = dynamicProgrammingTableConfig(width, length);

        //Start inserting the value from top left
        for(int x = 1; x < dynamicProgrammingTable.length; x++){
            for(int y = 1; y < dynamicProgrammingTable[x].length; y++){
                Land newLand = new Land(x,y);
                //Get all value in same column from table
                Integer[] verticalArray = getVerticalArray(x, y, dynamicProgrammingTable);
                //Get all value in same row from table
                Integer[] horizontalArray = getHorizontalArray(x, y, dynamicProgrammingTable);
                //Get the highest price by testing the combination
                //For instance, if I am looking at the best price of 3 x 5 land, the horizontal array will be 100, 250, 350, 420, 450
                //then the total value will be
                //350(first land) + 350(second land) - 50(subdivision cost) x 3(subdivision length) = 550
                int horizontalBestPrice = BestPriceForEachCombination(horizontalArray, x);
                //For instance, if I am looking at the best price of 3 x 6 land, the vertical array will be 200, 450
                //then the total value will be
                //200(first land) + 450(second land) - 50(subdivision cost) x 6(subdivision length) = 350
                int verticalBestPrice = BestPriceForEachCombination(verticalArray, y);
                //The original price without subdivision
                int originalPrice = newLand.getLandPrice();
                //Get the highest price and insert to the table
                dynamicProgrammingTable[x][y] = this.compareThreeInteger(horizontalBestPrice, verticalBestPrice, originalPrice);
            }
        }
        System.out.println(printTwoDimensionalArrayTable(dynamicProgrammingTable, false));
        //Return the highest value which should be the bottom right
        int rowLength = dynamicProgrammingTable.length - 1;
        int columnLength = dynamicProgrammingTable[rowLength].length - 1;
        return dynamicProgrammingTable[rowLength][columnLength];
    }

    public Integer[][] dynamicProgrammingTableConfig(int width, int length) throws IllegalArgumentException {
        //Throw exception when inappropriate value passed in
        if(width <= 0 || length <= 0){
            throw new IllegalArgumentException();
        }
        //Create a 2D array
        Integer[][] dynamicProgrammingTable = new Integer[width + 1][length + 1];
        //Initialize the first column and row to 0
        for(int x = 0; x < dynamicProgrammingTable.length; x++){
            dynamicProgrammingTable[x][0] = 0;
        }
        for(int y = 1; y < dynamicProgrammingTable[0].length; y++){
            dynamicProgrammingTable[0][y] = 0;
        }
        //Return 2D array
        return dynamicProgrammingTable;
    }

    //Get Vertical subarray from 2D array
    private Integer[] getVerticalArray(int width, int length, Integer[][] dynamicProgrammingTable){
        List<Integer> resultList = new ArrayList<>();
        for(int y = 1; y < width; y++){
            resultList.add(dynamicProgrammingTable[y][length]);
        }
        return listToArray(resultList);
    }

    //Get Horizontal subarray from 2D array
    private Integer[] getHorizontalArray(int width, int length,  Integer[][] dynamicProgrammingTable){
        List<Integer> resultList = new ArrayList<>();
        for(int x = 0; x < length - 1; x++){
            resultList.add(dynamicProgrammingTable[width][x + 1]);
        }
        return listToArray(resultList);
    }

    //Get the best price from all combinations
    //For instance, the reference array is [100, 250, 350, 420, 450]
    //Function start the combination from start and end elements which is (100, 450), then (250, 420), then
    //(350, 350). If any combination is higher than best price, then best price will be replaced. Finally, return best price at the end.
    private int BestPriceForEachCombination(Integer[] referenceArray, int subdivisionLength){
        if(referenceArray == null){
            return 0;
        }
        int bestPrice = 0;
        int count = (referenceArray.length / 2) + 1;
        for(int x = 0; x < count; x++){
            int firstPartPrice = referenceArray[x] != null ? referenceArray[x] : 0;
            int secondPartPrice = referenceArray[(referenceArray.length - 1) - x] != null ? referenceArray[(referenceArray.length - 1) - x] : 0;
            int totalPrice = getTotalValue(firstPartPrice, secondPartPrice, subdivisionLength);
            if(totalPrice > bestPrice){
                bestPrice = totalPrice;
            }
        }
        return bestPrice;
    }

    //Compare three integers
    private int compareThreeInteger(int a, int b, int c){
        int biggest = a;
        if(b > a){
            biggest = b;
        }
        if(c > b){
            biggest = c;
        }
        if(a > c){
            biggest = a;
        }
        return biggest;
    }

    //Convert a list to array
    private Integer[] listToArray(List<Integer> list){
        Integer[] output;
        if(list.isEmpty()){
            return null;
        }
        output = new Integer[list.size()];
        int counter = 0;
        for(Integer element: list){
            output[counter++] = element;
        }
        return output;
    }
    }
