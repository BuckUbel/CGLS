public class Main {

    public static void main(String[] args) throws Exception {

        Output o = new Output(false);

        CGLS cgls = new CGLS("Input/CTAE.dat", "Input/CTbE.dat", "Output/Bilderreihe/o", 1, 1872, 6084);

        cgls.setOutput(o);

        BackgroundTasks bt = new BackgroundTasks(cgls);

        View window = new View(cgls, "Computertomographie", o, 800, 600);
        o.setWindow(window);
        cgls.setView(window);

        Controller controller = new Controller(window, cgls, o, bt);

        window.setController(controller);
        window.createGUI();
        window.finish();

        cgls.inputData();
        o.print("Das Bild ist Albert Einstein.");
    }

}