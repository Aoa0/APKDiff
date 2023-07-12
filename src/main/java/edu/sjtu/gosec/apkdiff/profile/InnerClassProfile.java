package edu.sjtu.gosec.apkdiff.profile;

import soot.SootClass;

public class InnerClassProfile extends BasicClassProfile{
    public InnerClassProfile(SootClass clz, DexProfile dexProfile) {
        super(clz, dexProfile);
    }
}
