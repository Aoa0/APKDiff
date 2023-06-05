package edu.sjtu.gosec.apkdiff.util;

import soot.SootClass;

import java.util.Map;

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

    public void Squeezing(String name, HierarchyNode node) {
        HierarchyNode father = node.getFather();
        father.addClass(node.getClasses());
        father.removeChild(name);
        for(Map.Entry<String, HierarchyNode> entry : node.getChild().entrySet()) {
            entry.getValue().setFather(father);
            father.addChild(entry.getKey(), entry.getValue());
        }
    }

    public void show(HierarchyNode node, int level){
        for(Map.Entry<String, HierarchyNode> entry : node.getChild().entrySet()) {
            System.out.println(" ".repeat(level)+"-"+entry.getKey());
            show(entry.getValue(), level+1);
        }
    }
}
