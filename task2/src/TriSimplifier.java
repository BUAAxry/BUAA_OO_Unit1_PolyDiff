import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;

public class TriSimplifier {
    /**
     * 利用三角公式对多项式进行化简
     * 默认所有多项式的x项指数相同！！！
     */
    private static final int DFS_DEPTH = 9; //最大递归深度
    private static final long MAX_TIME = 1400; //熔断时间(ms)
    private static long startTime;
    private HashSet<Poly> polySet;
    
    public TriSimplifier() {
        this.polySet = new HashSet<>();
    }
    
    public Poly triSimplify(Poly oriPoly) {
        Poly bestPoly = oriPoly;
        int minLen = oriPoly.toString().length();
        startTime = System.currentTimeMillis();
        dfs(oriPoly, 0);
        for (Poly curPoly : polySet) {
            int curLen = curPoly.toString().length();
            if (curLen < minLen) {
                bestPoly = curPoly;
                minLen = curLen;
            }
        }
        return bestPoly;
    }
    
    private void dfs(Poly lastPoly, int curDepth) {
        //System.out.println("dfs---curDepth:" + curDepth);
        if (curDepth > DFS_DEPTH || polySet.contains(lastPoly) ||
                System.currentTimeMillis() - startTime > MAX_TIME) {
            return;
        } else {
            polySet.add(lastPoly);
            for (Map.Entry<Term, BigInteger> termAEntry : lastPoly.getTermMap().entrySet()) {
                Term termA = termAEntry.getKey();
                for (Map.Entry<Term, BigInteger> termBEntry : lastPoly.getTermMap().entrySet()) {
                    Term termB = termBEntry.getKey();
                    if (!termA.equals(termB) &&
                            System.currentTimeMillis() - startTime < MAX_TIME) {
                        int judge = judge(termA, termB);
                        Poly simPoly = null;
                        Poly newPoly = null;
                        if (judge != 0) {
                            switch (judge) {
                                case 1:
                                    simPoly = merge1(termA, termB);
                                    break;
                                case 2:
                                    simPoly = merge2(termA, termB);
                                    break;
                                case 3:
                                    simPoly = merge3(termA, termB);
                                    break;
                                default:
                                    break;
                            }
                            newPoly = lastPoly.removeTerms(termA, termB);
                            assert simPoly != null;
                            newPoly.addPoly(simPoly);
                            dfs(newPoly, curDepth + 1);
                        }
                    }
                }
            }
            return;
        }
    }
    
    private int judge(Term termA, Term termB) {
        if (termA.getSinExp().subtract(new BigInteger("2")).equals(termB.getSinExp()) &&
                termB.getCosExp().subtract(new BigInteger("2")).equals(termA.getCosExp())) {
            return 1;
        } else if (termA.getCosExp().equals(termB.getCosExp()) &&
                termB.getSinExp().subtract(new BigInteger("2")).equals(termA.getSinExp())) {
            return 2;
        } else if (termA.getSinExp().equals(termB.getSinExp()) &&
                termB.getCosExp().subtract(new BigInteger("2")).equals(termA.getCosExp())) {
            return 3;
        } else {
            return 0;
        }
    }
    
    private Poly merge1(Term termA, Term termB) {
        Term t1 = new Term(termA.getCoe(), termA.getPowExp(),
                termA.getSinExp().subtract(new BigInteger("2")), termA.getCosExp());
        Term t2 = new Term(termB.getCoe().subtract(termA.getCoe()),
                termB.getPowExp(), termB.getSinExp(), termB.getCosExp());
        Poly poly = new Poly(t1);
        poly.addCopyOfTerm(t2);
        return poly;
    }
    
    private Poly merge2(Term termA, Term termB) {
        Term t1 = new Term(termA.getCoe().add(termB.getCoe()),
                termA.getPowExp(), termA.getSinExp(), termA.getCosExp());
        Term t2 = new Term(termB.getCoe().negate(), termB.getPowExp(),
                termB.getSinExp().subtract(new BigInteger("2")),
                termB.getCosExp().add(new BigInteger("2")));
        Poly poly = new Poly(t1);
        poly.addCopyOfTerm(t2);
        return poly;
    }
    
    private Poly merge3(Term termA, Term termB) {
        Term t1 = new Term(termA.getCoe().add(termB.getCoe()),
                termA.getPowExp(), termA.getSinExp(), termA.getCosExp());
        Term t2 = new Term(termB.getCoe().negate(), termB.getPowExp(),
                termB.getSinExp().add(new BigInteger("2")),
                termB.getCosExp().subtract(new BigInteger("2")));
        Poly poly = new Poly(t1);
        poly.addCopyOfTerm(t2);
        return poly;
    }
    
}
