package mbeans;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import java.io.Serializable;

public class PointsCounter extends NotificationBroadcasterSupport implements PointsCounterMBean, Serializable {
    private int totalPoints = 0;
    private int missedPoints = 0;
    private int consecutiveMisses = 0;
    private long sequenceNumber = 1;

    public PointsCounter() {

    }

    @Override
    public int getTotalPoints() {
        return totalPoints;
    }

    @Override
    public int getConsecutiveMisses() {
        return consecutiveMisses;
    }

    @Override
    public int pointsInc() {
        totalPoints++;
        return totalPoints;
    }

    @Override
    public int getMissedPoints() {
        return missedPoints;
    }

    @Override
    public void checkConsecutiveMisses() {
        missedPoints++;
        consecutiveMisses++;
        if (consecutiveMisses % 4 == 0) {
            Notification notification = new Notification(
                    "misses",
                    this,
                    sequenceNumber++,
                    "Вы промахнулись 4 раза подряд"
            );
            sendNotification(notification);
        }
    }

    @Override
    public int clearConsecutiveMisses(){
        consecutiveMisses = 0;
        return 0;
    }
}

