package edu.sjtu.gosec.apkdiff;

import soot.SootClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    public static boolean validateFile(String filePath) {
        return new File(filePath).isFile();
    }

    public static boolean validateDirectory(String filePath) {
        return new File(filePath).isDirectory();
    }

    public static Set<String> readLinesToSet(String filePath) {
        System.out.println("readlinestoset");
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.collect(Collectors.toSet());
        } catch (IOException e) {
            return null;
        }
    }

    public static int sumList(ArrayList<Integer> list) {
        int ret = 0;
        for (Integer i: list) {
            ret += i;
        }
        return ret;
    }

    public static class Counter<T> {

        final ConcurrentMap<T, Integer> counts = new ConcurrentHashMap<>();

        public void put(T it) {
            add(it, 1);
        }

        public void add(T it, int v) {
            counts.merge(it, v, Integer::sum);
        }

        public List<T> mostCommon(int n) {
            return counts.entrySet().stream()
                    // Sort by value.
                    .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                    // Top n.
                    .limit(n)
                    // Keys only.
                    .map(Map.Entry::getKey)
                    // As a list.
                    .collect(Collectors.toList());
        }
    }

    public static void compareStringSet(Set<String> s1, Set<String> s2) {
        Set<String> s1Only = new HashSet<>(s1);
        Set<String> s2Only = new HashSet<>(s2);

        s1Only.removeAll(s2);
        s2Only.removeAll(s1);

        System.out.println("set1 size: " + s1.size());
        System.out.println("set2 size: " + s2.size());

        System.out.println("set1 only: " + s1Only.size() + " " + s1Only);
        System.out.println("set2 only: " + s2Only.size() + " " + s2Only);

    }

    public static boolean isResourceClass(String className) {
        return className.endsWith(".R");
    }

    public static boolean isResourceClass(SootClass sootClass) {
        String classSignature = sootClass.getName();
        return isResourceClass(classSignature);
    }

    public static boolean isAndroidClass(SootClass sootClass) {
        String classSignature = sootClass.getName();
        return isAndroidClass(classSignature);
    }

    public static boolean isAndroidClass(String className) {
        List<String> androidPrefixPkgNames = Arrays.asList("android.", "com.google.android.", "com.android.", "androidx.",
                "kotlin.", "java.", "javax.", "sun.", "com.sun.", "jdk.", "j$.",
                "org.omg.", "org.xml.", "org.w3c.dom");
        return androidPrefixPkgNames.stream().map(className::startsWith).reduce(false, (res, curr) -> res || curr);
    }

    public static String getRawType(String type) {
        return type.replace("[]", "").split("\\$")[0];
    }
}
