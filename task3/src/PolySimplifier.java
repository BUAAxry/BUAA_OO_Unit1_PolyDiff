import java.util.HashSet;
import java.util.Map;

public class PolySimplifier {
    
    private final HashSet<Poly> polySet;
    
    public PolySimplifier() {
        this.polySet = new HashSet<>();
    }
    
    public Poly polySimplify(Poly oriPoly) {
        this.dfs(oriPoly);
        Poly mostSimPoly = oriPoly;
        int minLength = oriPoly.toString().length();
        for (Poly newPoly : polySet) {
            int len = newPoly.toString().length();
            if (len < minLength) {
                minLength = len;
                mostSimPoly = newPoly;
            }
        }
        return mostSimPoly;
    }
    
    private void dfs(Poly oldPoly) {
        boolean mostSim = true;
        for (Map.Entry entry : oldPoly.getTermSet().entrySet()) {
            Term term = (Term) entry.getKey();
            if (termCanSimplify(term)) {
                mostSim = false;
                Poly newSubPoly = powExpand(term);
                Poly newPoly = oldPoly.removeTerm(term);
                newPoly.addPoly(newSubPoly);
                dfs(newPoly);
            }
        }
        if (mostSim) {
            this.polySet.add(oldPoly);
        }
    }
    
    private Poly powExpand(Term term) {
        Poly retPoly = new Poly();
        Poly polyFac = term.getPolyList().get(0);
        for (Map.Entry entry : polyFac.getTermSet().entrySet()) {
            Term subTerm = (Term) entry.getKey();
            Poly subPoly = polyFac.multiTerm(subTerm);
            retPoly.addPoly(subPoly);
        }
        retPoly = retPoly.multiCoe(new ConstFactor(term.getVal()));
        return retPoly;
    }
    
    private boolean termCanSimplify(Term term) {
        if (term.getVarFacSet().size() == 0 && term.getPolyList().size() == 2) {
            Poly poly0 = term.getPolyList().get(0);
            Poly poly1 = term.getPolyList().get(1);
            return poly0.equals(poly1);
        }
        return false;
    }
}
