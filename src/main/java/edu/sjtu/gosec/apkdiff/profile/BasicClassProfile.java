package edu.sjtu.gosec.apkdiff.profile;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
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
    private final int methodNum;

    public BasicClassProfile(SootClass clz) {
        String superClass1;
        this.clz = clz;

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


    }

    public String getPackageName() {
        return clz.getPackageName();
    }

    public Set<String> getFieldsType() {
        Set<String> fieldsType = new TreeSet<>();
        Chain<SootField> fields = this.clz.getFields();
        for (SootField field : fields) {
            fieldsType.add(Utils.getRawType(field.getType().toString()));
        }
        return fieldsType;
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
