package cur_put_grill.grill;

public class Cashier extends Thread{

    public static final int PORTION_GARNISH = 200; //grams

    private void work(){
        while(true){
            Client client = Grill.getInstance().getOrder();

            MeatType meatType = client.getMeatType();
            BreadType breadType = client.getBreadType();
            GarnishType garnishType = client.getGarnishType();

            Grill.getInstance().getAMeat(meatType);
            Grill.getInstance().getABread(breadType);
            Grill.getInstance().get200GramsGarnish(garnishType);

            GrillDAO.saveOrderInFile(client);
        }
    }

    @Override
    public void run() {
        work();
    }
}
