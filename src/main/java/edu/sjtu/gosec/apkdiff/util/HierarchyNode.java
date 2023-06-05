package edu.sjtu.gosec.apkdiff.util;

import soot.SootClass;

import java.util.ArrayList;
import java.util.HashMap;

public class HierarchyNode extends Node<SootClass>{
    public ArrayList<SootClass> Classes;
    public HashMap<String, HierarchyNode> Child;

    public HierarchyNode() {
        super();
        Child = new HashMap<String, HierarchyNode>();
        Classes = new ArrayList<SootClass>();
    }

    public void addClass(SootClass clz) {
        Classes.add(clz);
    }

    public void addClass(ArrayList<SootClass> clzes) {
        Classes.addAll(clzes);
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

    public ArrayList<SootClass> getClasses() {
        return Classes;
    }

    public HierarchyNode getChild(String name) {
        return Child.get(name);
    }

    public boolean removeChild(String name) {
        return removeChild(name, Child.get(name));
    }

    public boolean removeChild(String name, HierarchyNode node) {
        return Child.remove(name, node);
    }

    public HashMap<String, HierarchyNode> getChild() {
        return Child;
    }

    public boolean checkChild(String name) {
        return Child.containsKey(name);
    }

    @Override
    public HierarchyNode getFather() {
        return (HierarchyNode) super.getFather();
    }
}
