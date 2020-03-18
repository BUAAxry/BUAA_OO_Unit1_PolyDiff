import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) {
        try {
            String line = in.readLine();
            String diffPoly = expDiff(line);
            System.out.println(diffPoly);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String expDiff(String line) {
        String prePoly = PreParser.preParse(line);
        //System.out.println(prePoly);
        Poly oriPoly = Poly.parsePoly(prePoly);
        //System.out.println("oriPoly:" + oriPoly.toString());
        Term diffTerm = oriPoly.diff();
        Poly diffPoly = diffTerm.termToPoly();
        String diffStr;
        if (diffPoly != null) {
            diffStr = diffPoly.toString();
        } else {
            diffStr = diffTerm.toString();
        }
        if (diffStr.equals("")) {
            diffStr = "0";
        } else if (diffStr.startsWith("+")) {
            diffStr = diffStr.substring(1);
        }
        return diffStr;
    }
    
}
