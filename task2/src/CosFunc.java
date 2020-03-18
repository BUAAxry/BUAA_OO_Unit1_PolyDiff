import java.math.BigInteger;

public class CosFunc implements Function {
    private BigInteger exponent;
    
    public CosFunc(BigInteger exponent) {
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
        CosFunc cos = (CosFunc) f;
        BigInteger exp = cos.getExponent().add(this.exponent);
        return new CosFunc(exp);
    }
    
    @Override
    public int hashCode() {
        return this.exponent.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof CosFunc) {
            return this.exponent.compareTo(((CosFunc) o).exponent) == 0;
        }
        return false;
    }
    
    @Override
    public Function copyOf() {
        return new CosFunc(this.exponent);
    }
    
    @Override
    public String toString() {
        if (this.exponent.compareTo(BigInteger.ZERO) == 0) {
            return "";
        } else if (this.exponent.compareTo(BigInteger.ONE) == 0) {
            return "cos(x)";
        } else {
            return "cos(x)**" + this.exponent;
        }
    }
    
    @Override
    public Term diff() {
        if (this.exponent.compareTo(BigInteger.ZERO) == 0) {
            return new Term(BigInteger.ZERO);
        } else {
            return new Term(this.exponent.negate(), BigInteger.ZERO,
                    BigInteger.ONE, this.exponent.subtract(BigInteger.ONE));
        }
    }
    
}
