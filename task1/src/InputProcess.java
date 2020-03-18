import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputProcess {
    private HashMap<Function, BigInteger> primItemMap;
    private HashMap<Function, BigInteger> diffItemMap;
    
    public InputProcess() {
        this.primItemMap = new HashMap<>();
        this.diffItemMap = new HashMap<>();
    }
    
    public HashMap<Function, BigInteger> getPrimItemMap() {
        return primItemMap;
    }
    
    public void setPrimItemMap(HashMap<Function, BigInteger> primItemMap) {
        this.primItemMap = primItemMap;
    }
    
    public HashMap<Function, BigInteger> getDiffItemMap() {
        return diffItemMap;
    }
    
    public void setDiffItemMap(HashMap<Function, BigInteger> diffItemMap) {
        this.diffItemMap = diffItemMap;
    }
    
    /**
     * 从标准输入读取一行多项式，并解析之，结果存入@code{primItemMap}中
     * 保证该多项式@code{primItemMap}中均为指数不为0的幂函数（即无常数项），且相应系数也不为0
     */
    public void parsePoly(String ll) {
        String line = ll.replaceAll("\\s+", "");
        if (!line.equals("")) {
            String reg = this.RegExpGen();
            Pattern pat = Pattern.compile(reg);
            Matcher mat = pat.matcher(line);
            while (mat.find()) {
                String sig = mat.group("sig"); //符号位
                String power = mat.group("power"); //幂函数(系数+指数)
                if (power == null) {
                    String constant = mat.group("const");
                    //由于是求导，故直接丢弃常数项
                } else {
                    String exp = mat.group("exp"); //指数
                    String coef = mat.group("coef"); //系数
                    if (coef == null) {
                        coef = mat.group("sigCoef"); //系数为+1或-1
                    }
                    BigInteger coefficient = this.parseCoef(sig, coef);
                    BigInteger exponent = this.parseVal(exp);
                    if (!(exponent.equals(BigInteger.ZERO) ||
                            coefficient.equals(BigInteger.ZERO))) { //系数或指数为0直接丢弃
                        PowerFunc powerItem = new PowerFunc(exponent);
                        BigInteger oriCoef = primItemMap.put(powerItem, coefficient);
                        if (oriCoef != null) { //同类项合并
                            coefficient = oriCoef.add(coefficient);
                            if (coefficient.equals(BigInteger.ZERO)) {
                                primItemMap.remove(powerItem);
                            } else {
                                primItemMap.put(powerItem, coefficient);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 对多项式进行求导，结果存入@code{diffItemMap}中
     */
    public void diffPoly() {
        for (Map.Entry<Function, BigInteger> functionBigIntegerEntry : primItemMap.entrySet()) {
            Function func = (Function) ((Map.Entry) functionBigIntegerEntry).getKey();
            BigInteger coef = (BigInteger) ((Map.Entry) functionBigIntegerEntry).getValue();
            coef = coef.multiply(func.coefMult());
            Function diffFunc = func.differential();
            diffItemMap.put(diffFunc, coef);
        }
    }
    
    /**
     * 输出@code{diffItemMap}对应的最简导数表达式
     * 注意应优先系数为正者输出
     */
    public void printDiffPoly() {
        
        List<Map.Entry<Function, BigInteger>> sortList = new ArrayList<>(diffItemMap.entrySet());
        sortList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        String output = "";
        boolean isFirst = true;
        for (Map.Entry entry : sortList) {
            Function function = (Function) entry.getKey();
            BigInteger coefficient = (BigInteger) entry.getValue();
            if (isFirst) {
                output = output.concat(itemToString(coefficient, function, true));
                isFirst = false;
            } else {
                output = output.concat(itemToString(coefficient, function, false));
            }
        }
        if (output.equals("")) {
            output = "0";
        }
        System.out.println(output);
    }
    
    private String itemToString(BigInteger coef, Function func, boolean isFirst) {
        String retStr = "";
        if (!isFirst && coef.compareTo(BigInteger.ZERO) > 0) {
            retStr = "+";
        }
        if (func.toString().equals("")) { //指数为0
            return retStr + coef;
        } else {
            if (coef.equals(BigInteger.ONE)) { //系数为1
                return retStr + func.toString();
            } else if (coef.equals(BigInteger.ONE.negate())) { //系数为-1
                return "-" + func.toString();
            } else {
                return retStr + coef + "*" + func.toString();
            }
        }
    }
    
    private BigInteger parseCoef(String sigStr, String coefStr) {
        BigInteger coef = this.parseVal(coefStr);
        if (sigStr == null) {
            return coef;
        } else if (sigStr.equals("-")) {
            coef = coef.negate();
        }
        return coef;
    }
    
    private BigInteger parseVal(String str) {
        BigInteger val;
        if (str == null || str.equals("+")) {
            val = BigInteger.ONE;
        } else if (str.equals("-")) {
            val = BigInteger.ONE.negate();
        } else {
            val = new BigInteger(str);
        }
        return val;
    }
    
    private String RegExpGen() {
        String coefficient = "(((?<coef>[+\\-]?\\d+)\\*)?|(?<sigCoef>[+\\-]))";//系数
        String exponent = "(\\*\\*(?<exp>[+\\-]?\\d+))?";//指数
        String constant = "(?<const>[+\\-]?\\d+)";//常数项
        String powerItem = "(?<power>" + coefficient + "x" + exponent + ")";
        String item = "(?<Item>" + powerItem + "|" + constant + ")";
        String sig = "(?<sig>[+\\-])?";//符号位
        return sig + item;
    }
    
}
