package mbeans;

import javax.management.NotificationEmitter;

public interface PointsCounterMBean extends NotificationEmitter {
    int getTotalPoints();
    int pointsInc();
    int getMissedPoints();
    int getConsecutiveMisses();
    void checkConsecutiveMisses();
    int clearConsecutiveMisses();
}

