import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VarItemSet<T> extends HashMap<T, BigInteger> {
    @Override
    public BigInteger put(T ttKey, BigInteger value) {
        assert ttKey != null;
        VarItem key = (VarItem) ttKey;
        if (!key.getVal().equals(BigInteger.ZERO)) {
            BigInteger oriVal = super.put(ttKey, value);
            if (oriVal != null) {
                super.remove(ttKey);
                key.addVal(oriVal);
                if (!key.getVal().equals(BigInteger.ZERO)) {
                    super.put(ttKey, key.getVal());
                }
            }
        }
        return super.get(key);
    }
    
    @Override
    public Set<Entry<T, BigInteger>> entrySet() {
        return super.entrySet();
    }
    
    @Override
    public void putAll(Map<? extends T, ? extends BigInteger> m) {
        m.forEach(this::put);
    }
    
    @Override
    public BigInteger remove(Object key) {
        return super.remove(key);
    }
    
}
