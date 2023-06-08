package edu.sjtu.gosec.apkdiff.profile;

import edu.sjtu.gosec.apkdiff.Utils;
import soot.*;
import soot.jimple.StringConstant;
import soot.jimple.internal.ImmediateBox;

import java.util.List;
import java.util.ArrayList;

public class MethodProfile {
    private final SootMethod sootMethod;
    private final String returnType;
    private final List<String> parameterTypes;
    private final List<String> constantStrings;
    private String hash;

    public MethodProfile(SootMethod sootMethod) {
        this.sootMethod = sootMethod;

        this.returnType = this.sootMethod.getReturnType().toString();
        this.parameterTypes = new ArrayList<>();

        List<Type> pts = sootMethod.getParameterTypes();
        for (Type parameterType : pts) {
            this.parameterTypes.add(Utils.getRawType(parameterType.toString()));
        }

        this.constantStrings = new ArrayList<>();
        setHash();
        //storeConstantStrings();
    }

    private void setHash() {
        //String identifier = sootMethod.getName();
        String identifier = "X";

        int modifier = sootMethod.getModifiers();
        List<String> parameters = new ArrayList<>();
        for (String type : parameterTypes) {
            parameters.add(Utils.getHashType(type));
        }
        this.hash = String.format("%s_%d_%s_%s",
                identifier, modifier, Utils.getHashType(returnType), String.join(",", parameters));
    }

    private void storeConstantStrings() {
        if (sootMethod.hasActiveBody()) {
            Body body = sootMethod.getActiveBody();

            for (Unit unit : body.getUnits()) {
                for (ValueBox b : unit.getUseBoxes()) {
                    if (b instanceof ImmediateBox) {
                        Value value = b.getValue();
                        if (value instanceof StringConstant) {
                            String cs = value.toString();
                            constantStrings.add(cs.substring(1, cs.length() - 1));
                        }
                    }
                }
            }
        }
    }

    public List<String> getConstantStrings() {
        return constantStrings;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getHash() {
        return hash;
    }
}
