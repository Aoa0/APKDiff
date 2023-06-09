package edu.sjtu.gosec.apkdiff.analysis;

import edu.sjtu.gosec.apkdiff.profile.ClassProfile;

import java.util.Objects;

public class ClassComparator {
    private final ClassProfile c1;
    private final ClassProfile c2;

    public ClassComparator(ClassProfile c1, ClassProfile c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public boolean similar() {
        return compareByMethodNum() && compareByMethod() && compareByFields();
    }

    private boolean compareByMethodNum() {
        int n1 = c1.getMethodNum();
        int n2 = c2.getMethodNum();
        return n1==n2;
    }

    private boolean compareByMethod() {
        String h1 = c1.getMethodHash();
        String h2 = c2.getMethodHash();
        return Objects.equals(h1, h2);
    }

    private boolean compareByFields() {
        String h1 = c1.getFieldHash();
        String h2 = c2.getFieldHash();
        return Objects.equals(h1, h2);
    }
}
