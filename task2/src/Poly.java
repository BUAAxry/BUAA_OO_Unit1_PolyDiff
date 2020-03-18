import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Poly implements RegStr {
    private final HashMap<Term, BigInteger> termMap;
    
    public Poly() {
        this.termMap = new HashMap<>();
    }
    
    public Poly(Term t) {
        this.termMap = new HashMap<>();
        termMap.put(t, t.getCoe());
    }
    
    public HashMap<Term, BigInteger> getTermMap() {
        return termMap;
    }
    
    //解析多项式
    public void parse(String polyStr) {
        String regTerm = "(?<sig>[+\\-])?" + "(?<term>" + TERM_REG + ")";
        Pattern pat = Pattern.compile(regTerm);
        Matcher mat = pat.matcher(polyStr);
        while (mat.find()) {
            String termStr = mat.group("term");
            boolean isNegate = false;
            if (mat.group("sig") != null) {
                if (mat.group("sig").equals("-")) {
                    isNegate = true;
                }
            }
            Term term = new Term();
            term.parse(termStr, isNegate);
            //System.out.println("term:"+term.toString());
            this.addCopyOfTerm(term);
        }
    }
    
    public Poly diff() {
        Poly diffPoly = new Poly();
        for (Map.Entry<Term, BigInteger> termEntry : termMap.entrySet()) {
            Term term = (Term) ((Map.Entry) termEntry).getKey();
            Poly subPoly = term.diff();
            diffPoly.addPoly(subPoly);
        }
        return diffPoly;
    }
    
    //合并多项式
    public void addPoly(Poly subPoly) {
        HashMap<Term, BigInteger> subTermMap = subPoly.getTermMap();
        if (subTermMap != null && subTermMap.size() != 0) {
            for (Map.Entry<Term, BigInteger> termEntry : subTermMap.entrySet()) {
                this.addCopyOfTerm(termEntry.getKey());
            }
        }
    }
    
    //常数因子为0的项将被舍弃
    public void addCopyOfTerm(Term newTerm) {
        if (!newTerm.isZero()) {
            Term term = newTerm.copyOf();
            BigInteger oriCoe = this.termMap.put(term, term.getCoe());
            if (oriCoe != null) {
                this.termMap.remove(term);
                Term t = term.addCoe(oriCoe);
                if (!t.isZero()) {
                    this.termMap.put(t, t.getCoe());
                }
            }
        }
    }
    
    //生成一个不含termA,termB的子多项式
    public Poly removeTerms(Term oldTermA, Term oldTermB) {
        Poly newPoly = new Poly();
        for (Map.Entry<Term, BigInteger> termEntry : this.termMap.entrySet()) {
            Term curTerm = termEntry.getKey();
            if (!curTerm.equals(oldTermA)) {
                if (!curTerm.equals(oldTermB)) {
                    newPoly.addCopyOfTerm(curTerm);
                }
            }
        }
        return newPoly;
    }
    
    @Override
    public String toString() {
        //优先系数为正者输出
        List<Map.Entry<Term, BigInteger>> sortList = new ArrayList<>(this.termMap.entrySet());
        sortList.sort((o1, o2) -> o2.getKey().getCoe().compareTo(o1.getKey().getCoe()));
        
        String polyStr = "";
        for (Map.Entry<Term, BigInteger> termTermEntry : sortList) {
            //System.out.println(((Map.Entry) termTermEntry).getKey().toString());
            polyStr = polyStr.concat(((Map.Entry) termTermEntry).getKey().toString());
        }
        if (polyStr.equals("")) {
            polyStr = "0";
        } else if (polyStr.charAt(0) == '+') {
            polyStr = polyStr.substring(1);
        }
        return polyStr;
    }
    
    @Override
    public int hashCode() {
        int code = 1;
        for (Map.Entry<Term, BigInteger> termEntry : this.termMap.entrySet()) {
            BigInteger coe = termEntry.getValue();
            code ^= coe.hashCode();
        }
        return code;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof Poly) {
            Poly poly = (Poly) o;
            if (poly.getTermMap().size() == this.getTermMap().size()) {
                boolean flag = true;
                for (Map.Entry<Term, BigInteger> termEntry : this.termMap.entrySet()) {
                    Term t = termEntry.getKey();
                    BigInteger coe = termEntry.getValue();
                    if (!(poly.termMap.containsKey(t) && poly.termMap.get(t).equals(coe))) {
                        flag = false;
                        break;
                    }
                }
                //System.out.println("flag:" + flag);
                return flag;
            }
            return false;
        }
        return false;
    }
}
