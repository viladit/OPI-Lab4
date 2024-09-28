package mbeans;

public class ClickInterval implements ClickIntervalMBean {
    private long lastClickTime = 1;
    private double totalInterval = 1;
    private int clickCount = 0;

    public void registerClick() {
        long currentTime = System.currentTimeMillis();
        if (lastClickTime != 0) {
            totalInterval += (currentTime - lastClickTime);
            clickCount++;
        }
        lastClickTime = currentTime;
    }

    @Override
    public double getAverageInterval() {
        registerClick();
        return clickCount == 0 ? 0 : totalInterval / clickCount;
    }
}
