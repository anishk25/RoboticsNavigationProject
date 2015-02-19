package particle_filter;

/**
 * Created by anish_khattar25 on 2/17/15.
 */
public class EncoderPos {
    private int curr_x, curr_y;

    public EncoderPos(int initX, int initY){
        this.curr_x = initX;
        this.curr_y = initY;
    }

    public int getCurr_x() {
        return curr_x;
    }

    public void setCurr_x(int curr_x) {
        this.curr_x = curr_x;
    }

    public int getCurr_y() {
        return curr_y;
    }

    public void setCurr_y(int curr_y) {
        this.curr_y = curr_y;
    }
}
