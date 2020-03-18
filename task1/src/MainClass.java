import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainClass {
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    public BufferedReader getIn() {
        return in;
    }
    
    public static void main(String[] args) {
        String line = null;
        try {
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputProcess sysInput = new InputProcess();
        assert line != null;
        sysInput.parsePoly(line);
        sysInput.diffPoly();
        sysInput.printDiffPoly();
    }
}
