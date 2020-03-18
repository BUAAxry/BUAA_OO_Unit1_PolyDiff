import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class PolySimplifier {
    /**
     * 多项式化简母类，对多项式进行预处理（分组）
     */
    private HashMap<BigInteger, Poly> oriPolyMap;
    private HashMap<BigInteger, Poly> newPolyMap;
    
    public PolySimplifier() {
        this.oriPolyMap = new HashMap<>();
        this.newPolyMap = new HashMap<>();
    }
    
    public Poly simplify(Poly oriPoly) {
        this.splitPoly(oriPoly);
        for (Map.Entry<BigInteger, Poly> polyEntry : oriPolyMap.entrySet()) {
            Poly oldPoly = polyEntry.getValue();
            TriSimplifier triSimplifier = new TriSimplifier();
            Poly newPoly = triSimplifier.triSimplify(oldPoly);
            newPolyMap.put(polyEntry.getKey(), newPoly);
        }
        return this.mergePoly();
    }
    
    private void splitPoly(Poly oriPoly) {
        for (Map.Entry termEntry : oriPoly.getTermMap().entrySet()) {
            Term term = (Term) termEntry.getKey();
            BigInteger powExp = term.getPowExp();
            Poly curPoly = oriPolyMap.get(powExp);
            if (curPoly == null) {
                Poly poly = new Poly(term.copyOf());
                oriPolyMap.put(powExp, poly);
            } else {
                curPoly.addCopyOfTerm(term);//此时原polyMap中的poly也发生变化
            }
        }
    }
    
    private Poly mergePoly() {
        Poly newPoly = new Poly();
        for (Map.Entry<BigInteger, Poly> polyEntry:newPolyMap.entrySet()) {
            newPoly.addPoly(polyEntry.getValue());
        }
        return newPoly;
    }
    
}
