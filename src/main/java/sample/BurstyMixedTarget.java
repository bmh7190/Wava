package sample;

public class BurstyMixedTarget {
    private long checksum;

    public static void main(String[] args) {
        new BurstyMixedTarget().run(SampleTargetSupport.durationMillis(args));
    }

    private void run(long durationMillis) {
        long endTime = System.currentTimeMillis() + durationMillis;
        int round = 0;
        while (System.currentTimeMillis() < endTime) {
            int phase = (round / 30) % 4;
            if (phase == 0 || phase == 1) {
                runCpuBurst(round);
            } else if (phase == 2) {
                runAllocationBurst(round);
            } else {
                SampleTargetSupport.sleep(160L);
            }
            if (round % 30 == 0) {
                SampleTargetSupport.printStatus("BurstyMixedTarget", round, checksum);
            }
            round++;
        }
        SampleTargetSupport.printStatus("BurstyMixedTarget", round, checksum);
    }

    private void runCpuBurst(int round) {
        for (int index = 0; index < 14_000; index++) {
            long value = index * 43L + round;
            checksum += Long.rotateRight(value * value + 19L, index % 13);
        }
        SampleTargetSupport.sleep(20L);
    }

    private void runAllocationBurst(int round) {
        byte[][] buffers = new byte[6][];
        for (int index = 0; index < buffers.length; index++) {
            buffers[index] = new byte[48 * 1024 + (round + index) % 1_024];
            buffers[index][0] = (byte) (round + index);
            checksum += buffers[index][0];
        }
        SampleTargetSupport.sleep(55L);
    }
}
