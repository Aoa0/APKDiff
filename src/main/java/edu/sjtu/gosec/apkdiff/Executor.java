package edu.sjtu.gosec.apkdiff;

import edu.sjtu.gosec.apkdiff.analysis.ObfuscationAnalysis;
import org.xmlpull.v1.XmlPullParserException;
import soot.Scene;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.io.IOException;

public class Executor {
    private final String source;
    private final String target;
    private final String androidJar;

    public Executor(String source, String target, String androidJar) {
        this.source = source;
        this.target = target;
        this.androidJar = androidJar;
    }

    public void run() {
        ObfuscationAnalysis oa = new ObfuscationAnalysis();

        System.out.println(oa.isIdentifierObfuscated("id"));
        System.out.println(oa.isIdentifierObfuscated("ida"));
        System.out.println(oa.isIdentifierObfuscated("ab0"));
        System.out.println(oa.isIdentifierObfuscated("_abc"));
    }


    public static InfoflowAndroidConfiguration getFlowDroidConfig(String apkPath, String androidJar) {
        final InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
        config.getAnalysisFileConfig().setTargetAPKFile(apkPath);
        config.getAnalysisFileConfig().setAndroidPlatformDir(androidJar);
        config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.CHA);
        config.setMergeDexFiles(true);
        config.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);

        return config;
    }


}
