public class BackgroundTasks implements Runnable {

    private CGLS cgls;

    public BackgroundTasks(CGLS cgls) {
        this.cgls = cgls;
    }

    @Override
    public void run() {
        cgls.cgls();
    }

}
