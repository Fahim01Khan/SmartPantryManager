package com.fahimkhan.smartpantry.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Converts ingredient names into one canonical form so that names which mean
 * the same ingredient compare as equal: "  Tomatoes " and "tomato" both become
 * "tomato"; "Cake flour" becomes "flour".
 *
 * Both pantry names and recipe names pass through this class, so even an
 * imperfect rule is safe as long as singular and plural forms end up the same.
 */
public final class IngredientNormaliser {

    /** Plurals the general rules would get wrong. */
    private static final Map<String, String> IRREGULAR_PLURALS = new HashMap<>();

    /** Words that end in "s" but are already singular. */
    private static final Set<String> UNCHANGED_WORDS = new HashSet<>();

    /** Common alternative names, mapped to the name the recipes use. Keys are already singular. */
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        IRREGULAR_PLURALS.put("leaves", "leaf");
        IRREGULAR_PLURALS.put("loaves", "loaf");
        IRREGULAR_PLURALS.put("halves", "half");

        UNCHANGED_WORDS.add("hummus");
        UNCHANGED_WORDS.add("couscous");
        UNCHANGED_WORDS.add("asparagus");
        UNCHANGED_WORDS.add("molasses");

        SYNONYMS.put("cooking oil", "oil");
        SYNONYMS.put("sunflower oil", "oil");
        SYNONYMS.put("vegetable oil", "oil");
        SYNONYMS.put("olive oil", "oil");
        SYNONYMS.put("cake flour", "flour");
        SYNONYMS.put("plain flour", "flour");
        SYNONYMS.put("all purpose flour", "flour");
        SYNONYMS.put("white sugar", "sugar");
        SYNONYMS.put("brown sugar", "sugar");
        SYNONYMS.put("castor sugar", "sugar");
        SYNONYMS.put("full cream milk", "milk");
        SYNONYMS.put("low fat milk", "milk");
        SYNONYMS.put("white bread", "bread");
        SYNONYMS.put("brown bread", "bread");
        SYNONYMS.put("cheddar", "cheese");
        SYNONYMS.put("cheddar cheese", "cheese");
        SYNONYMS.put("gouda", "cheese");
        SYNONYMS.put("gouda cheese", "cheese");
        SYNONYMS.put("spaghetti", "pasta");
        SYNONYMS.put("macaroni", "pasta");
        SYNONYMS.put("penne", "pasta");
        SYNONYMS.put("white rice", "rice");
        SYNONYMS.put("basmati rice", "rice");
        SYNONYMS.put("garlic clove", "garlic");
    }

    private IngredientNormaliser() {
        // Static methods only
    }

    /**
     * Returns the canonical form of an ingredient name: lowercase, single-spaced,
     * last word made singular, and common alternative names replaced.
     */
    public static String normalise(String name) {
        if (name == null) {
            return "";
        }
        String cleaned = name.trim()
                .toLowerCase(Locale.ROOT)
                .replace('-', ' ')            // "all-purpose" -> "all purpose"
                .replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) {
            return cleaned;
        }

        // Only the last word carries the plural: "green peppers" -> "green pepper"
        int lastSpace = cleaned.lastIndexOf(' ');
        String singular = cleaned.substring(0, lastSpace + 1)
                + singularise(cleaned.substring(lastSpace + 1));

        String synonym = SYNONYMS.get(singular);
        return synonym != null ? synonym : singular;
    }

    /** Converts one lowercase word to its singular form using common English rules. */
    private static String singularise(String word) {
        if (word.length() <= 3 || UNCHANGED_WORDS.contains(word)) {
            return word;
        }
        String irregular = IRREGULAR_PLURALS.get(word);
        if (irregular != null) {
            return irregular;
        }
        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";      // berries -> berry
        }
        if (word.endsWith("oes") || word.endsWith("ches") || word.endsWith("shes")
                || word.endsWith("xes") || word.endsWith("sses") || word.endsWith("zes")) {
            return word.substring(0, word.length() - 2);            // tomatoes -> tomato
        }
        if (word.endsWith("s") && !word.endsWith("ss")
                && !word.endsWith("us") && !word.endsWith("is")) {
            return word.substring(0, word.length() - 1);            // eggs -> egg, apples -> apple
        }
        return word;
    }
}
