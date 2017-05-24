package meme.common;

public class StopWatch {
	/*
	 * This class is not used in this iteration. It is designed for the 
	 * development of a trackbar, available in future releases.
	 */
    private boolean running = false;
    private boolean paused = false;
    private long startTime = 0;
    private long pausedTime = 0;
    private long end = 0;

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public void start() {
        startTime = System.nanoTime();
        running = true;
        paused = false;
        pausedTime = -1;
    }

    public long stop() {
        if (!isRunning()) {
            return -1;
        } else if (isPaused()) {
            running = false;
            paused = false;

            return pausedTime - startTime;
        } else {
            end = System.nanoTime();
            running = false;
            return end - startTime;
        }
    }

    public long pause() {
        if (!isRunning()) {
            return -1;
        } else if (isPaused()) {
            return pausedTime - startTime;
        } else {
            pausedTime = System.nanoTime();
            paused = true;
            return pausedTime - startTime;
        }
    }

    public void resume() {
        if (isPaused() && isRunning()) {
            startTime = System.nanoTime() - (pausedTime - startTime);
            paused = false;
        }
    }

    public long elapsed() {
        if (isRunning()) {
            if (isPaused())
                return pausedTime - startTime;
            return System.nanoTime() - startTime;
        } else
            return end - startTime;
    }

    @Override
    public String toString() {
        long elapsed = elapsed();
        int seconds = (int)((double)elapsed / Math.pow(10, 9));
        return seconds + " Seconds";
    }

}
