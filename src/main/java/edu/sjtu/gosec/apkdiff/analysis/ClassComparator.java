package edu.sjtu.gosec.apkdiff.analysis;

import edu.sjtu.gosec.apkdiff.profile.ClassProfile;

public class ClassComparator {
    private final ClassProfile c1;
    private final ClassProfile c2;

    public ClassComparator(ClassProfile c1, ClassProfile c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public boolean similar() {
        return compareByMethodNum();
    }

    private boolean compareByMethodNum() {
        int n1 = c1.getMethodNum();
        int n2 = c2.getMethodNum();
        return n1==n2;
    }
}
