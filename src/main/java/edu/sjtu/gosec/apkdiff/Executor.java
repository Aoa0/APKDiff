package edu.sjtu.gosec.apkdiff;

import edu.sjtu.gosec.apkdiff.analysis.DiffAnalysis;
import edu.sjtu.gosec.apkdiff.profile.AppProfile;
import org.xmlpull.v1.XmlPullParserException;
import soot.PackManager;
import soot.Scene;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.options.Options;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Executor {
    private String source;
    private String target;
    private final String androidJar;
    private String dir;
    String sourceName;
    String targetName;
    AppProfile sourceProfile;
    AppProfile targetProfile;

    public Executor(String source, String target, String androidJar) {
        this.source = source;
        this.target = target;
        this.sourceName = source.split("/")[0];
        this.targetName = target.split("/")[0];
        this.androidJar = androidJar;
        this.sourceProfile = getAppProfile(source, androidJar);
        this.targetProfile = getAppProfile(target, androidJar);
    }

    public Executor(String dir, String androidJar) {
        this.dir = dir;
        this.androidJar = androidJar;
    }

    public void run() {
        DiffAnalysis analysis = new DiffAnalysis(sourceProfile, targetProfile);
        analysis.diff();
        writeMatchesToFile(analysis.getResult(), sourceName+"_2_"+targetName);
    }

    public void runPairAnalyse() {
        File directory = new File(dir);
        ArrayList<String> apks = new ArrayList<>(Arrays.asList(Objects.requireNonNull(directory.list())));
        Collections.sort(apks);
        this.targetName = apks.get(0);
        this.target = dir + "/" + apks.get(0);
        this.targetProfile = getAppProfile(target, androidJar);
        apks.remove(0);

        for(String apk:apks) {
            this.sourceName = this.targetName;
            this.source = this.target;
            this.sourceProfile = this.targetProfile;
            this.target = dir + "/" + apk;
            this.targetName = apk;
            this.targetProfile = getAppProfile(target, androidJar);
            run();
        }
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

    public AppProfile getAppProfile(String apkPath, String androidJarPath) {
        setupSoot(apkPath, androidJarPath);
        PackManager.v().runPacks();
        try (ProcessManifest manifest = new ProcessManifest(apkPath)) {
            //app.hierarchyTree.show(app.hierarchyTree.root, 0);
            return new AppProfile(Scene.v().getApplicationClasses());
        } catch (XmlPullParserException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeMatchesToFile(Map<String, String> matches, String tar) {
        TreeMap<String, String> sortedMatches = new TreeMap<>(matches);
        try (PrintWriter out = new PrintWriter("results/"+tar+".txt")) {
            for (Map.Entry<String, String> entry : sortedMatches.entrySet()) {
                out.write(entry.getKey() + " -> " + entry.getValue() + "\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
