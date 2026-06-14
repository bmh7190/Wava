package sample;

final class SampleTargetSupport {
    private static final long DEFAULT_DURATION_MILLIS = 120_000L;

    private SampleTargetSupport() {
    }

    static long durationMillis(String[] args) {
        if (args.length == 0) {
            return DEFAULT_DURATION_MILLIS;
        }
        try {
            long seconds = Long.parseLong(args[0]);
            if (seconds <= 0L) {
                return DEFAULT_DURATION_MILLIS;
            }
            return seconds * 1_000L;
        } catch (NumberFormatException exception) {
            return DEFAULT_DURATION_MILLIS;
        }
    }

    static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    static void printStatus(String targetName, int round, long checksum) {
        System.out.println(targetName + " round=" + round + " checksum=" + checksum);
    }
}
