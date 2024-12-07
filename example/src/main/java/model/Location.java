package model;

public class Location {
  private final int x;
  private final int y;

  public Location(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public String toString() {
    return "Location{" + "x=" + x + ", y=" + y + '}';
  }
}
