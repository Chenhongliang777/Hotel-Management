package com.homestay.context;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    public static void setContext(UserContext context) {
        CONTEXT.set(context);
    }

    public static UserContext getContext() {
        return CONTEXT.get();
    }

    public static Long getUserId() {
        UserContext context = getContext();
        return context != null ? context.getUserId() : null;
    }

    public static String getUsername() {
        UserContext context = getContext();
        return context != null ? context.getUsername() : null;
    }

    public static String getUserType() {
        UserContext context = getContext();
        return context != null ? context.getUserType() : null;
    }

    public static String getRole() {
        UserContext context = getContext();
        return context != null ? context.getRole() : null;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
