import java.math.BigInteger;

public class PowerFunc implements Function {
    private BigInteger exponent;
    
    public PowerFunc(BigInteger exponent) {
        this.exponent = exponent;
    }
    
    public BigInteger getExponent() {
        return exponent;
    }
    
    public void setExponent(BigInteger exponent) {
        this.exponent = exponent;
    }
    
    @Override
    public int hashCode() {
        return exponent.hashCode();
    }
    
    /**
     * 指数相等即认为两者相等
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof PowerFunc) {
            PowerFunc powerFunc = (PowerFunc) obj;
            return powerFunc.getExponent().compareTo(this.exponent) == 0;
        }
        return false;
    }
    
    @Override
    public String toString() {
        if (this.exponent.equals(BigInteger.ZERO)) {
            return "";
        } else if (this.exponent.equals(BigInteger.ONE)) {
            return "x";
        } else {
            return "x**" + exponent;
        }
    }
    
    @Override
    public PowerFunc differential() {
        if (exponent.equals(BigInteger.ZERO)) {
            return null;
        } else {
            return new PowerFunc(exponent.subtract(BigInteger.ONE));
        }
    }
    
    @Override
    public BigInteger coefMult() {
        return this.exponent;
    }
    
}
