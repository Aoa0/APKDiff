package com.apkdiff.profile;

import com.apkdiff.Utils;
import com.apkdiff.util.HierarchyTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final String packageName;
    private final String versionName;
    private final int versionCode;
    private final String apkPath;
    private final Logger logger = LoggerFactory.getLogger(AppProfile.class);


    public AppProfile(String apkPath, ProcessManifest manifest) {
        this.apkPath = apkPath;
        this.packageName = manifest.getPackageName();
        this.versionName = manifest.getVersionName();
        this.versionCode = manifest.getVersionCode();

        logger.info("Profiling: " + apkPath);
        logger.info("package name: " + this.packageName);
        logger.info("version code: " + this.versionCode);
        logger.info("version name: " + this.versionName);

        logger.info("Run Soot Packs.");
        PackManager.v().runPacks();

        logger.info("Run APKDiff Profiler.");
        this.allClasses = new HashMap<>();
        this.sootClasses = Scene.v().getApplicationClasses();
        logger.info("Build Hierarchy Tree.");
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

            ClassProfile classProfile = new ClassProfile(clz);
            //System.out.println(classProfile.getFieldHash());
            hierarchyTree.addClass(clz.getPackageName(), classProfile);
            allClasses.put(clzName, classProfile);
        }
        logger.info("Class Number: " + allClasses.size());
        //mergeInnerclass(toMerge);
    }

    private void mergeInnerclass(List<SootClass> toMerge) {
        for (SootClass clz : toMerge) {
            //ToDo: Innerclass need to be finished
            System.out.println(clz.getName());
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public Map<String, ClassProfile> getAllClasses() {
        return allClasses;
    }
}
