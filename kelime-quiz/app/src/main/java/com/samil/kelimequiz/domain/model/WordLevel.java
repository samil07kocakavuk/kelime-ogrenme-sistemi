package com.samil.kelimequiz.domain.model;

import java.util.Locale;

public enum WordLevel {
    A1,
    A2,
    B1,
    B2,
    C1,
    C2;

    public static WordLevel fromCategory(String category) {
        if (category == null) {
            return A1;
        }

        switch (category) {
            case "Hayvanlar":
            case "Yiyecekler":
            case "Ev ve Eşyalar":
            case "Okul":
                return A1;
            case "Meslekler":
            case "Ulaşım":
            case "Günlük Yaşam":
                return A2;
            case "Doğa":
            case "Sıfatlar":
            case "Fiiller":
            case "Sağlık ve Vücut":
            case "Sporlar":
                return B1;
            default:
                return A1;
        }
    }

    public static String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return A1.name();
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if ("A1".equals(normalized) || "A2".equals(normalized) || "B1".equals(normalized)
                || "B2".equals(normalized) || "C1".equals(normalized) || "C2".equals(normalized)) {
            return normalized;
        }
        return A1.name();
    }

    public String getDisplayName() {
        return name();
    }
}
