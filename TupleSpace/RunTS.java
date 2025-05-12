package TupleSpace;

import org.jspace.*;

/**
 * @author Jens Van der Plas
 */
public class RunTS {

    /** Creates a tuple space which is made available over tcp and keeps running until the TS contains a tuple ("quitTS"). */
    public static void main(String[] args) throws InterruptedException {
        SpaceRepository repository = new SpaceRepository();
        Space ts = new RandomSpace();
        repository.add("ts", ts);
        repository.addGate("tcp://localhost:10101/?");
        // When running, the TS size will be printed every 5 seconds.
        while (ts.getp(new ActualField("quitTS")) == null) {
            Thread.sleep(5000);
            System.out.print(ts.size() + " ");
        }
        System.out.println("Quitting.");
    }

}
