package edu.sjtu.gosec.apkdiff.util;

import soot.SootClass;
import soot.util.Chain;

import java.util.HashMap;

public class HierarchyNode extends Node<SootClass>{
    public Chain<SootClass> Classes;
    public HashMap<String, HierarchyNode> Child;

    public HierarchyNode() {
        super();
        Child = new HashMap<String, HierarchyNode>();
        Classes = null;
    }

    public void addClass(SootClass clz) {
        Classes.add(clz);
    }

    public void addChild(String className, HierarchyNode child) {
        Child.put(className, child);
    }

    public HierarchyNode addChild(String className) {
        HierarchyNode a = new HierarchyNode();
        a.setFather(this);
        Child.put(className, a);
        return a;
    }

    public boolean removeClass(SootClass clz) {
        return Classes.remove(clz);
    }

    public Chain<SootClass> getClasses() {
        return Classes;
    }

    public HierarchyNode getChild(String name) {
        return Child.get(name);
    }

    public boolean checkChild(String name) {
        return Child.containsKey(name);
    }

    @Override
    public HierarchyNode getFather() {
        return (HierarchyNode) super.getFather();
    }
}
