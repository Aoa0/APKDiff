package edu.sjtu.gosec.apkdiff.util;

import soot.SootClass;

public class HierarchyTree {
    //TODO
    public HierarchyNode root;
    public HierarchyTree() {
        root = new HierarchyNode();
    }

    public HierarchyNode findNode(String packageName) {
        HierarchyNode i = root;
        if (packageName.length()>0) {
            String[] names = packageName.split("\\.");
            for (String name : names) {
                if (i.checkChild(name)) {
                    i = i.getChild(name);
                } else {
                    i = i.addChild(name);
                }
            }
        }
        return i;
    }

    public void addNode(String packageName) {
        findNode(packageName);
    }

    public void addNode(String packageName, SootClass clz) {
        HierarchyNode i = findNode(packageName);
        i.addClass(clz);
    }

    public void addNode(SootClass clz) {
        addNode(clz.getPackageName(), clz);
    }

    public void upgrade(SootClass clz) {
        HierarchyNode field = findNode(clz.getPackageName());
        if(field == root) {
            return;
        }
        field.removeClass(clz);
        field = field.getFather();
        field.addClass(clz);
    }
}
