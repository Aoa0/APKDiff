package edu.sjtu.gosec.apkdiff.util;

import edu.sjtu.gosec.apkdiff.profile.ClassProfile;

import java.util.ArrayList;
import java.util.HashMap;

public class HierarchyNode extends Node<ClassProfile>{
    public ArrayList<ClassProfile> Classes;
    public HashMap<String, HierarchyNode> Child;

    public HierarchyNode() {
        super();
        Child = new HashMap<>();
        Classes = new ArrayList<>();
    }

    public void addClass(ClassProfile clz) {
        Classes.add(clz);
    }

    public void addClass(ArrayList<ClassProfile> clzes) {
        Classes.addAll(clzes);
    }

    public void addChild(String className, HierarchyNode child) {
        Child.put(className, child);
    }

    public HierarchyNode addChild(String name) {
        HierarchyNode a = new HierarchyNode();
        a.setFather(this);
        Child.put(name, a);
        return a;
    }

    public boolean removeClass(ClassProfile clz) {
        return Classes.remove(clz);
    }

    public ArrayList<ClassProfile> getClasses() {
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

    public boolean hasChild(String name) {
        return Child.containsKey(name);
    }

    @Override
    public HierarchyNode getFather() {
        return (HierarchyNode) super.getFather();
    }
}
