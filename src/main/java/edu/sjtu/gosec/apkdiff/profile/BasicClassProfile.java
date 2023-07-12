package edu.sjtu.gosec.apkdiff.profile;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.util.Chain;

import edu.sjtu.gosec.apkdiff.Utils;

import java.util.*;

public class BasicClassProfile {
    private final SootClass clz;
    private final String name;
    private final boolean isEnum;
    private final boolean isInterface;
    private final ClassType classType;
    private final List<String> interfaces;
    private final String superClass;
    private final List<SootMethod> methodList;
    private final List<MethodProfile> methodProfiles;
    private final List<SootField> fieldList;
    private final int methodNum;
    public boolean matched = false;
    private final Map<String, ArrayList<String>> methodInstructionMap;

    public BasicClassProfile(SootClass clz,  DexProfile dexProfile) {
        String superClass1;
        this.clz = clz;
        this.methodInstructionMap = dexProfile.getMethodInstructionMap();

        this.name = clz.getName();
        this.isEnum = clz.isEnum();
        this.isInterface = clz.isInterface();
        if (this.isEnum) {
            this.classType = ClassType.ENUM;
        } else if (this.isInterface) {
            this.classType = ClassType.INTERFACE;
        } else {
            this.classType = ClassType.CLASS;
        }

        this.interfaces = new ArrayList<>();
        for (SootClass sc : clz.getInterfaces()) {
            this.interfaces.add(sc.getName());
            Collections.sort(this.interfaces);
        }

        superClass1 = "";
        try {
            superClass1 = clz.getSuperclass().getName();
        } catch (Exception ignored) {
        }

        this.superClass = superClass1;
        this.methodList = new ArrayList<>(clz.getMethods());
        this.methodNum = this.clz.getMethodCount();

        this.methodProfiles = new ArrayList<>();
        constructMethodProfiles();

        this.fieldList = new ArrayList<>(clz.getFields());

    }

    private void constructMethodProfiles() {
        for (SootMethod m: this.methodList) {
            if (
//                m.isConstructor() ||
//                m.isStaticInitializer() ||
                    m.getName().startsWith("access$") ||
                            isDeprecated(m)) {
                continue;
            }
            String methodName = name + "." + m.getName();
            ArrayList<String> instructions = methodInstructionMap.get(methodName);
            MethodProfile p = new MethodProfile(m, instructions);

            this.methodProfiles.add(p);
        }
    }

    public List<MethodProfile> getMethodProfiles() {
        return methodProfiles;
    }

    public String getPackageName() {
        return clz.getPackageName();
    }

    public String getName() {
        return this.name;
    }

    public int getMethodNum() {
        return this.methodNum;
    }

    public List<SootMethod> getMethodList() {
        return this.methodList;
    }

    public String getMethodHash() {
        List<String> hashes = new ArrayList<>();
        for (MethodProfile method : methodProfiles) {
            hashes.add(method.getHash());
        }
        hashes.sort(Comparator.naturalOrder());
        return String.join("_", hashes);
    }

    public List<SootField> getFieldList() {
        return fieldList;
    }

    public String getFieldHash() {
        List<String> hashes = new ArrayList<>();
        for (SootField field : fieldList) {
            hashes.add(String.format("%d_%s",
                    field.getModifiers(), Utils.getHashType(field.getType().toString())));
        }
        return String.join(",", hashes);
    }

    private boolean isDeprecated(SootMethod sootMethod) {
        List<Tag> tags = sootMethod.getTags();
        for (Tag tag: tags) {
            // VisibilityParameterAnnotationTag
            if (tag instanceof VisibilityAnnotationTag) {
                ArrayList<AnnotationTag> annotationTags = ((VisibilityAnnotationTag) tag).getAnnotations();
                for (AnnotationTag annotationTag: annotationTags) {
                    String type = annotationTag.getType();
                    if (Objects.equals(type, "Lkotlin/Deprecated;")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public ClassType getClassType() {
        return classType;
    }

    public String getSuperClass() {
        return superClass;
    }

    @Override
    public String toString() {
        return name;
    }
}
