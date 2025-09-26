package Controller;

public class Timer {
    //Do we want this as its own class or should it just be in controller??
    private static long endingTime;


    public static void set(int durationSeconds) {
        long now = System.currentTimeMillis();
        endingTime = now + (durationSeconds * 1000L);
    }


    public static boolean timerEnded() {
        return System.currentTimeMillis() >= endingTime;
    }

    //Do we want a clear method like in the srs or no

}
