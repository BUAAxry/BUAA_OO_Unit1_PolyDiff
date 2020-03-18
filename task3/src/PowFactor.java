import java.math.BigInteger;

public class PowFactor implements Factor, VarItem {
    private BigInteger exponent;
    
    public PowFactor(BigInteger exponent) {
        this.exponent = exponent;
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
    public PowFactor copyOf() {
        return new PowFactor(this.exponent);
    }
    
    @Override
    public Term diff() {
        if (this.exponent.equals(BigInteger.ZERO)) {
            return null;
        } else {
            Term newTerm = new Term(new ConstFactor(this.exponent));
            newTerm.addFactor(new PowFactor(this.exponent.subtract(BigInteger.ONE)));
            return newTerm;
        }
    }
    
    @Override
    public boolean isZero() {
        return false;
    }
    
    //同类型即相等
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else {
            return o instanceof PowFactor;
        }
    }
    
    @Override
    public int hashCode() {
        return 2147483647;
    }
    
    @Override
    public String toString() {
        if (this.exponent.compareTo(BigInteger.ZERO) == 0) {
            return "";
        } else if (this.exponent.compareTo(BigInteger.ONE) == 0) {
            return "x";
        } else if (this.exponent.compareTo(new BigInteger("2")) == 0) {
            return "x*x";
        } else {
            return "x**" + this.exponent;
        }
    }
    
}
