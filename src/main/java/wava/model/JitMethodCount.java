package wava.model;

public class JitMethodCount {
    private final String methodName;
    private final int count;

    public JitMethodCount(String methodName, int count) {
        this.methodName = methodName;
        this.count = count;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getCount() {
        return count;
    }
}
