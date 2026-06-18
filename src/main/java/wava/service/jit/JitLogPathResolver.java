package wava.service.jit;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import wava.model.process.JavaProcessInfo;

public class JitLogPathResolver {
    private static final Map<String, Path> SAMPLE_LOG_PATHS = Map.of(
            "sample.CpuWarmupTarget", Path.of("logs", "cpu-warmup-jit.log"),
            "sample.GcPulseTarget", Path.of("logs", "gc-pulse-jit.log"),
            "sample.SteadyStateTarget", Path.of("logs", "steady-state-jit.log"),
            "sample.BurstyMixedTarget", Path.of("logs", "bursty-mixed-jit.log"));

    public Optional<Path> resolve(JavaProcessInfo process) {
        if (process == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(SAMPLE_LOG_PATHS.get(process.getDisplayName()));
    }
}
