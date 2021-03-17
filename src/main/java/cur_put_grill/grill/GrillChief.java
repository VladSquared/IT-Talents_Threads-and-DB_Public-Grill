package cur_put_grill.grill;

import cur_put_grill.util.Randomizer;

import static cur_put_grill.grill.Grill.CAPACITY_OF_MEAT_CONTAINER;

public class GrillChief extends Thread{


    private void work(){
        while (true) {

            int timeToSleep = Grill.getInstance().makeMeat();

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
