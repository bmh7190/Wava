package sample;

import java.util.ArrayDeque;
import java.util.Queue;

public class GcPulseTarget {
    private static final int SHORT_LIVED_BUFFER_COUNT = 10;
    private static final int SHORT_LIVED_BUFFER_SIZE = 32 * 1024;
    private static final int RETAINED_BUFFER_SIZE = 128 * 1024;
    private static final int MAX_RETAINED_BUFFERS = 18;

    private final Queue<byte[]> retainedBuffers = new ArrayDeque<>();
    private long checksum;

    public static void main(String[] args) {
        new GcPulseTarget().run(SampleTargetSupport.durationMillis(args));
    }

    private void run(long durationMillis) {
        long endTime = System.currentTimeMillis() + durationMillis;
        int round = 0;
        while (System.currentTimeMillis() < endTime) {
            allocatePulse(round);
            checksum += round * 13L;
            if (round % 20 == 0) {
                SampleTargetSupport.printStatus("GcPulseTarget", round, checksum);
            }
            SampleTargetSupport.sleep(35L);
            round++;
        }
        SampleTargetSupport.printStatus("GcPulseTarget", round, checksum);
    }

    private void allocatePulse(int round) {
        byte[][] shortLivedBuffers = new byte[SHORT_LIVED_BUFFER_COUNT][];
        for (int index = 0; index < shortLivedBuffers.length; index++) {
            shortLivedBuffers[index] = new byte[SHORT_LIVED_BUFFER_SIZE + (round + index) % 512];
            shortLivedBuffers[index][0] = (byte) index;
            checksum += shortLivedBuffers[index][0];
        }
        if (round % 5 == 0) {
            retainedBuffers.add(new byte[RETAINED_BUFFER_SIZE]);
            while (retainedBuffers.size() > MAX_RETAINED_BUFFERS) {
                retainedBuffers.poll();
            }
        }
    }
}
