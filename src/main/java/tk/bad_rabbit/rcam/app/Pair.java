package tk.bad_rabbit.rcam.app;

public class Pair<FirstType, SecondType> {
  private FirstType left;
  private SecondType right;
  
  public Pair(FirstType ackNumber, SecondType returnCode) {
    this.left = ackNumber;
    this.right = returnCode;
  }
  
  public FirstType getLeft() {
    return left;
  }
  
  public SecondType getRight() {
    return right;
  }
  
  @Override
  public int hashCode() {
    return left.hashCode() ^ right.hashCode();
  }
  
  @Override
  public boolean equals(Object o) {
    if(!(o instanceof Pair)) return false;
    return (this.left.equals(((Pair) o).getLeft())) && (this.right.equals(((Pair) o).getRight()));
  }
}