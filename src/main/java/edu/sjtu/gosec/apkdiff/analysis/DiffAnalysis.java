package edu.sjtu.gosec.apkdiff.analysis;

import edu.sjtu.gosec.apkdiff.profile.AppProfile;
import edu.sjtu.gosec.apkdiff.profile.ClassProfile;
import edu.sjtu.gosec.apkdiff.util.HierarchyTree;

import java.util.HashMap;
import java.util.Map;

public class DiffAnalysis {

    private final AppProfile srcProfile;
    private final AppProfile tarProfile;
    private HierarchyTree srcTree;
    private HierarchyTree tarTree;
    private final Map<String, ClassProfile> SourceRemain;
    private final Map<String, ClassProfile> TargetRemain;
    private final Map<String, String> matches;

    public DiffAnalysis(AppProfile src, AppProfile tar) {
        this.matches = new HashMap<>();
        this.srcProfile = src;
        this.tarProfile = tar;
        this.srcTree = src.hierarchyTree;
        this.tarTree = tar.hierarchyTree;
        this.SourceRemain = src.getAllClasses();
        this.TargetRemain = src.getAllClasses();
    }

    private void diff() {
        boolean sign = true;
        while (sign) {
            sign = matchIterate();
        }
        show();
    }

    private boolean matchIterate() {
        boolean sign = false;
        //ToDo: Transfer Function
        return sign;
    }

    private void show() {
        //ToDo: Show the result
    }
}
