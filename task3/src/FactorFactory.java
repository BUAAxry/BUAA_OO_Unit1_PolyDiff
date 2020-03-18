import java.math.BigInteger;

public class FactorFactory {
    private static final String SIG_REG = "[+\\-]";
    private static final String SIG_INT_REG = "(" + SIG_REG + "?\\d+)";
    private static final String EXP_REG = "(\\^" + SIG_INT_REG + ")";
    private static final String POW_REG = "(x" + EXP_REG + "?)";
    private static final String SIN_REG = "(sin\\(.{1,100}\\)" + EXP_REG + "?)";
    private static final String COS_REG = "(cos\\(.{1,100}\\)" + EXP_REG + "?)";
    
    public Factor getNewFac(String preFacStr) {
        String facStr = preFacStr.replaceAll("^[\\t ]+", "");
        facStr = facStr.replaceAll("[\\t ]+$", "");
        Factor factor = null;
        //System.out.println("facStr: "+ facStr);
        if (facStr.matches(SIG_INT_REG)) {
            factor = this.getConst(facStr);
            //System.out.println("ConstFac:" + factor.toString());
        } else if (facStr.matches(POW_REG)) {
            factor = this.getPow(facStr);
        } else if (facStr.matches(SIN_REG)) {
            factor = this.getTri(facStr, true);
        } else if (facStr.matches(COS_REG)) {
            factor = this.getTri(facStr, false);
        } else if (facStr.startsWith("(") && facStr.endsWith(")")) {
            String polyStr = facStr.substring(1, facStr.length() - 1);
            Poly poly = Poly.parsePoly(polyStr);
            Factor polyFac = poly.polyToFac();
            if (polyFac != null) {
                factor = polyFac;
            } else {
                factor = poly;
            }
        } else {
            System.out.println("WRONG FORMAT!");
            System.exit(0);
        }
        return factor;
    }
    
    private int getTriIndex(String triStr) {
        int index = -1;
        for (int i = triStr.length() - 1; i >= 0; i--) {
            if (triStr.charAt(i) == ')') {
                break;
            } else if (triStr.charAt(i) == '^') {
                index = i;
                break;
            }
        }
        return index;
    }
    
    private Factor getTri(String triStr, boolean isSin) {
        int index = getTriIndex(triStr);
        Factor base;
        BigInteger exp;
        if (index == -1) {
            base = this.getNewFac(triStr.substring(4, triStr.length() - 1));
            exp = BigInteger.ONE;
        } else {
            String baseStr = triStr.substring(4, index - 1);
            String expStr = triStr.substring(index + 1);
            exp = this.getExp(expStr);
            base = this.getNewFac(baseStr);
        }
        if (isSin) {
            return this.simpSin(exp, base);
        } else {
            return this.simpCos(exp, base);
        }
    }
    
    private Factor simpSin(BigInteger exp, Factor base) {
        BigInteger two = new BigInteger("2");
        if (!exp.equals(BigInteger.ZERO)) {
            if (base instanceof ConstFactor) {
                BigInteger val = ((ConstFactor) base).getValue();
                if (val.compareTo(BigInteger.ZERO) < 0) {
                    if (exp.mod(two).equals(BigInteger.ZERO)) {
                        return new SinFactor(exp, new ConstFactor(val.negate()));
                    } else {
                        Term term = new Term(new ConstFactor(new BigInteger("-1")));
                        term.addFactor(new SinFactor(exp, new ConstFactor(val.negate())));
                        Poly poly = new Poly();
                        poly.addTerm(term);
                        return poly;
                    }
                } else if (val.equals(BigInteger.ZERO)) {
                    return new ConstFactor(BigInteger.ZERO);
                }
            } else if (base instanceof Poly) {
                Poly poly = (Poly) base;
                if (poly.toString().startsWith("-")) {
                    Poly newPoly = poly.multiCoe(new ConstFactor(new BigInteger("-1")));
                    Factor newFac = newPoly.polyToFac();
                    SinFactor newSinFac;
                    if (newFac != null) {
                        newSinFac = new SinFactor(exp, newFac);
                    } else {
                        newSinFac = new SinFactor(exp, newPoly);
                    }
                    if (exp.mod(two).equals(BigInteger.ZERO)) {
                        return newSinFac;
                    } else {
                        Term term = new Term(new ConstFactor(new BigInteger("-1")));
                        term.addFactor(newSinFac);
                        Poly retPoly = new Poly();
                        retPoly.addTerm(term);
                        return retPoly;
                    }
                }
            }
        }
        return new SinFactor(exp, base);
    }
    
    private Factor simpCos(BigInteger exp, Factor base) {
        if (!exp.equals(BigInteger.ZERO)) {
            if (base instanceof ConstFactor) {
                BigInteger val = ((ConstFactor) base).getValue();
                if (val.compareTo(BigInteger.ZERO) < 0) {
                    return new CosFactor(exp, new ConstFactor(val.negate()));
                } else if (val.equals(BigInteger.ZERO)) {
                    return new ConstFactor(BigInteger.ONE);
                }
            } else if (base instanceof Poly) {
                Poly poly = (Poly) base;
                if (poly.toString().startsWith("-")) {
                    Poly newPoly = poly.multiCoe(new ConstFactor(new BigInteger("-1")));
                    Factor newFac = newPoly.polyToFac();
                    if (newFac != null) {
                        return new CosFactor(exp, newFac);
                    } else {
                        return new CosFactor(exp, newPoly);
                    }
                }
            }
        }
        return new CosFactor(exp, base);
    }
    
    private ConstFactor getConst(String constStr) {
        return new ConstFactor(new BigInteger(constStr));
    }
    
    private Factor getPow(String powStr) {
        if ("x".equals(powStr)) {
            return new PowFactor(BigInteger.ONE);
        } else {
            int index = powStr.indexOf('^') + 1;
            String expStr = powStr.substring(index);
            BigInteger exp = this.getExp(expStr);
            if (exp.equals(BigInteger.ZERO)) {
                return new ConstFactor(BigInteger.ONE);
            } else {
                return new PowFactor(this.getExp(expStr));
            }
        }
    }
    
    private BigInteger getExp(String expStr) {
        BigInteger exp = new BigInteger(expStr);
        if (exp.abs().compareTo(new BigInteger("50")) > 0) {
            System.out.println("WRONG FORMAT!");
            System.exit(0);
        } else {
            return exp;
        }
        return null;
    }
    
}
