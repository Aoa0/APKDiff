package edu.sjtu.gosec.apkdiff;

import edu.sjtu.gosec.apkdiff.analysis.ObfuscationAnalysis;
import edu.sjtu.gosec.apkdiff.profile.AppProfile;
import edu.sjtu.gosec.apkdiff.util.HierarchyNode;
import edu.sjtu.gosec.apkdiff.util.HierarchyTree;
import org.xmlpull.v1.XmlPullParserException;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider;
import soot.options.Options;

import java.io.IOException;
import java.util.Collections;

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
        setupSoot(source, androidJar);
        PackManager.v().runPacks();
        HierarchyTree tree = new HierarchyTree();
        try (ProcessManifest manifest = new ProcessManifest(source)) {
            AppProfile app = new AppProfile(Scene.v().getApplicationClasses());
            app.PackageSqueezing();
            app.hierarchyTree.show(app.hierarchyTree.root, 0);
        } catch (XmlPullParserException | IOException e) {
            throw new RuntimeException(e);
        }
        tree.show(tree.root, 0);
    }


    protected void setupSoot(String apkPath, String androidJarPath) {
        soot.G.reset();
        Options sootOpt = Options.v();
        sootOpt.set_keep_line_number(false);
        sootOpt.set_prepend_classpath(true);
        sootOpt.set_allow_phantom_refs(true);
        sootOpt.set_src_prec(Options.src_prec_apk);
        sootOpt.set_process_dir(Collections.singletonList(apkPath));
        sootOpt.set_android_jars(androidJarPath);
        sootOpt.set_process_multiple_dex(true);
        sootOpt.set_output_format(Options.output_format_dex);

        Scene.v().loadBasicClasses();
        Scene.v().loadNecessaryClasses();
        Scene.v().loadDynamicClasses();
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
