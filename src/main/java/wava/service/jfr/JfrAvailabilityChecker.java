package wava.service.jfr;

import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;
import wava.model.jfr.JfrAvailabilityStatus;

public class JfrAvailabilityChecker {
    private final BooleanSupplier availabilityProbe;

    public JfrAvailabilityChecker() {
        this(JfrAvailabilityChecker::detectJfrAvailability);
    }

    JfrAvailabilityChecker(BooleanSupplier availabilityProbe) {
        this.availabilityProbe = availabilityProbe;
    }

    public JfrAvailabilityStatus check() {
        try {
            if (availabilityProbe.getAsBoolean()) {
                return JfrAvailabilityStatus.available();
            }
            return JfrAvailabilityStatus.unavailable("JFR is present but not available in this runtime.");
        } catch (RuntimeException exception) {
            return JfrAvailabilityStatus.unavailable(exception.getMessage());
        }
    }

    private static boolean detectJfrAvailability() {
        try {
            Class<?> flightRecorderClass = Class.forName("jdk.jfr.FlightRecorder");
            Method isAvailableMethod = flightRecorderClass.getMethod("isAvailable");
            Object result = isAvailableMethod.invoke(null);
            return Boolean.TRUE.equals(result);
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException("JFR API is not available: " + exception.getMessage());
        }
    }
}
