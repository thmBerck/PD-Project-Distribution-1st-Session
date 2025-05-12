package TupleSpace;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Jens Van der Plas
 */
public class TSClient {

    /** Connects to the tuple space created by RunTS, and writes and reads a tuple. */
    public static void main(String[] args) throws IOException, InterruptedException {
        RemoteSpace ts = new RemoteSpace("tcp://localhost:10101/ts?keep");
        ts.put("field1", "field2", 1, 2);
        Object[] tuple = ts.get(new FormalField(String.class), new ActualField("field2"), new FormalField(Integer.class), new ActualField(2));
        System.out.println(Arrays.toString(tuple));
        System.out.println((int) tuple[2] + 1);
    }
}
