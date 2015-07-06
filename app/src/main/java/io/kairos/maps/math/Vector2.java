package io.kairos.maps.math;

public class Vector2 {
    /**
     * The number of dimensions.
     */
    public static final int DIM = 2;

    /**
     * The zero vector.
     */
    static final Vector2 Zero = new Vector2(0.0, 0.0);

    /**
     * The vector (1,1).
     */
    static final Vector2 Ones = new Vector2(1.0, 1.0);

    /**
     * The vector (1,0).
     */
    static final Vector2 UnitX = new Vector2(1.0, 0.0);

    /**
     * The vector (0,1).
     */
    static final Vector2 UnitY = new Vector2(0.0, 1.0);

    /**
     * Components of this vector.
     */
    double x, y;

    /**
     * Create a vector with the given values.
     */
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 rhs) {
        return new Vector2(this.x + rhs.x, this.y + rhs.y);
    }

    public Vector2 addSelf(Vector2 rhs) {
        this.x += rhs.x;
        this.y += rhs.y;
        return this;
    }

    public Vector2 subtract(Vector2 rhs) {
        return new Vector2(this.x - rhs.x, this.y - rhs.y);
    }

    public Vector2 subtractSelf(Vector2 rhs) {
        this.x -= rhs.x;
        this.y -= rhs.y;
        return this;
    }

    public Vector2 multiply(double s) {
        return new Vector2(x * s, y * s);
    }

    public Vector2 multiplySelf(double s) {
        x *= s;
        y *= s;
        return this;
    }

    public Vector2 divide(double s) {
        double inv = 1.0 / s;
        return new Vector2(x * inv, y * inv);
    }

    public Vector2 divideSelf(double s) {
        double inv = 1.0 / s;
        x *= inv;
        y *= inv;
        return this;
    }

    public Vector2 negate() {
        return new Vector2(-x, -y);
    }

    /**
     * @remark No bounds checking.
     */
    public final double at(int i) {
        // assumes all members are in a contiguous block
        assert (i < DIM && i >= 0);
        return i == 0 ? x : y;
    }

    /**
     * Returns the dot product of two vectors
     */
    public static double dot(final Vector2 lhs, final Vector2 rhs) {
        return lhs.x * rhs.x + lhs.y * rhs.y;
    }

    /**
     * Efficiency function: does not require square root operation.
     */
    public static double squared_length(final Vector2 v) {
        return v.x * v.x + v.y * v.y;
    }

    /**
     * Returns the length of a vector.
     */
    public static double length(final Vector2 v) {
        return Math.sqrt(squared_length(v));
    }

    /**
     * Calculate the positive distance between two vectors.
     */
    public static double distance(final Vector2 lhs, final Vector2 rhs) {
        return length(lhs.subtract(rhs));
    }

    /**
     * Efficiency function: does not require square root operation.
     */
    public static double squared_distance(final Vector2 lhs, final Vector2 rhs) {
        return squared_length(lhs.subtract(rhs));
    }

    /**
     * Returns the unit vector pointing in the same direction as this vector.
     */
    public static Vector2 normalize(final Vector2 v) {
        return v.divide(length(v));
    }

    /**
     * Returns a vector whose elements are the absolute values of all the
     * elements of this vector.
     */
    public static Vector2 vabs(final Vector2 v) {
        return new Vector2(Math.abs(v.x), Math.abs(v.y));
    }

    /**
     * Returns the element-wise maximum of the two vectors.
     */
    public static Vector2 vmax(final Vector2 lhs, final Vector2 rhs) {
        return new Vector2(
                Math.max(lhs.x, rhs.x),
                Math.max(lhs.y, rhs.y)
        );
    }

    /**
     * Returns the element-wise minimum of the two vectors.
     */
    public static Vector2 vmin(final Vector2 lhs, final Vector2 rhs) {
        return new Vector2(
                Math.min(lhs.x, rhs.x),
                Math.min(lhs.y, rhs.y)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2 vector2 = (Vector2) o;

        if (Double.compare(vector2.x, x) != 0) return false;
        if (Double.compare(vector2.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
