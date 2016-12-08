import com.leapmotion.leap.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LeapMotionLoger {

       public static void main(String[] args) {
           
        // Create a sample listener and controller
        SampleListener listener = new SampleListener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);
        
        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException ex) {
            //e.printStackTrace();
            Logger.getLogger(LeapMotionLoger.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }
}

class SampleListener extends Listener {

    @Override
    public void onConnect(Controller controller) {
        System.out.println("Connected");
        String header = "Palm_Position_X,Palm_Position_Y,Palm_Position_Z,Palm_Speed_X,Palm_Speed_Y,Palm_Speed_Z,Is_Right_Hand";
        CreateCSVFile(header);
    }
    
    @Override
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        
        Vector palmPosition = frame.hands().frontmost().palmPosition();
        Vector handSpeed = frame.hands().frontmost().palmVelocity();
        boolean isRight = frame.hands().frontmost().isRight();
        
        String data = palmPosition.getX() + "," + palmPosition.getY() + "," + palmPosition.getZ() + "," + handSpeed.getX() + "," + handSpeed.getY() + "," + handSpeed.getZ() + "," + isRight + "\r\n";
        //String data = palmPosition.getX() + "," + palmPosition.getY() + "," + palmPosition.getZ() + "\r\n";
        System.out.println("Palm X, Y, Z Position: " + palmPosition + " - PalmSpeed:" + handSpeed + " - Rechts?: " + isRight + "\r\n");
        
        AppendRowIntoCSVFile(data);
    }
    
    
    @Override
    public void onExit(Controller controller) {
        System.out.println("Exit");
        CloseCSVFile();
    }
    
    // Helper Functions To Save The Data Into CSV File
    static FileWriter fw;
    static File file;
    static Boolean CreateCSVFile(String header)
        {
            String dirName = System.getProperty("user.dir");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");  
            file = new File( dirName + "\\"+ df.format(new Date()) +"_Statistics.csv");  
            
            if ( !file.exists() )
            try {
                    file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(LeapMotionLoger.class.getName()).log(Level.SEVERE, null, ex);
                    //ex.printStackTrace();
                    return false;
                }
            try {
                fw = new FileWriter(file,true);
                //writer = new BufferedWriter( fw );
                fw.write(header + "\r\n");
                //fw.flush();
                //fw.close();
                return true;
            } catch (IOException ex) {
                Logger.getLogger(LeapMotionLoger.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
    static Boolean AppendRowIntoCSVFile(String row)
    {
        try {
                fw.write(row);
                return true;
            } catch (IOException ex) {
                Logger.getLogger(LeapMotionLoger.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
    }
    static boolean CloseCSVFile()
    {
        try {
            fw.flush();
            fw.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(SampleListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
