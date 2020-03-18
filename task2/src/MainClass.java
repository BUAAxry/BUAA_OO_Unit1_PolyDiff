import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainClass implements RegStr {
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) {
        try {
            String line = in.readLine();
            final long startTime = System.currentTimeMillis();
            line = line.replaceAll("[\\t ]+", "");
            try {
                if (line.matches(POLY_REG)) {
                    Poly oriPoly = new Poly();
                    oriPoly.parse(line);
        
                    //PolySimplifier oriPolySimplifier = new PolySimplifier();
        
                    PolySimplifier newPolySimplifier = new PolySimplifier();
                    Poly diffPoly = oriPoly.diff();
                    //System.out.println(diffPoly.toString());
                    Poly simPoly = newPolySimplifier.simplify(diffPoly);
                    System.out.println(simPoly.toString());
                    double rate = diffPoly.toString().length() * 1.0 / simPoly.toString().length();
                    System.out.println("Optimization rate: " + rate);
                    long endTime = System.currentTimeMillis();
                    System.out.println("total Time: " + (endTime - startTime) + "ms");
                } else {
                    System.out.println("WRONG FORMAT!");
                }
            } catch (StackOverflowError e) {
                System.out.println("WRONG FORMAT!");
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
