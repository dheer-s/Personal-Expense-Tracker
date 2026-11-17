package com.expensetracker.model;

/**
 * Enum representing pre-defined expense categories.
 * Demonstrates basic Java Enum type usage.
 */
public enum Category {
    FOOD("Food & Dining"),
    TRANSPORT("Transportation"),
    UTILITIES("Bills & Utilities"),
    ENTERTAINMENT("Entertainment"),
    HEALTH("Healthcare"),
    EDUCATION("Education"),
    SHOPPING("Shopping"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category fromString(String text) {
        if (text == null) return OTHER;
        for (Category c : Category.values()) {
            if (c.name().equalsIgnoreCase(text) || c.getDisplayName().equalsIgnoreCase(text)) {
                return c;
            }
        }
        return OTHER;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
