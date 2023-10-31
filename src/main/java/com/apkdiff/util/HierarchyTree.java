package com.apkdiff.util;

import com.apkdiff.analysis.ObfuscationAnalysis;
import com.apkdiff.profile.ClassProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HierarchyTree {
    public HierarchyNode root;

    public HierarchyTree() {
        root = new HierarchyNode();
    }

    public void addClass(String packageName, ClassProfile clz) {
        if (packageName.length() < 1) {
            addClassToRoot(clz);
            return;
        }

        String[] names = packageName.split("\\.");
        List<String> newPack = new ArrayList<>();
        for (String name : names) {
            if (!ObfuscationAnalysis.isIdentifierObfuscated(name)) {
                newPack.add(name);
            }
        }

        if (newPack.size() == 0) {
            addClassToRoot(clz);
        } else {
            HierarchyNode i = root;
            for (String name : newPack) {
                if (i.hasChild(name)) {
                    i = i.getChild(name);
                } else {
                    i = i.addChild(name);
                }
            }
            i.addClass(clz);
        }

    }

    private void addClassToRoot(ClassProfile clz) {
        root.addClass(clz);
    }

    public void Show() {
        show(root, 0);
    }

    private void show(HierarchyNode node, int level) {
        for (Map.Entry<String, HierarchyNode> entry : node.getChild().entrySet()) {
            System.out.println(" ".repeat(level) + "-" + entry.getKey());
            show(entry.getValue(), level + 1);
        }
    }
}
