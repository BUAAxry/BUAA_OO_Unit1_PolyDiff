import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

public class Term implements VarItem {
    private final ConstFactor coeFactor;
    private final VarItemSet<VarItem> varFacSet;
    private final ArrayList<Poly> polyList;
    private final FactorFactory factorFactory;
    
    public VarItemSet<VarItem> getVarFacSet() {
        return varFacSet;
    }
    
    public ArrayList<Poly> getPolyList() {
        return polyList;
    }
    
    public Term() {
        this.coeFactor = new ConstFactor(BigInteger.ONE);
        this.varFacSet = new VarItemSet<>();
        this.polyList = new ArrayList<>();
        this.factorFactory = new FactorFactory();
    }
    
    public Term(ConstFactor coeFactor, VarItemSet<VarItem> varItemSet) {
        this.coeFactor = coeFactor;
        this.varFacSet = varItemSet;
        this.polyList = new ArrayList<>();
        this.factorFactory = new FactorFactory();
    }
    
    public Term(ConstFactor coeFactor) {
        this.coeFactor = coeFactor;
        this.varFacSet = new VarItemSet<>();
        this.polyList = new ArrayList<>();
        this.factorFactory = new FactorFactory();
    }
    
    public static Term parseTerm(String preTermStr, boolean multiNegate) {
        String termStr = preTermStr.replaceAll("^[\\t ]+", "");
        termStr = termStr.replaceAll("[\\t ]+$", "");
        boolean isNegate = multiNegate;
        Term term = new Term();
        int startIndex = 0;
        int braStack = 0;
        int length = termStr.length();
        char firstChar = termStr.charAt(0);
        if ((firstChar == '+' || firstChar == '-') && !Character.isDigit(preTermStr.charAt(1))) {
            startIndex = 1;
            if (firstChar == '-') {
                isNegate = !isNegate;
            }
        }
        //System.out.println("isNegate:" + isNegate);
        if (isNegate) {
            term.coeFactor.multi(new ConstFactor(new BigInteger("-1")));
        }
        for (int i = 0; i < length; i++) {
            switch (termStr.charAt(i)) {
                case '(':
                    braStack++;
                    break;
                case ')':
                    braStack--;
                    break;
                case '*':
                    if (braStack == 0) {
                        String facStr = termStr.substring(startIndex, i);
                        term.addFacParse(facStr);
                        startIndex = i + 1;
                    }
                    break;
                default:
                    break;
            }
        }
        if (startIndex < length) {
            String facStr = termStr.substring(startIndex, length);
            term.addFacParse(facStr);
        }
        /*
        对于项数为1的表达式因子，应尝试合并（解析时进行）
        对表达式列表进行排序，以便比较
        判断项是否需要转化成表达式（由上层判断）
         */
        term.sortPolyList();
        return term;
    }
    
    private void addFacParse(String facStr) {
        Factor factor = this.factorFactory.getNewFac(facStr);
        this.addFactor(factor);
    }
    
    //乘因子
    public void addFactor(Factor factor) {
        if (factor.isZero()) {
            this.coeFactor.multi(new ConstFactor(BigInteger.ZERO));
        } else if (!factor.toString().equals("")) {
            if (factor instanceof Poly) {
                Term polyTerm = ((Poly) factor).polyToTerm();
                if (polyTerm != null) {
                    this.addTerm(polyTerm);
                    //System.out.println("newTerm:" + this.toString());
                } else {
                    this.polyList.add((Poly) factor);
                }
            } else if (factor instanceof ConstFactor) {
                this.coeFactor.multi((ConstFactor) factor);
            } else {
                VarItem varItem = (VarItem) factor;
                this.varFacSet.put(varItem, varItem.getVal());
            }
        }
    }
    
    public void addTerm(Term newTerm) {
        if (newTerm != null) {
            this.coeFactor.multi(newTerm.coeFactor);
            this.polyList.addAll(newTerm.polyList);
            this.varFacSet.putAll(newTerm.varFacSet);
        }
    }
    
    //解析时化简
    public Poly termToPoly() {
        if (varFacSet.size() == 0 && polyList.size() == 1) {
            if (this.coeFactor.getValue().equals(BigInteger.ONE)) {
                return polyList.get(0);
            } else if (this.coeFactor.getValue().equals(new BigInteger("-1"))) {
                return polyList.get(0).multiCoe(this.coeFactor);
            }
        }
        return null;
    }
    
    public Factor termToFac() {
        if (this.polyList.size() == 0) {
            if (this.coeFactor.getValue().equals(BigInteger.ONE) && this.varFacSet.size() == 1) {
                for (Map.Entry entry : this.varFacSet.entrySet()) {
                    return (Factor) entry.getKey();
                }
            } else if (this.varFacSet.size() == 0) {
                return this.coeFactor;
            }
        }
        return null;
    }
    
    private void sortPolyList() {
        this.polyList.sort((o1, o2) -> {
            if (o1.getTermSet().size() == o2.getTermSet().size()) {
                return o1.hashCode() - o2.hashCode();
            } else {
                return o1.getTermSet().size() - o2.getTermSet().size();
            }
        });
    }
    
    public Poly diff() {
        Poly diffVarPoly = this.diffVarFacSet();
        Poly diffPolyPoly = this.diffPolyList();
        if (diffVarPoly == null && diffPolyPoly == null) {
            return null;
        }
        Poly newPoly = new Poly();
        newPoly.addPoly(diffPolyPoly);
        newPoly.addPoly(diffVarPoly);
        return newPoly;
        //求导结束后记得对每一项的多项式因子排序
    }
    
    private Poly diffVarFacSet() {
        if (varFacSet.size() == 0) {
            return null;
        }
        Poly newPoly = new Poly();
        for (Map.Entry entryI : this.varFacSet.entrySet()) {
            Factor varFacI = (Factor) entryI.getKey();
            Term diffTerm = varFacI.diff();
            if (diffTerm != null) {
                for (Map.Entry entryJ : this.varFacSet.entrySet()) {
                    Factor varFacJ = (Factor) entryJ.getKey();
                    if (!varFacI.equals(varFacJ)) {
                        diffTerm.addFactor(varFacJ.copyOf());
                    }
                }
                for (Poly poly : polyList) {
                    diffTerm.addFactor(poly.copyOf());
                }
                diffTerm.sortPolyList();
                diffTerm.addFactor(this.coeFactor);
                Poly simPoly = diffTerm.termToPoly();
                if (simPoly != null) {
                    newPoly.addPoly(simPoly);
                } else {
                    newPoly.addTerm(diffTerm);
                }
            }
        }
        return newPoly;
    }
    
    private Poly diffPolyList() {
        if (polyList.size() == 0) {
            return null;
        }
        Poly newPoly = new Poly();
        for (int i = 0; i < polyList.size(); i++) {
            Term diffTerm = polyList.get(i).diff();
            if (diffTerm != null) {
                for (int j = 0; j < polyList.size(); j++) {
                    if (i != j) {
                        diffTerm.addFactor(polyList.get(j).copyOf());
                    }
                }
                varFacSet.forEach((key, value) -> {
                    diffTerm.addFactor(((Factor) key).copyOf());
                });
                diffTerm.sortPolyList();
                diffTerm.addFactor(this.coeFactor);
                Poly simPoly = diffTerm.termToPoly();
                if (simPoly != null) {
                    newPoly.addPoly(simPoly);
                } else {
                    newPoly.addTerm(diffTerm);
                }
            }
        }
        return newPoly;
    }
    
    private boolean isConstant() {
        return this.polyList.size() == 0 && this.varFacSet.size() == 0;
    }
    
    public boolean isZero() {
        return this.coeFactor.isZero();
    }
    
    @Override
    public BigInteger getVal() {
        return coeFactor.getValue();
    }
    
    //同类项合并(+)
    @Override
    public void addVal(BigInteger value) {
        this.coeFactor.addValue(value);
    }
    
    public Term copyOf() {
        Term term = new Term(this.coeFactor.copyOf());
        this.varFacSet.forEach((key,value) -> term.addFactor(((Factor) key).copyOf()));
        this.polyList.forEach(poly -> term.polyList.add(poly.copyOf()));
        return term;
    }
    
    //非常数因子相同即相同
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof Term) {
            Term term = (Term) o;
            return polyListEquals(term.polyList) && facSetEquals(term.varFacSet);
        }
        return false;
    }
    
    private boolean polyListEquals(ArrayList<Poly> polyList) {
        if (this.polyList.size() == polyList.size()) {
            boolean flag = true;
            for (int i = 0; i < polyList.size(); i++) {
                if (!polyList.get(i).equals(this.polyList.get(i))) {
                    flag = false;
                    break;
                }
            }
            return flag;
        }
        return false;
    }
    
    private boolean facSetEquals(VarItemSet varFacSet) {
        if (this.varFacSet.size() == varFacSet.size()) {
            boolean flag = true;
            for (Map.Entry entry : this.varFacSet.entrySet()) {
                Factor factor = (Factor) entry.getKey();
                BigInteger exp = (BigInteger) entry.getValue();
                if (!(varFacSet.containsKey(factor) && varFacSet.get(factor).equals(exp))) {
                    flag = false;
                    break;
                }
            }
            return flag;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        for (Map.Entry entry : this.varFacSet.entrySet()) {
            VarItem varItem = (VarItem) entry.getKey();
            code ^= varItem.hashCode();
        }
        for (Poly poly : polyList) {
            code ^= poly.hashCode();
        }
        return code;
    }
    
    @Override
    public String toString() {
        if (this.coeFactor.getValue().equals(BigInteger.ZERO)) {
            return "";
        } else if (this.isConstant()) {
            if (this.coeFactor.getValue().compareTo(BigInteger.ZERO) > 0) {
                return "+" + this.coeFactor.toString();
            } else {
                return this.coeFactor.toString();
            }
        } else {
            String termStr = "";
            BigInteger coe = coeFactor.getValue();
            if (coe.compareTo(BigInteger.ONE) == 0) {
                termStr = "+";
            } else if (coe.compareTo(BigInteger.ONE) > 0) {
                termStr = "+" + coe + "*";
            } else if (coe.compareTo(new BigInteger("-1")) == 0) {
                termStr = "-";
            } else if (coe.compareTo(new BigInteger("-1")) < 0) {
                termStr = coe + "*";
            }
            boolean isFirst = true;
            for (Map.Entry entry : this.varFacSet.entrySet()) {
                String facStr = entry.getKey().toString();
                if (!facStr.equals("")) {
                    if (!isFirst) {
                        termStr = termStr.concat("*");
                    }
                    isFirst = false;
                    termStr = termStr.concat(facStr);
                }
            }
            for (Poly poly : polyList) {
                String polyStr = poly.toString();
                if (!polyStr.equals("")) {
                    if (!isFirst) {
                        termStr = termStr.concat("*");
                    }
                    isFirst = false;
                    termStr = termStr.concat("(" + polyStr + ")");
                }
            }
            return termStr;
        }
    }
}
