package edu.sjtu.gosec.apkdiff.profile;

import edu.sjtu.gosec.apkdiff.Utils;
import soot.Modifier;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
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
    private final DexProfile dexProfile;
    private final String packageName;
    private final String apkPath;


    public AppProfile(String apkPath, ProcessManifest manifest) {
        dexProfile = new DexProfile(apkPath);
        PackManager.v().runPacks();
        this.apkPath = apkPath;
        this.allClasses = new HashMap<>();
        this.sootClasses = Scene.v().getApplicationClasses();
        this.packageName = manifest.getPackageName();
        this.hierarchyTree = new HierarchyTree();
        buildHierarchy();
    }

    private void buildHierarchy() {
        List<SootClass> toMerge = new ArrayList<>();
        for (SootClass clz : sootClasses) {
            //ToDo: currently not analyzing these classes for efficiency
            if (clz.isPhantomClass()
                    || Utils.isAndroidClass(clz)
                    || Utils.isResourceClass(clz)
                    || Modifier.isSynthetic(clz.getModifiers())
            ) {
                continue;
            }
            String clzName = clz.getName();

            /*
            if (clzName.contains("$")) {
                toMerge.add(clz);
                continue;
            }
            */

            ClassProfile classProfile = new ClassProfile(clz, dexProfile);
            //System.out.println(classProfile.getFieldHash());
            hierarchyTree.addClass(clz.getPackageName(), classProfile);
            allClasses.put(clzName, classProfile);
        }
        System.out.println(allClasses.size());
        //mergeInnerclass(toMerge);
    }

    private void mergeInnerclass(List<SootClass> toMerge) {
        for (SootClass clz : toMerge) {
            //ToDo: Innerclass need to be finished
            System.out.println(clz.getName());
        }
    }

    public Map<String, ClassProfile> getAllClasses() {
        return allClasses;
    }
}
