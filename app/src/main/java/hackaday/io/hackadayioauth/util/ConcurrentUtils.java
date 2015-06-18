package hackaday.io.hackadayioauth.util;

/**
 * Created by paul on 2015/06/16.
 */
public class ConcurrentUtils {

    /**
     * Executes the network requests on a separate thread.
     *
     * @param runnable The runnable instance containing network mOperations to be
     *                 executed.
     */
    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };

        t.start();

        return t;
    }

}

