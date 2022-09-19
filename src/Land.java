public class Land {

    private static Integer[][] priceTable;
    public Land firstLand;
    public Land secondLand;
    public int width;
    public int height;
    public Direction subdivision;

    public Land(int width, int height) {
        this.width = width;
        this.height = height;
        firstLand = null;
        secondLand = null;
        subdivision = null;
    }

    public Land getFirstLand() {
        return firstLand;
    }

    public void setFirstLand(Land firstLand) {
        this.firstLand = firstLand;
    }

    public Land getSecondLand() {
        return secondLand;
    }

    public void setSecondLand(Land secondLand) {
        this.secondLand = secondLand;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Direction getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(Direction subdivision) {
        this.subdivision = subdivision;
    }

    //Get the original price of a land from priceTable
    public int getLandPrice(){
        return priceTable[width - 1][height - 1];
    }

    public int getBestPrice(){
        int bestPrice = priceTable[width - 1][height - 1];
        if(subdivision != null){
            bestPrice = subdivision.getBestPrice();
        }
        return bestPrice;
    }

    //Initialize Land Price
    public static void configPriceTable(){
        priceTable = new Integer[6][6];
        priceTable[0] = new Integer[]{20,40,100,130,150,200};
        priceTable[1] = new Integer[]{40,140,250,320,400,450};
        priceTable[2] = new Integer[]{100,250,350,420,450,500};
        priceTable[3] = new Integer[]{130,320,420,500,600,700};
        priceTable[4] = new Integer[]{150,400,450,600,700,800};
        priceTable[5] = new Integer[]{200,450,500,700,800,900};
    }
}
