package edu.sjtu.gosec.apkdiff.profile;

import edu.sjtu.gosec.apkdiff.Utils;
import soot.SootClass;
import soot.util.Chain;
import edu.sjtu.gosec.apkdiff.util.*;
import edu.sjtu.gosec.apkdiff.analysis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AppProfile {

    public HierarchyTree hierarchyTree;
    private final Chain<SootClass> sootClasses;

    public AppProfile(Chain<SootClass> classes) {
        this.sootClasses = classes;
        this.hierarchyTree = new HierarchyTree();
        buildHierarchy();
    }

    private void buildHierarchy() {
        List<SootClass> toMerge = new ArrayList<>();
        for(SootClass clz:sootClasses) {
            //ToDo: currently not analyzing these classes for efficiency
            if (clz.isPhantomClass() || Utils.isAndroidClass(clz) || Utils.isResourceClass(clz)) {
                continue;
            }
            String clazzName = clz.getName();
            if (clazzName.contains("$")) {
                toMerge.add(clz);
                continue;
            }
            hierarchyTree.addNode(clz);
        }
    }

    public void PackageSqueezing() {
        boolean sign = packageSqueezing(hierarchyTree.root);
    }

    private boolean packageSqueezing(HierarchyNode node) {
        boolean sign = false;
        List<String> removed = new ArrayList<>();
        List<String> names = new ArrayList<>(node.getChild().keySet());
        for(String name : names) {
            sign = sign || packageSqueezing(node.getChild().get(name));
            if(ObfuscationAnalysis.isIdentifierObfuscated(name)) {
                hierarchyTree.Squeezing(name, node.getChild().get(name));
                sign = true;
                removed.add(name);
            }
        }
        for(String c:removed) {
            node.removeChild(c);
        }
        return sign;
    }
}
