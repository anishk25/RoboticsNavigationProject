package usb_manager;

/**
 * Created by anish_khattar25 on 2/17/15.
 */
public class USBDeviceConnectionException extends Exception{
    public USBDeviceConnectionException(){};

    public USBDeviceConnectionException(String msg){
        super(msg);
    }
}
