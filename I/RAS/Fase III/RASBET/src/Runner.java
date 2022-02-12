import Entidades.Subject;

public class Runner implements Runnable{

    public Runner() {
    }

    @Override
    public void run() {
        Subject s = Subject.getInstance();
        while(true){
            try {
                wait(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}

