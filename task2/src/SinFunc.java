import java.math.BigInteger;

public class SinFunc implements Function {
    private BigInteger exponent;
    
    public SinFunc(BigInteger exponent) {
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
        SinFunc sin = (SinFunc) f;
        BigInteger exp = sin.getExponent().add(this.exponent);
        return new SinFunc(exp);
    }
    
    @Override
    public int hashCode() {
        return this.exponent.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof SinFunc) {
            return this.exponent.compareTo(((SinFunc) o).exponent) == 0;
        }
        return false;
    }
    
    @Override
    public Function copyOf() {
        return new SinFunc(this.exponent);
    }
    
    @Override
    public String toString() {
        if (this.exponent.compareTo(BigInteger.ZERO) == 0) {
            return "";
        } else if (this.exponent.compareTo(BigInteger.ONE) == 0) {
            return "sin(x)";
        } else {
            return "sin(x)**" + this.exponent;
        }
    }
    
    @Override
    public Term diff() {
        if (this.exponent.compareTo(BigInteger.ZERO) == 0) {
            return new Term(BigInteger.ZERO);
        } else {
            return new Term(this.exponent, BigInteger.ZERO,
                    this.exponent.subtract(BigInteger.ONE), BigInteger.ONE);
        }
    }
    
}
