package cur_put_grill.grill;

import cur_put_grill.util.Randomizer;

import static cur_put_grill.grill.Grill.CAPACITY_OF_GARNISH_CONTAINER;

public class GarnishChief extends Thread {

    public static final int PRODUCED_QUANTITY_IN_GRAMS = 500;

    private void work() {

        while (true) {

            int timeToSleep = Grill.getInstance().makeGarnish();

            try {
                Thread.sleep(timeToSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void run() {
        work();
    }
}