package edu.sjtu.gosec.apkdiff.analysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObfuscationAnalysis {

    private static final Set<String> javaKeywords = readJavaKeywords();

    public boolean isIdentifierObfuscated(String identifier) {
        return !isHardcodedException(identifier) && (isJavaKeyword(identifier) ||
                                                    isShortSize(identifier) ||
                                                    containRepeatedConsonants(identifier) ||
                                                    containsSpecialCharacters(identifier));
    }

    private boolean isJavaKeyword(String s) {
        return javaKeywords.contains(s);
    }

    private boolean isShortSize(String s) {
        return s.length() <= 2;
    }

    private boolean isHardcodedException(String s) {
        Set<String> hardcodedExceptions = new HashSet<>(Arrays.asList("id", "io", "r"));
        return hardcodedExceptions.contains(s.toLowerCase());
    }

    private boolean containRepeatedConsonants(String s) {
        Set<Character> consonants = new HashSet<>(Arrays.asList('b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'));
        for (int i = 0; i < s.length() - 2; i++) {
            char c = s.charAt(i);
            if (consonants.contains(s.charAt(i))) {
                if (s.charAt(i+1) == c && s.charAt(i+2) == c) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsSpecialCharacters(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '$' || c > 0x7F || c == '_') {
                return true;
            }
        }
        return false;
    }

    private static Set<String> readJavaKeywords() {
        return new HashSet<>(Arrays.asList("abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if",
                "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof",
                "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp",
                "volatile", "const", "float", "native", "super", "while"));
    }
}
