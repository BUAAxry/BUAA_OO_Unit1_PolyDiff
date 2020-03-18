public interface RegStr {
    String SIG_REG = "[+\\-]";
    String SIG_INT_REG = "(" + SIG_REG + "?\\d+)";
    String EXP_REG = "(\\*\\*" + SIG_INT_REG + ")";
    String SIN_REG = "(sin\\(x\\)" + EXP_REG + "?)";
    String COS_REG = "(cos\\(x\\)" + EXP_REG + "?)";
    String POW_REG = "(x" + "(\\*\\*[+\\-]?(0*((10000)|\\d{1,4})))" + "?)";
    String CONST_REG = SIG_INT_REG;
    String FUNC_REG = "(" + POW_REG + "|" + SIN_REG + "|" + COS_REG + ")";
    String FACTOR_REG = "(" + FUNC_REG + "|" + CONST_REG + ")";
    String TERM_REG = "(" + SIG_REG + "?" + FACTOR_REG + "(\\*" + FACTOR_REG + "){0,100})";
    String POLY_REG = "(" + SIG_REG + "?" + TERM_REG + "(" + SIG_REG + TERM_REG + "){0,100})";
    
}
