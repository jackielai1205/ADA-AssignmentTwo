public class Direction{

    private final CutDirection cutDirection;
    private final int cutPlace;
    private int bestPrice;

    public Direction(CutDirection cutDirection, int distance, int bestPrice) {
        this.cutDirection = cutDirection;
        this.cutPlace = distance;
        this.bestPrice = bestPrice;
    }

    public CutDirection getCutDirection() {
        return cutDirection;
    }

    public int getCutPlace() {
        return cutPlace;
    }

    public int getBestPrice() {
        return bestPrice;
    }

    public void setBestPrice(int bestPrice) {
        this.bestPrice = bestPrice;
    }
}
