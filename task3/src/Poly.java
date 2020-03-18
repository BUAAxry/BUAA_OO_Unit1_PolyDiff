import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Poly implements Factor {
    private final VarItemSet<Term> termSet;
    
    public Poly() {
        this.termSet = new VarItemSet<>();
    }
    
    public VarItemSet<Term> getTermSet() {
        return termSet;
    }
    
    public void addTerm(Term term) {
        this.termSet.put(term, term.getVal());
    }
    
    public static Poly parsePoly(String prePolyStr) {
        String polyStr = prePolyStr.replaceAll("^[\\t ]+", "");
        polyStr = polyStr.replaceAll("[\\t ]+$", "");
        if (polyStr.equals("")) {
            System.out.println("WRONG FORMAT!");
            System.exit(0);
        }
        Poly poly = new Poly();
        int length = polyStr.length();
        int startIndex = 0;
        int braStack = 0;
        boolean isNextNegative = false;
        for (int i = 0; i < length; i++) {
            switch (polyStr.charAt(i)) {
                case '(':
                    braStack++;
                    break;
                case ')':
                    braStack--;
                    break;
                case '@':
                case '#':
                    if (braStack == 0) {
                        if (i != 0) {
                            String termStr = polyStr.substring(startIndex, i);
                            //System.out.println("isNextNegative:" + isNextNegative);
                            poly.addTermParse(termStr, isNextNegative);
                        }
                        startIndex = i + 1;
                        isNextNegative = polyStr.charAt(i) == '#';
                    }
                    //System.out.println("i:" + i);
                    break;
                default:
                    break;
            }
        }
        if (startIndex < length) {
            String termStr = polyStr.substring(startIndex, length);
            poly.addTermParse(termStr, isNextNegative);
        }
        PolySimplifier polySimplifier = new PolySimplifier();
        return polySimplifier.polySimplify(poly);
    }
    
    private void addTermParse(String termStr, boolean isNegative) {
        Term term = Term.parseTerm(termStr, isNegative);
        Poly poly = term.termToPoly();
        //System.out.println("term:"+term);
        if (poly == null) {
            this.termSet.put(term, term.getVal());
        } else {
            this.termSet.putAll(poly.termSet);
        }
    }
    
    public Factor polyToFac() {
        if (this.termSet.size() == 0) {
            return new ConstFactor(BigInteger.ZERO);
        }
        if (this.termSet.size() == 1) {
            for (Map.Entry<Term, BigInteger> entry : this.termSet.entrySet()) {
                Term term = entry.getKey();
                return term.termToFac();
            }
        }
        return null;
    }
    
    public Term polyToTerm() {
        if (this.termSet.size() == 1) {
            for (Map.Entry<Term, BigInteger> entry : this.termSet.entrySet()) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    //返回一个去除了特定项后的原多项式的拷贝
    public Poly removeTerm(Term term) {
        Poly poly = this.copyOf();
        poly.termSet.remove(term);
        return poly;
    }
    
    //整体乘另一项
    public Poly multiTerm(Term term) {
        Poly retPoly = new Poly();
        for (Map.Entry<Term, BigInteger> entry : this.termSet.entrySet()) {
            Term newTerm = entry.getKey().copyOf();
            newTerm.addTerm(term.copyOf());
            retPoly.termSet.put(newTerm, newTerm.getVal());
        }
        return retPoly;
    }
    
    //整体乘系数
    public Poly multiCoe(ConstFactor coe) {
        Poly poly = new Poly();
        for (Map.Entry<Term, BigInteger> entry : this.termSet.entrySet()) {
            Term newTerm = entry.getKey().copyOf();
            newTerm.addFactor(coe);
            poly.termSet.put(newTerm, newTerm.getVal());
        }
        return poly;
    }
    
    //+
    public void addPoly(Poly subPoly) {
        if (subPoly != null && subPoly.termSet.size() != 0) {
            for (Map.Entry entry : subPoly.termSet.entrySet()) {
                Term term = (Term) entry.getKey();
                this.termSet.put(term, term.getVal());
            }
        }
    }
    
    @Override
    public Term diff() {
        if (this.termSet.size() == 0) {
            return new Term(new ConstFactor(BigInteger.ZERO));
        } else {
            Term newTerm = new Term();
            Poly diffPoly = new Poly();
            for (Map.Entry entry : this.termSet.entrySet()) {
                Term term = (Term) entry.getKey();
                diffPoly.addPoly(term.diff());
            }
            Factor diffFac = diffPoly.polyToFac();
            if (diffFac != null) {
                newTerm.addFactor(diffFac);
            } else {
                PolySimplifier polySimplifier = new PolySimplifier();
                Poly simPoly = polySimplifier.polySimplify(diffPoly);
                newTerm.addFactor(simPoly);
            }
            return newTerm;
        }
    }
    
    @Override
    public boolean isZero() {
        if (this.termSet.size() == 0) {
            return true;
        } else {
            for (Map.Entry entry : termSet.entrySet()) {
                Term term = (Term) entry.getKey();
                if (!term.isZero()) {
                    return false;
                }
            }
            return true;
        }
    }
    
    @Override
    public Poly copyOf() {
        Poly poly = new Poly();
        for (Map.Entry entry : this.termSet.entrySet()) {
            Term term = ((Term) entry.getKey()).copyOf();
            poly.termSet.put(term, term.getVal());
        }
        return poly;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof Poly) {
            Poly poly = (Poly) o;
            if (poly.termSet.size() == this.termSet.size()) {
                boolean flag = true;
                for (Map.Entry entry : this.termSet.entrySet()) {
                    Term term = (Term) entry.getKey();
                    BigInteger coe = (BigInteger) entry.getValue();
                    if (!(poly.termSet.containsKey(term) && poly.termSet.get(term).equals(coe))) {
                        flag = false;
                        break;
                    }
                }
                return flag;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        for (Map.Entry entry : this.termSet.entrySet()) {
            BigInteger coe = (BigInteger) entry.getValue();
            Term term = (Term) entry.getKey();
            code ^= coe.hashCode();
            code ^= term.hashCode();
        }
        return code;
    }
    
    @Override
    public String toString() {
        Set<Map.Entry<Term, BigInteger>> entries = this.termSet.entrySet();
        List<Map.Entry<Term, BigInteger>> sortList = new ArrayList<>(entries);
        sortList.sort((o1, o2) -> o2.getKey().getVal().compareTo(o1.getKey().getVal()));
        
        String polyStr = "";
        for (Map.Entry<Term, BigInteger> termEntry : sortList) {
            //System.out.println(((Map.Entry) termTermEntry).getKey().toString());
            polyStr = polyStr.concat(termEntry.getKey().toString());
        }
        if (polyStr.equals("")) {
            polyStr = "0";
        } else if (polyStr.charAt(0) == '+') {
            polyStr = polyStr.substring(1);
        }
        return polyStr;
    }
    
}
