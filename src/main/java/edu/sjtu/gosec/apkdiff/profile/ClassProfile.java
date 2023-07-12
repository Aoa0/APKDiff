package edu.sjtu.gosec.apkdiff.profile;

import soot.SootClass;

public class  ClassProfile extends BasicClassProfile{
    private SootClass clz;

    public ClassProfile(SootClass clz, DexProfile dexProfile) {
        super(clz, dexProfile);
    }
}
