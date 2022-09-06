import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlotLandCalculator {

    private Integer[][] priceTable;
    private final int subdivideFee;

    public static void main(String[] args){
        PlotLandCalculator calculator = new PlotLandCalculator(50);
        calculator.configPriceTable();
        System.out.println(PlotLandCalculator.printTwoDimensionalArrayTable(calculator.priceTable, true));
        System.out.println(calculator.bestPriceByBruteForce(3, 6));
        System.out.println(calculator.bestPriceByDynamicProgramming(3, 6));
        System.out.println(calculator.bestPriceByGreedyAlgorithm(3, 6));
    }

    public PlotLandCalculator(int subdivideFee) {
        this.subdivideFee = subdivideFee;
    }

    //Common Function
    private int getTotalValue(int firstPartPrice, int secondPartPrice, int subdivideLength){
        return (firstPartPrice + secondPartPrice - (subdivideLength * subdivideFee));
    }

    private int getLandPrice(int width, int length){
        return priceTable[width - 1][length - 1];
    }

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

    //Brute Force Approach
    public int bestPriceByBruteForce(int width, int length){
        return bestPriceByBruteForceHelper(width , length , new ArrayList<>());
    }

    public int bestPriceByBruteForceHelper(int width, int length, ArrayList<Direction> directions){
        int bestPrice = getLandPrice(width, length);
        int firstPartPrice;
        int secondPartPrice;
        int totalPrice;

        //horizontally subdivide
        for(int x = 1; x < width; x++){
            firstPartPrice = Math.max(getLandPrice(x, length), bestPriceByBruteForceHelper(x, length, directions));
            secondPartPrice = Math.max(getLandPrice(width - x, length), bestPriceByBruteForceHelper(width - x, length, directions));
            totalPrice = getTotalValue(firstPartPrice, secondPartPrice, length);
            if(totalPrice > bestPrice){
                bestPrice = totalPrice;
            }
        }
        //vertically subdivide
        for(int y = 1 ; y < length; y++){
            firstPartPrice = Math.max(getLandPrice(width, y), bestPriceByBruteForceHelper(width, y, directions));
            secondPartPrice = Math.max(getLandPrice(width, length - y), bestPriceByBruteForceHelper(width, length - y, directions));
            totalPrice = getTotalValue(firstPartPrice, secondPartPrice, width);
            if(totalPrice > bestPrice){
                bestPrice = totalPrice;
            }
        }

        return bestPrice;
    }

    //Greedy Algorithm Approach
    public int bestPriceByGreedyAlgorithm(int width, int length){
        Direction direction = greedyOptionForBestPrice(width, length, getLandPrice(width, length));
        switch (direction.getCutDirection()){
            case Vertical -> {
                int firstBestPart = bestPriceByGreedyAlgorithm(width, direction.getCutPlace());
                int secondBestPart = bestPriceByGreedyAlgorithm(width, length - direction.getCutPlace());
                direction.setBestPrice(getTotalValue(firstBestPart, secondBestPart, width));
            }
            case Horizontal -> {
                int firstBestPart = bestPriceByGreedyAlgorithm(direction.getCutPlace(), length);
                int secondBestPart = bestPriceByGreedyAlgorithm(width - direction.getCutPlace(), length);
                direction.setBestPrice(getTotalValue(firstBestPart, secondBestPart, width));
            }
            default -> {}
        }
        return direction.getBestPrice();
    }

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

    //Dynamic Programming Approach
    public int bestPriceByDynamicProgramming(int width, int length){
        Integer[][] dynamicProgrammingTable = dynamicProgrammingTableConfig(width, length);
        for(int x = 1; x < dynamicProgrammingTable.length; x++){
            for(int y = 1; y < dynamicProgrammingTable[x].length; y++){

                Integer[] verticalArray = getVerticalArray(x, y, dynamicProgrammingTable);
                Integer[] horizontalArray = getHorizontalArray(x, y, dynamicProgrammingTable);
                int horizontalBestPrice = BestPriceForEachLand(horizontalArray, x);
                int verticalBestPrice = BestPriceForEachLand(verticalArray, y);
                int originalPrice = getLandPrice(x, y);
                dynamicProgrammingTable[x][y] = this.compareThreeInteger(horizontalBestPrice, verticalBestPrice, originalPrice);
            }
        }
        int rowLength = dynamicProgrammingTable.length - 1;
        int columnLength = dynamicProgrammingTable[rowLength].length - 1;
        return dynamicProgrammingTable[rowLength][columnLength];
    }

    public Integer[][] dynamicProgrammingTableConfig(int width, int length) throws IllegalArgumentException {
        if(width <= 0 || length <= 0){
            throw new IllegalArgumentException();
        }
        Integer[][] dynamicProgrammingTable = new Integer[width + 1][length + 1];
        for(int x = 0; x < dynamicProgrammingTable.length; x++){
            dynamicProgrammingTable[x][0] = 0;
        }
        for(int y = 1; y < dynamicProgrammingTable[0].length; y++){
            dynamicProgrammingTable[0][y] = 0;
        }
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

    private Integer[] getVerticalArray(int width, int length, Integer[][] dynamicProgrammingTable){
        List<Integer> resultList = new ArrayList<>();
        for(int y = 1; y < width; y++){
            resultList.add(dynamicProgrammingTable[y][length]);
        }
        return listToArray(resultList);
    }

    private Integer[] getHorizontalArray(int width, int length,  Integer[][] dynamicProgrammingTable){
        List<Integer> resultList = new ArrayList<>();
        for(int x = 0; x < length - 1; x++){
            resultList.add(dynamicProgrammingTable[width][x + 1]);
        }
        return listToArray(resultList);
    }

    private int BestPriceForEachLand(Integer[] referenceArray, int subdivisionLength){
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


    //Config Land Price
    public void configPriceTable(){
        priceTable = new Integer[6][6];
        priceTable[0] = new Integer[]{20,40,100,130,150,200};
        priceTable[1] = new Integer[]{40,140,250,320,400,450};
        priceTable[2] = new Integer[]{100,250,350,420,450,500};
        priceTable[3] = new Integer[]{130,320,420,500,600,700};
        priceTable[4] = new Integer[]{150,400,450,600,700,800};
        priceTable[5] = new Integer[]{200,450,500,700,800,900};
    }

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

//    public void configPriceTable(int width, int length){
//    priceTable = new int[width][length];
//    for(int y = 0; y < priceTable.length; y++){
//        for(int x = 0; x < priceTable[y].length; x++){
//            priceTable[y][x] = (y + x + 1) * 100;
//        }
//    }
    }
