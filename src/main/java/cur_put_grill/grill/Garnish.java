package cur_put_grill.grill;

enum GarnishType{
    RUSSIAN(1.5, 10, 1),
    LIUTEBUTSA(1.1, 8, 2),
    SNOW_WHITE(1.2, 4, 3),
    CABBAGE_ADN_CARROTS(0.8, 2, 4),
    TOMATOES_AND_CUCUMBERS(1.3, 3, 5);

    private double price;
    private int timeInSeconds;
    private int iDinDB;

    GarnishType(double price, int timeInSeconds, int DbId){
        this.price = price;
        this.timeInSeconds = timeInSeconds;
        this.iDinDB = DbId;
    }

    public double getPrice() {
        return price;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public int getIdInDB() {
        return iDinDB;
    }
}

