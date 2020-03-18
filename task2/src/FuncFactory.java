import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuncFactory implements RegStr {
    public Function getNewFunc(String factorStr) {
        Function function;
        Pattern pat = Pattern.compile(SIG_INT_REG);
        Matcher mat = pat.matcher(factorStr);
        BigInteger exp = BigInteger.ONE;
        //System.out.println("factor:"+factorStr);
        if (factorStr.contains("sin(x)")) {
            if (mat.find()) {
                exp = new BigInteger(mat.group());
            }
            //System.out.println("exp:"+exp);
            function = new SinFunc(exp);
        } else if (factorStr.contains("cos(x)")) {
            if (mat.find()) {
                exp = new BigInteger(mat.group());
            }
            //System.out.println("exp:"+exp);
            function = new CosFunc(exp);
        } else if (factorStr.contains("x")) {
            if (mat.find()) {
                exp = new BigInteger(mat.group());
            }
            //System.out.println("exp:"+exp);
            function = new PowFunc(exp);
        } else {
            BigInteger constant = new BigInteger(factorStr);
            //System.out.println("const:"+constant);
            function = new ConstFunc(constant);
        }
        return function;
    }
}
