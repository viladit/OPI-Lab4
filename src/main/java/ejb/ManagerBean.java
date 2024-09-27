package ejb;

import jakarta.annotation.PostConstruct;
import mbeans.*;
import models.Point;

import jakarta.ejb.Singleton;

import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@Singleton
public class ManagerBean {
    private MBeanServer mbs;
    private PointsCounterMBean pointsCounterMBean;
    private ClickIntervalMBean clickIntervalMBean;

    public ManagerBean() {
    }

    @PostConstruct
    public void initialize() {
        try {
            this.mbs = ManagementFactory.getPlatformMBeanServer();

            this.pointsCounterMBean = new PointsCounter();
            ObjectName pointsCounterName = new ObjectName("mbeans:type=PointsCounter");
            mbs.registerMBean(pointsCounterMBean, pointsCounterName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public PointsCounterMBean getPointsCounterMBean() {
        return pointsCounterMBean;
    }


    public void handlePointsWithMBeans(Point point) {
        pointsCounterMBean.pointsInc();

        if (point.getStatus()) {
            pointsCounterMBean.clearConsecutiveMisses();
        } else {
            pointsCounterMBean.checkConsecutiveMisses();
        }
    }

    public int getTotalPoints() {
        return pointsCounterMBean.getTotalPoints();
    }

    public int getMissedPoints() {
        return pointsCounterMBean.getMissedPoints();
    }

    public int getConsecutiveMisses() {
        return pointsCounterMBean.getConsecutiveMisses();
    }

    public double getAverageInterval() { return clickIntervalMBean.getAverageInterval(); }
    public MBeanNotificationInfo[] getNotificationMessage() {return pointsCounterMBean.getNotificationInfo();}
}
