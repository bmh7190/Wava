package wava.service.jit;

import wava.model.jit.JitFilterPreset;
import wava.model.process.JavaProcessInfo;

public class JitDefaultFilterPolicy {
    public JitDefaultFilter resolve(JavaProcessInfo process) {
        if (process == null) {
            return new JitDefaultFilter(JitFilterPreset.ALL, "");
        }

        String displayName = process.getDisplayName();
        if (isArchiveProcess(displayName)) {
            return new JitDefaultFilter(JitFilterPreset.ALL, "");
        }

        return new JitDefaultFilter(JitFilterPreset.CURRENT_TARGET, createCurrentTargetFilter(displayName));
    }

    private boolean isArchiveProcess(String displayName) {
        String lowerCaseName = displayName.toLowerCase();
        return lowerCaseName.endsWith(".jar") || lowerCaseName.endsWith(".war");
    }

    private String createCurrentTargetFilter(String displayName) {
        int packageSeparator = displayName.lastIndexOf('.');
        if (packageSeparator >= 0 && packageSeparator + 1 < displayName.length()) {
            return displayName.substring(packageSeparator + 1);
        }
        return displayName;
    }
}
