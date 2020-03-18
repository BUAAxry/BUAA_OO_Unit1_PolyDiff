public interface Function {
    @Override
    String toString();
    
    @Override
    boolean equals(Object o);
    
    Function copyOf();
    
    Function multi(Function f);
    
    Term diff();
}
