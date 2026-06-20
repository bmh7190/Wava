# Wava

## Sample Targets

Compile the project first, then run all sample target JVMs together:

```text
javac -encoding UTF-8 -d out <all java files under src>
java -cp out sample.SampleTargetLauncher
```

The launcher starts these separate Java processes:

- `sample.CpuWarmupTarget`: CPU and JIT warm-up focused workload
- `sample.GcPulseTarget`: controlled allocation and GC pulse workload
- `sample.SteadyStateTarget`: low and stable workload
- `sample.BurstyMixedTarget`: alternating CPU, allocation, and idle phases

Each target writes its own JIT compilation log under `logs/`:

- `logs/cpu-warmup-jit.log`
- `logs/gc-pulse-jit.log`
- `logs/steady-state-jit.log`
- `logs/bursty-mixed-jit.log`

You can pass a duration in seconds:

```text
java -cp out sample.SampleTargetLauncher 180
```

## Windows App Image

Build a Windows app-image that contains `Wava.exe` and a bundled runtime:

```powershell
.\scripts\package-windows.ps1 -Clean
```

The executable is created at:

```text
dist\Wava\Wava.exe
```

This packaging path uses the JDK `jpackage` tool with `--type app-image`, so it does not require an installer generator such as WiX. Run the script with a JDK that includes `javac`, `jar`, and `jpackage`.
