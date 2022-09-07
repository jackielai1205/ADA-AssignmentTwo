import java.util.ArrayList;
import java.util.List;

public class PlotLandCalculator {

    private Integer[][] priceTable;
    private final int subdivideCost;

    public static void main(String[] args){
        PlotLandCalculator calculator = new PlotLandCalculator(50);
        calculator.configPriceTable();
        System.out.println(PlotLandCalculator.printTwoDimensionalArrayTable(calculator.priceTable, true));
        System.out.println(calculator.bestPriceByBruteForce(3, 6));
        System.out.println(calculator.bestPriceByDynamicProgramming(3, 6));
        System.out.println(calculator.bestPriceByGreedyAlgorithm(3, 6));
    }

    //Initialize subdivide cost;
    public PlotLandCalculator(int subdivideCost) {
        this.subdivideCost = subdivideCost;
    }

    //Initialize Land Price
    public void configPriceTable(){
        priceTable = new Integer[6][6];
        priceTable[0] = new Integer[]{20,40,100,130,150,200};
        priceTable[1] = new Integer[]{40,140,250,320,400,450};
        priceTable[2] = new Integer[]{100,250,350,420,450,500};
        priceTable[3] = new Integer[]{130,320,420,500,600,700};
        priceTable[4] = new Integer[]{150,400,450,600,700,800};
        priceTable[5] = new Integer[]{200,450,500,700,800,900};
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

    //Get the original price of a land from priceTable
    private int getLandPrice(int width, int length){
        return priceTable[width - 1][length - 1];
    }

    //Brute Force Approach
    public int bestPriceByBruteForce(int width, int length){
        return bestPriceByBruteForceHelper(width , length , new ArrayList<>());
    }

    private int bestPriceByBruteForceHelper(int width, int length, ArrayList<Direction> directions){
        //Initialize the best price by original price
        int bestPrice = getLandPrice(width, length);
        int firstPartPrice;
        int secondPartPrice;
        int totalPrice;

        //horizontally subdivide
        //Calculate all possibilities of horizontally subdivision
        for(int x = 1; x < width; x++){
            //Recurse the divided land to find the best value and compare the original price without subdivision
            firstPartPrice = Math.max(getLandPrice(x, length), bestPriceByBruteForceHelper(x, length, directions));
            secondPartPrice = Math.max(getLandPrice(width - x, length), bestPriceByBruteForceHelper(width - x,
                    length, directions));
            //Calculate the best price of two lands
            totalPrice = getTotalValue(firstPartPrice, secondPartPrice, length);
            //best price replaced by total price if total price is higher than best price
            if(totalPrice > bestPrice){
                bestPrice = totalPrice;
            }
        }
        //vertically subdivide
        //Calculate all possibilities of vertically subdivision
        for(int y = 1 ; y < length; y++){
            //Recurse the divided land to find the best value and compare the original price without subdivision
            firstPartPrice = Math.max(getLandPrice(width, y), bestPriceByBruteForceHelper(width, y, directions));
            secondPartPrice = Math.max(getLandPrice(width, length - y), bestPriceByBruteForceHelper(width,
                    length - y, directions));
            //Calculate the best price of two lands
            totalPrice = getTotalValue(firstPartPrice, secondPartPrice, width);
            //best price replaced by total price if total price is higher than best price
            if(totalPrice > bestPrice){
                bestPrice = totalPrice;
            }
        }
        //Return the highest price of the land
        return bestPrice;
    }

    //Greedy Algorithm Approach
    public int bestPriceByGreedyAlgorithm(int width, int length){
        //Get current best option
        //Direction contains the cut direction and the cut place
        Direction direction = greedyOptionForBestPrice(width, length, getLandPrice(width, length));
        //Read the cut direction
        switch (direction.getCutDirection()){
            //If vertical
            case Vertical -> {
                int firstBestPart = bestPriceByGreedyAlgorithm(width, direction.getCutPlace());
                int secondBestPart = bestPriceByGreedyAlgorithm(width, length - direction.getCutPlace());
                direction.setBestPrice(getTotalValue(firstBestPart, secondBestPart, width));
            }
            //If horizontal
            case Horizontal -> {
                int firstBestPart = bestPriceByGreedyAlgorithm(direction.getCutPlace(), length);
                int secondBestPart = bestPriceByGreedyAlgorithm(width - direction.getCutPlace(), length);
                direction.setBestPrice(getTotalValue(firstBestPart, secondBestPart, width));
            }
            //No further action if not require cutting
            case NotRequired -> {}
        }
        //Return the best price
        return direction.getBestPrice();
    }

    //Find greedy option for current situation
    //Almost same approach with brute force, but only process next step after measure of all possibilities.
    //No further action will take if no subdivision make more value than original land.
    public Direction greedyOptionForBestPrice(int width, int length, int bestPrice){
        CutDirection cutDirection = CutDirection.NotRequired;
        int subdivisionPlace = 0;
        int firstPartPrice;
        int secondPartPrice;
        int totalPrice;
        //horizontal subdivision
        for(int x = 1; x < width; x++){
            firstPartPrice = getLandPrice(x, length);
            secondPartPrice = getLandPrice(width - x, length);
            totalPrice = getTotalValue(firstPartPrice, secondPartPrice, length);
            if(totalPrice > bestPrice){
                cutDirection = CutDirection.Horizontal;
                subdivisionPlace = x;
                bestPrice = totalPrice;
            }
        }
        //vertical subdivision
        for(int y = 1; y < length; y++){
            firstPartPrice = getLandPrice(width, y);
            secondPartPrice = getLandPrice(width, length - y);
            totalPrice = getTotalValue(firstPartPrice, secondPartPrice, width);
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
                int originalPrice = getLandPrice(x, y);
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

//        for(int x = 1; x < dynamicProgrammingTable.length; x++){
//            for(int y = 1; y < dynamicProgrammingTable[x].length; y++){
//
//                Integer[] verticalArray = getVerticalArray(x, y, dynamicProgrammingTable);
//                Integer[] horizontalArray = getHorizontalArray(x, y, dynamicProgrammingTable);
//                int horizontalBestPrice = BestPriceForEachLand(horizontalArray, x);
//                int verticalBestPrice = BestPriceForEachLand(verticalArray, y);
//                int originalPrice = getLandPrice(x, y);
//                dynamicProgrammingTable[x][y] = this.compareThreeInteger(horizontalBestPrice, verticalBestPrice, originalPrice);
//            }
//        }
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
//    public void configPriceTable(int width, int length){
//    priceTable = new int[width][length];
//    for(int y = 0; y < priceTable.length; y++){
//        for(int x = 0; x < priceTable[y].length; x++){
//            priceTable[y][x] = (y + x + 1) * 100;
//        }
//    }
    }
