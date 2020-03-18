import java.math.BigInteger;

public class PowFunc implements Function, Cloneable {
    private BigInteger exponent;
    
    public PowFunc(BigInteger exponent) {
        this.exponent = exponent;
    }
    
    public BigInteger getExponent() {
        return exponent;
    }
    
    public void setExponent(BigInteger exponent) {
        this.exponent = exponent;
    }
    
    @Override
    public Function multi(Function f) {
        PowFunc pow = (PowFunc) f;
        BigInteger exp = pow.getExponent().add(this.exponent);
        return new PowFunc(exp);
    }
    
    @Override
    public int hashCode() {
        return this.exponent.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof PowFunc) {
            return this.exponent.compareTo(((PowFunc) o).exponent) == 0;
        }
        return false;
    }
    
    @Override
    public Function copyOf() {
        return new PowFunc(this.exponent);
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
    
    @Override
    public Term diff() {
        if (this.exponent.compareTo(BigInteger.ZERO) == 0) {
            return new Term(BigInteger.ZERO);
        } else {
            return new Term(this.exponent, this.exponent.subtract(BigInteger.ONE),
                    BigInteger.ZERO, BigInteger.ZERO);
        }
    }
    
}
