public interface Factor {
    Factor copyOf();
    
    Term diff();
    
    boolean isZero();
    
}
