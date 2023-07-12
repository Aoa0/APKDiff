package edu.sjtu.gosec.apkdiff.profile;

import edu.sjtu.gosec.apkdiff.Utils;
import org.checkerframework.checker.units.qual.A;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.MultiDexContainer;
import org.jf.dexlib2.iface.instruction.Instruction;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DexProfile {
    private final String apkPath;
    private Map<String, ArrayList<String>> methodInstructionMap;

    public DexProfile(String apkPath) {
        this.apkPath = apkPath;
        load();
    }

    private void load() {
        Set<ClassDef> allClasses = new HashSet<>();
        methodInstructionMap = new HashMap<>();
        try {
            MultiDexContainer<? extends DexBackedDexFile> dexContainer = DexFileFactory.loadDexContainer(new File(this.apkPath), Opcodes.getDefault());
            for (String dexName: dexContainer.getDexEntryNames()) {
//                System.out.println(dexName);
                allClasses.addAll(dexContainer.getEntry(dexName).getDexFile().getClasses());
            }
//            System.out.println(allClasses.size());
            for (ClassDef cd: allClasses) {
                String className = Utils.dexClassType2Name(cd.getType());
                for (Method m: cd.getMethods()) {
                    String methodName = className + "." + m.getName();

                    ArrayList<String> arrayList = new ArrayList<>();
                    MethodImplementation methodImplementation = m.getImplementation();
                    if (methodImplementation != null) {
                        for (Instruction instruction: Objects.requireNonNull(m.getImplementation()).getInstructions()) {
                            arrayList.add(instruction.getOpcode().name);
                        }
                    }
                    methodInstructionMap.put(methodName, arrayList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, ArrayList<String>> getMethodInstructionMap() {
        return methodInstructionMap;
    }
}
