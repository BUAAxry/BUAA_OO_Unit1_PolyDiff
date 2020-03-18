import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Term implements RegStr {
    private static final int CONST = 0;
    private static final int POW = 1;
    private static final int SIN = 2;
    private static final int COS = 3;
    private static final int FUNC_TOT = 4;
    
    private final Function[] funcList;
    private final FuncFactory funcFactory;
    
    public Term() {
        this.funcList = new Function[FUNC_TOT];
        this.funcList[CONST] = new ConstFunc(BigInteger.ONE);
        this.funcList[POW] = new PowFunc(BigInteger.ZERO);
        this.funcList[SIN] = new SinFunc(BigInteger.ZERO);
        this.funcList[COS] = new CosFunc(BigInteger.ZERO);
        this.funcFactory = new FuncFactory();
    }
    
    public Term(BigInteger coe) {
        this.funcList = new Function[FUNC_TOT];
        this.funcList[CONST] = new ConstFunc(coe);
        this.funcList[POW] = new PowFunc(BigInteger.ZERO);
        this.funcList[SIN] = new SinFunc(BigInteger.ZERO);
        this.funcList[COS] = new CosFunc(BigInteger.ZERO);
        this.funcFactory = new FuncFactory();
    }
    
    public Term(BigInteger coe, BigInteger powExp, BigInteger sinExp, BigInteger cosExp) {
        this.funcList = new Function[FUNC_TOT];
        this.funcList[CONST] = new ConstFunc(coe);
        this.funcList[POW] = new PowFunc(powExp);
        this.funcList[SIN] = new SinFunc(sinExp);
        this.funcList[COS] = new CosFunc(cosExp);
        this.funcFactory = new FuncFactory();
    }
    
    public BigInteger getCoe() {
        return ((ConstFunc) this.funcList[CONST]).getValue();
    }
    
    public BigInteger getPowExp() {
        return ((PowFunc) this.funcList[POW]).getExponent();
    }
    
    public BigInteger getSinExp() {
        return ((SinFunc) this.funcList[SIN]).getExponent();
    }
    
    public BigInteger getCosExp() {
        return ((CosFunc) this.funcList[COS]).getExponent();
    }
    
    //解析项字符串
    public void parse(String termStr, boolean multiNegate) {
        boolean isNegate = multiNegate;
        Pattern pat = Pattern.compile(FACTOR_REG);
        Matcher mat = pat.matcher(termStr);
        while (mat.find() && !this.isZero()) { //若发现该项存在因子为0则自动停止
            if (mat.start() == 1 && termStr.charAt(0) == '-') {
                isNegate = !isNegate;
            }
            //System.out.println("factor:"+mat.group());
            Function newFunc = funcFactory.getNewFunc(mat.group());
            this.multiFunc(newFunc);
        }
        if (isNegate) {
            this.multiFunc(new ConstFunc(new BigInteger("-1")));
        }
    }
    
    //合并同类项(传入值为同类项的系数)
    public Term addCoe(BigInteger coe) {
        Term newTerm = new Term();
        newTerm.funcList[CONST] = new ConstFunc(coe.add(
                ((ConstFunc) this.funcList[CONST]).getValue()));
        //System.out.println(newTerm.funcList[CONST].toString());
        for (int i = 1; i < FUNC_TOT; i++) {
            newTerm.funcList[i] = this.funcList[i].copyOf();
        }
        //System.out.println("newTerm:"+newTerm.toString());
        return newTerm;
    }
    
    public Term copyOf() {
        Term term = new Term();
        for (int i = 0; i < FUNC_TOT; i++) {
            term.funcList[i] = this.funcList[i].copyOf();
        }
        return term;
    }
    
    //判断该项常数因子是否为0
    public boolean isZero() {
        return this.funcList[CONST].equals(new ConstFunc(BigInteger.ZERO));
    }
    
    //判断该项是否为常数
    private boolean isConstant() {
        return this.funcList[POW].equals(new PowFunc(BigInteger.ZERO)) &&
                this.funcList[SIN].equals(new SinFunc(BigInteger.ZERO)) &&
                this.funcList[COS].equals(new CosFunc(BigInteger.ZERO));
    }
    
    public Poly diff() {
        Poly poly = new Poly();
        if (!this.isZero()) {
            for (int i = 1; i < FUNC_TOT; i++) {
                Term diffTerm = this.funcList[i].diff();
                if (!diffTerm.isZero()) {
                    for (int j = 0; j < FUNC_TOT; j++) {
                        if (j != i) {
                            diffTerm.multiFunc(this.funcList[j]);
                        }
                    }
                    poly.addCopyOfTerm(diffTerm);
                }
            }
        }
        return poly;
    }
    
    //更新函数数组
    private void multiFunc(Function f) {
        if (f instanceof ConstFunc) {
            multiAimFunc(f, CONST);
        } else if (f instanceof PowFunc) {
            multiAimFunc(f, POW);
        } else if (f instanceof SinFunc) {
            multiAimFunc(f, SIN);
        } else if (f instanceof CosFunc) {
            multiAimFunc(f, COS);
        }
    }
    
    //添加特定类型函数
    private void multiAimFunc(Function f, int index) {
        assert index < FUNC_TOT;
        if (funcList[index] == null) {
            funcList[index] = f;
        } else {
            funcList[index] = f.multi(funcList[index]);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof Term) {
            Term term = (Term) o;
            return term.funcList[POW].equals(this.funcList[POW]) &&
                    term.funcList[SIN].equals(this.funcList[SIN]) &&
                    term.funcList[COS].equals(this.funcList[COS]);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return funcList[POW].hashCode() ^ funcList[SIN].hashCode() ^ funcList[COS].hashCode();
    }
    
    @Override
    public String toString() {
        if (this.isZero()) {
            return "";
        } else if (this.isConstant()) {
            if (((ConstFunc) this.funcList[CONST]).getValue().compareTo(BigInteger.ZERO) > 0) {
                return "+" + this.funcList[CONST].toString();
            } else {
                return this.funcList[CONST].toString();
            }
        } else {
            String termStr = "";
            BigInteger coe = ((ConstFunc) funcList[CONST]).getValue();
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
            for (int i = 1; i < FUNC_TOT; i++) {
                String curStr = this.funcList[i].toString();
                if (!curStr.equals("")) {
                    if (!isFirst) {
                        termStr = termStr.concat("*");
                    }
                    isFirst = false;
                    termStr = termStr.concat(curStr);
                }
            }
            return termStr;
        }
    }
}
