import java.math.BigInteger;

public class ConstFactor implements Factor {
    private BigInteger value;
    
    public ConstFactor(BigInteger value) {
        this.value = value;
    }
    
    public BigInteger getValue() {
        return value;
    }
    
    public void addValue(BigInteger addVal) {
        this.value = value.add(addVal);
    }
    
    public void multi(ConstFactor constFactor) {
        this.value = this.value.multiply(constFactor.value);
    }
    
    @Override
    public ConstFactor copyOf() {
        return new ConstFactor(this.value);
    }
    
    @Override
    public Term diff() {
        return null;
    }
    
    @Override
    public boolean isZero() {
        return this.value.equals(BigInteger.ZERO);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof ConstFactor) {
            ConstFactor constFactor = (ConstFactor) o;
            return this.value.equals(constFactor.value);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public String toString() {
        if (this.value.compareTo(BigInteger.ZERO) == 0) {
            return "";
        } else {
            return this.value.toString();
        }
    }
    
}
