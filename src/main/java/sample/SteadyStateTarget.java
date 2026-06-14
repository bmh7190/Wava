package sample;

public class SteadyStateTarget {
    private long checksum;

    public static void main(String[] args) {
        new SteadyStateTarget().run(SampleTargetSupport.durationMillis(args));
    }

    private void run(long durationMillis) {
        long endTime = System.currentTimeMillis() + durationMillis;
        int round = 0;
        while (System.currentTimeMillis() < endTime) {
            for (int index = 0; index < 3_000; index++) {
                checksum += (index + 1L) * (round + 3L);
                checksum ^= checksum >>> 9;
            }
            if (round % 25 == 0) {
                SampleTargetSupport.printStatus("SteadyStateTarget", round, checksum);
            }
            SampleTargetSupport.sleep(120L);
            round++;
        }
        SampleTargetSupport.printStatus("SteadyStateTarget", round, checksum);
    }
}
