package sample;

public class CpuWarmupTarget {
    private static final int BATCH_SIZE = 18_000;

    private long checksum;

    public static void main(String[] args) {
        new CpuWarmupTarget().run(SampleTargetSupport.durationMillis(args));
    }

    private void run(long durationMillis) {
        long endTime = System.currentTimeMillis() + durationMillis;
        int round = 0;
        while (System.currentTimeMillis() < endTime) {
            executeBatch(round);
            if (round % 200 == 0) {
                SampleTargetSupport.printStatus("CpuWarmupTarget", round, checksum);
                SampleTargetSupport.sleep(25L);
            }
            round++;
        }
        SampleTargetSupport.printStatus("CpuWarmupTarget", round, checksum);
    }

    private void executeBatch(int round) {
        for (int index = 0; index < BATCH_SIZE; index++) {
            checksum += calculate(index, round);
            checksum ^= rotate(index + round);
        }
    }

    private long calculate(int value, int round) {
        long result = value * 31L + round * 17L;
        result = result * result + 97L;
        result ^= result >>> 11;
        return result;
    }

    private long rotate(int value) {
        long result = value;
        for (int index = 0; index < 6; index++) {
            result = Long.rotateLeft(result * 1_664_525L + 1_013_904_223L, 7);
        }
        return result;
    }
}
