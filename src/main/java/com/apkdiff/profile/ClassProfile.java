package com.apkdiff.profile;

import soot.SootClass;

public class  ClassProfile extends BasicClassProfile{
    private SootClass clz;

    public ClassProfile(SootClass clz) {
        super(clz);
    }
}
