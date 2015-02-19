package particle_filter;

/**
 * Created by anish_khattar25 on 2/17/15.
 */
public class ParticleFilterController {
    private EncoderPos encoderPos;
    private Object dataProcessingLock = new Object();
    private boolean newDataReceived = false;

    public ParticleFilterController(int initX, int initY){
        encoderPos = new EncoderPos(initX,initY);
    }

    public void updateFilter(int newX, int newY){
        encoderPos.setCurr_x(newX);
        encoderPos.setCurr_y(newY);
        newDataReceived = true;
        encoderPos.notifyAll();
    }

    private synchronized void processNewData(){
        while(!newDataReceived){
            try {
                dataProcessingLock.wait();
                newDataReceived = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private class FilterRunnable implements Runnable{


        @Override
        public void run() {
            while(true){
                processNewData();
            }
        }
    }

}
