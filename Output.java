public class Output {

    View window;
    Boolean system = true;

    public Output() {
    }

    public Output(boolean system) {
        this.system = system;
    }

    public void setWindow(View window){
        this.window = window;
    }

    public void print(String s) {

        if (system) {
            System.out.println(s);

        } else {
            this.window.write(s);

        }
    }


}
