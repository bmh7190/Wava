package wava.service.jit;

import wava.model.jit.JitEvent;

public class JitEventFilter {
    private final String keyword;

    public JitEventFilter(String keyword) {
        this.keyword = normalize(keyword);
    }

    public boolean matches(JitEvent event) {
        if (keyword.isEmpty()) {
            return true;
        }
        return normalize(event.getMethodName()).contains(keyword)
                || normalize(event.getRawLine()).contains(keyword);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase();
    }
}
