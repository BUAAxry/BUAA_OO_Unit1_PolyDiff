import java.math.BigInteger;

public class CosFactor implements Factor, VarItem {
    private BigInteger exponent;
    private final Factor base;
    
    public CosFactor(BigInteger exponent, Factor base) {
        this.exponent = exponent;
        this.base = base;
    }
    
    @Override
    public BigInteger getVal() {
        return this.exponent;
    }
    
    @Override
    public void addVal(BigInteger value) {
        this.exponent = exponent.add(value);
    }
    
    @Override
    public CosFactor copyOf() {
        return new CosFactor(this.exponent, this.base.copyOf());
    }
    
    @Override
    public Term diff() {
        if (this.base.diff() == null || this.exponent.equals(BigInteger.ZERO)) {
            return null;
        } else {
            Term newTerm = new Term(new ConstFactor(this.exponent.negate()));
            newTerm.addFactor(new SinFactor(BigInteger.ONE, this.base.copyOf()));
            newTerm.addTerm(this.base.diff());
            if (!this.exponent.equals(BigInteger.ONE)) {
                newTerm.addFactor(
                        new CosFactor(this.exponent.subtract(BigInteger.ONE), this.base.copyOf()));
            }
            return newTerm;
        }
    }
    
    @Override
    public boolean isZero() {
        return false;
    }
    
    //底数相等就认为两者相等
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof CosFactor) {
            CosFactor cosFactor = (CosFactor) o;
            if (this.base instanceof VarItem && cosFactor.base instanceof VarItem) {
                VarItem thisBase = (VarItem) this.base;
                VarItem thatBase = (VarItem) cosFactor.base;
                return thisBase.equals(thatBase) && thisBase.getVal().equals(thatBase.getVal());
            } else {
                return this.base.equals(cosFactor.base);
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.base.hashCode();
    }
    
    @Override
    public String toString() {
        if (exponent.equals(BigInteger.ZERO)) {
            return "";
        } else if (base.isZero()) {
            return "";
        } else {
            String cosStr = "";
            if (base instanceof Poly) {
                cosStr = "cos((" + base.toString() + "))";
            } else {
                if (base.toString().equals("x*x")) {
                    cosStr = "cos(x**2)";
                } else {
                    cosStr = "cos(" + base.toString() + ")";
                }
            }
            if (exponent.equals(BigInteger.ONE)) {
                return cosStr;
            } else {
                return cosStr + "**" + exponent.toString();
            }
        }
    }
}
