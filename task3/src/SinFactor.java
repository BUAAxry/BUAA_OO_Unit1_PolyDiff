import java.math.BigInteger;

public class SinFactor implements Factor, VarItem {
    private BigInteger exponent;
    private final Factor base;
    
    public SinFactor(BigInteger exponent, Factor base) {
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
    public SinFactor copyOf() {
        return new SinFactor(this.exponent, this.base.copyOf());
    }
    
    @Override
    public Term diff() {
        if (this.base.diff() == null || this.exponent.equals(BigInteger.ZERO)) {
            return null;
        } else {
            Term newTerm = new Term(new ConstFactor(this.exponent));
            newTerm.addFactor(new CosFactor(BigInteger.ONE, this.base.copyOf()));
            newTerm.addTerm(this.base.diff());
            if (!this.exponent.equals(BigInteger.ONE)) {
                newTerm.addFactor(
                        new SinFactor(this.exponent.subtract(BigInteger.ONE), this.base.copyOf()));
            }
            return newTerm;
        }
    }
    
    @Override
    public boolean isZero() {
        return this.base.isZero() && !this.exponent.equals(BigInteger.ZERO);
    }
    
    //底数相等就认为两者相等
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof SinFactor) {
            SinFactor sinFactor = (SinFactor) o;
            if (this.base instanceof VarItem && sinFactor.base instanceof VarItem) {
                VarItem thisBase = (VarItem) this.base;
                VarItem thatBase = (VarItem) sinFactor.base;
                return thisBase.equals(thatBase) && thisBase.getVal().equals(thatBase.getVal());
            } else {
                return this.base.equals(sinFactor.base);
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
            return "0";
        } else {
            String sinStr = "";
            if (base instanceof Poly) {
                sinStr = "sin((" + base.toString() + "))";
            } else {
                if (base.toString().equals("x*x")) {
                    sinStr = "sin(x**2)";
                } else {
                    sinStr = "sin(" + base.toString() + ")";
                }
            }
            if (exponent.equals(BigInteger.ONE)) {
                return sinStr;
            } else {
                return sinStr + "**" + exponent.toString();
            }
        }
    }
}
