package com.apkdiff.profile;

import com.apkdiff.Utils;
import soot.*;
import soot.jimple.StringConstant;
import soot.jimple.internal.ImmediateBox;

import soot.toolkits.graph.CompleteUnitGraph;

import java.util.List;
import java.util.ArrayList;

public class MethodProfile {
    private final SootMethod sootMethod;
    private final String returnType;
    private final List<String> parameterTypes;
    private final List<String> constantStrings;
    private int statementNum = 0;
    private String hash;

    public MethodProfile(SootMethod sootMethod) {
        this.sootMethod = sootMethod;

        this.returnType = this.sootMethod.getReturnType().toString();
        this.parameterTypes = new ArrayList<>();

        //ToDo: need to be more accurate
        try {
            this.statementNum = sootMethod.retrieveActiveBody().getUnits().size();
        } catch (Exception e) {
            this.statementNum = 0;
        }


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

    private static ArrayList<String> getOpSeq(soot.toolkits.graph.Block block) {
        ArrayList<String> opSequence = new ArrayList<>();
        CompleteUnitGraph unitGraph = new CompleteUnitGraph(block.getBody());
        for (Unit unit : unitGraph) {
            String classname = unit.getClass().toString();
            String name = classname.substring(classname.lastIndexOf('.') + 1);
            opSequence.add(name.substring(1, name.length() - 4));
//            System.out.println(unit.getClass());
//            System.out.println(unit);
//            opSequence.add(((AbstractInst) unit).getName());
        }
        return opSequence;
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

    public int getStatementNum() {
        return statementNum;
    }
}
