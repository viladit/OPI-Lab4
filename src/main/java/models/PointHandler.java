package models;

import database.DatabaseHandler;
import ejb.ManagerBean;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import validators.PointValidator;

import javax.management.MBeanNotificationInfo;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Map;

@Named("pointHandler")
@ApplicationScoped
public class PointHandler implements Serializable {

    private Point point = new Point();
    private LinkedList<Point> points = new LinkedList<>();

    @EJB
    private ManagerBean managerBean;

    public LinkedList<Point> getPoints() {
        return points;
    }

    @PostConstruct
    public void loadPointsFromDb(){
        this.points = DatabaseHandler.getDatabaseManager().loadCollection();
    }

    public void add(){
        long timer = System.nanoTime();
        point.setTime(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
        point.setStatus(PointValidator.isHit(point.getX(), point.getY(), point.getR()));
        point.setScriptTime((long) ((System.nanoTime() - timer) * 0.01));

        this.addPoint(point);
        point = new Point(point.getX(), point.getY(), point.getR());

        managerBean.handlePointsWithMBeans(point);
    }

    public void clear(){
//        System.out.println("clear");
//        try {
//            DatabaseHandler.getDatabaseManager().clearCollection();
//            this.points.clear();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
    private float roundY(double y) {
        if (y < 0) {
            return (float) (Math.ceil(y * 1000) / 1000);
        } else {
            return (float) Math.floor(y * 10000) / 10000;
        }
    }
    private float processY(String yString) {
        if (yString.length() <= 6) {
            return Float.parseFloat(yString);
        } else {
            return roundY(Float.parseFloat(yString));
        }
    }

    public void addFromJS(){
        long timer = System.nanoTime();
        final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        params.values().forEach(System.out::println);

        try {
            float x = Float.parseFloat(params.get("x"));
            float y = processY(params.get("y"));
            float r = Float.parseFloat(params.get("r"));

            final Point attemptBean = new Point(x, y, r);
            attemptBean.setTime(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
            attemptBean.setStatus(PointValidator.isHit(x, y, r));
            attemptBean.setScriptTime((long) ((System.nanoTime() - timer) * 0.01));
            this.addPoint(attemptBean);

            managerBean.handlePointsWithMBeans(attemptBean);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
        }
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void addPoint(Point point){
        DatabaseHandler.getDatabaseManager().addPoint(point);
        this.points.addFirst(point);
    }

    public void setPoints(LinkedList<Point> points) {
        this.points = points;
    }

    public int getTotalPoints() {
        return managerBean.getTotalPoints();
    }

    public int getMissedPoints() {
        return managerBean.getMissedPoints();
    }

    public int getConsecutiveMisses() {
        return managerBean.getConsecutiveMisses();
    }

    public double getAverageInterval() {
        return managerBean.getAverageInterval();
    }
    public MBeanNotificationInfo[] getNotificationMessage() {
        return managerBean.getNotificationMessage();
    }
}
