import java.math.BigInteger;

public interface Function {
    boolean equals(Object obj);
    
    int hashCode();
    
    String toString();
    
    Function differential();
    
    BigInteger coefMult();
}
