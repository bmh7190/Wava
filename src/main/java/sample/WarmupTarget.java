package sample;

public class WarmupTarget {
    private static final long RUN_MILLIS = 120_000L;
    private static final int BATCH_SIZE = 20_000;
    private static final int ALLOCATION_SIZE = 1_024;

    private long checksum;

    public static void main(String[] args) {
        WarmupTarget target = new WarmupTarget();
        target.run();
    }

    private void run() {
        long endTime = System.currentTimeMillis() + RUN_MILLIS;
        int round = 0;
        while (System.currentTimeMillis() < endTime) {
            executeBatch(round);
            round++;
            if (round % 100 == 0) {
                printStatus(round);
                pause();
            }
        }
        printStatus(round);
    }

    private void executeBatch(int round) {
        for (int index = 0; index < BATCH_SIZE; index++) {
            checksum += calculateValue(index, round);
            checksum ^= transformValue(index + round);
        }
        allocateShortLivedObjects(round);
    }

    private long calculateValue(int value, int round) {
        long result = value + 31L * round;
        result = result * result + 17L;
        result ^= result >>> 13;
        return result;
    }

    private long transformValue(int value) {
        long result = value;
        for (int index = 0; index < 8; index++) {
            result = result * 1_103_515_245L + 12_345L;
            result ^= result >>> 16;
        }
        return result;
    }

    private void allocateShortLivedObjects(int round) {
        byte[][] buffers = new byte[16][];
        for (int index = 0; index < buffers.length; index++) {
            buffers[index] = new byte[ALLOCATION_SIZE + (round + index) % 512];
            buffers[index][0] = (byte) index;
        }
        checksum += buffers[round % buffers.length][0];
    }

    private void printStatus(int round) {
        System.out.println("WarmupTarget round=" + round + " checksum=" + checksum);
    }

    private void pause() {
        try {
            Thread.sleep(50L);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
