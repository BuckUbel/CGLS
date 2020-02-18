import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller implements ActionListener {

    static final String INPUT = "INPUT";
    static final String CGLS = "CGLS";
    static final String INVERTED = "INVERTED";
    static final String TOLERANCE = "TOLERANCE";
    static final String STOP = "STOP";

    CGLS cgls;
    View window;
    Output output;
    BackgroundTasks bt;

    Thread lastThread;

    public Controller(View window, CGLS cgls, Output o, BackgroundTasks bt) {
        this.cgls = cgls;
        this.window = window;
        this.output = o;
        this.bt = bt;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();

        switch (command) {
            case Controller.INPUT:

                try {
                    cgls.inputData();
                } catch (Exception err) {

                    output.print("File Error: " + err.getMessage());
                }

                break;
            case Controller.CGLS:

                window.imageCounter=0;

                Thread t = new Thread(this.bt);
                lastThread = t;
                t.start();

                break;

            case Controller.STOP:

                window.imageCounter=0;
                lastThread.stop();

                break;
            case Controller.INVERTED:

                cgls.setInverted(!cgls.inverted);
                break;

            case Controller.TOLERANCE:

                cgls.setTolerance( Math.pow(10, window.tolSlider.getValue()));

                break;
            default:
                break;
        }

    }


}
