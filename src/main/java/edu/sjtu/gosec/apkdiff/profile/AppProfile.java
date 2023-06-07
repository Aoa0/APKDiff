package edu.sjtu.gosec.apkdiff.profile;

import edu.sjtu.gosec.apkdiff.Utils;
import soot.SootClass;
import soot.util.Chain;
import edu.sjtu.gosec.apkdiff.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppProfile {

    public HierarchyTree hierarchyTree;
    private final Chain<SootClass> sootClasses;
    private final Map<String, ClassProfile> allClasses;

    public AppProfile(Chain<SootClass> classes) {
        this.sootClasses = classes;
        this.hierarchyTree = new HierarchyTree();
        this.allClasses = new HashMap<>();
        buildHierarchy();
    }

    private void buildHierarchy() {
        List<SootClass> toMerge = new ArrayList<>();
        for (SootClass clz : sootClasses) {
            //ToDo: currently not analyzing these classes for efficiency
            if (clz.isPhantomClass() || Utils.isAndroidClass(clz) || Utils.isResourceClass(clz)) {
                continue;
            }
            String clzName = clz.getName();
            if (clzName.contains("$")) {
                toMerge.add(clz);
                continue;
            }
            ClassProfile classProfile = new ClassProfile(clz);
            hierarchyTree.addClass(clz.getPackageName(), classProfile);
            allClasses.put(clzName, classProfile);
        }
        //mergeInnerclass(toMerge);
    }

    private void mergeInnerclass(List<SootClass> toMerge) {
        for (SootClass clz : toMerge) {
            //ToDo: Innerclass need to be finished
            //System.out.println(clz.getName());
        }
    }

    public Map<String, ClassProfile> getAllClasses() {
        return allClasses;
    }
}
