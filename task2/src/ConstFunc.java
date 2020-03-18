import java.math.BigInteger;

public class ConstFunc implements Function {
    private BigInteger value;
    
    public ConstFunc(BigInteger value) {
        this.value = value;
    }
    
    public BigInteger getValue() {
        return value;
    }
    
    public void setValue(BigInteger value) {
        this.value = value;
    }
    
    @Override
    public Function multi(Function f) {
        ConstFunc constFunc = (ConstFunc) f;
        BigInteger val = constFunc.getValue().multiply(this.value);
        return new ConstFunc(val);
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof ConstFunc) {
            return this.value.compareTo(((ConstFunc) o).value) == 0;
        }
        return false;
    }
    
    @Override
    public Function copyOf() {
        return new ConstFunc(this.value);
    }
    
    @Override
    public String toString() {
        if (this.value.compareTo(BigInteger.ZERO) == 0) {
            return "";
        } else {
            return this.value.toString();
        }
    }
    
    @Override
    public Term diff() {
        return new Term(BigInteger.ZERO);
    }
    
}
