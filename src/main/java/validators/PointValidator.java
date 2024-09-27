package validators;

public class PointValidator {
    public static Boolean isHit(float x, float y, float r) {
        // Rectangle in 1 quarter
        if(x >= 0 && y >= 0 && x <= r / 2 && y < r)
            return true;

        // Triangle in four quarters
        if (x >= 0 && y<=0 && x <= r/2 && y >= 2 * x - r && y>= -r)
            return true;

        // The sector in the second quarter
        if (x <= 0 && y >= 0 && x * x + y * y <= r * r / 4)
            return true;

        // 3 четверть
        return false;
    }
}
