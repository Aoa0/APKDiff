package edu.sjtu.gosec.apkdiff;

import edu.sjtu.gosec.apkdiff.analysis.DiffAnalysis;
import edu.sjtu.gosec.apkdiff.profile.AppProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;
import soot.PackManager;
import soot.Scene;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.options.Options;

import java.io.*;
import java.util.*;

import static edu.sjtu.gosec.apkdiff.Utils.checkAndMake;

public class Executor {
    private String source;
    private String target;
    private final String androidJar;
    private String dir;
    String sourceName;
    String targetName;
    AppProfile sourceProfile;
    AppProfile targetProfile;
    String outDir = "";
    long time;
    private static final Logger logger = LoggerFactory.getLogger(Executor.class);

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
        logger.info("Diffing: " + sourceName + "  " + targetName);
        DiffAnalysis analysis = new DiffAnalysis(sourceProfile, targetProfile);
        analysis.diff();
        writeMatchesToFile(analysis.getResult(), sourceName+"_vs_"+targetName);
    }

    public void runPairAnalyse() {
        File directory = new File(dir);
        ArrayList<String> apks = new ArrayList<>(Arrays.asList(Objects.requireNonNull(directory.list())));
        logger.info(apks.size() + "apks need to be analysed");
        apks.sort((str1, str2) -> {
            int id1 = Integer.parseInt(str1.split("___")[1].replace(".apk", ""));
            int id2 = Integer.parseInt(str2.split("___")[1].replace(".apk", ""));
            return id1 - id2;
        });
        this.time = System.currentTimeMillis();
        this.targetName = apks.get(0);
        this.outDir = outDir + "/" + targetName.split("___")[0];
        checkAndMake(outDir);
        this.targetName = targetName.split("___")[1].replace(".apk", "");
        this.target = dir + "/" + apks.get(0);
        this.targetProfile = getAppProfile(target, androidJar);
        apks.remove(0);

        for(String apk:apks) {
            this.sourceName = this.targetName;
            this.source = this.target;
            this.sourceProfile = this.targetProfile;
            this.target = dir + "/" + apk;
            this.targetName = apk.split("___")[1].replace(".apk", "");;
            this.targetProfile = getAppProfile(target, androidJar);
            run();
            recordTime();
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

    public AppProfile getAppProfile(String apkPath, String androidJarPath) {
        //ToDo: need timeout check
        setupSoot(apkPath, androidJarPath);
        PackManager.v().runPacks();
        try (ProcessManifest manifest = new ProcessManifest(apkPath)) {
            //app.hierarchyTree.show(app.hierarchyTree.root, 0);
            return new AppProfile(apkPath, manifest);
        } catch (XmlPullParserException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeMatchesToFile(Map<String, String> matches, String tar) {
        TreeMap<String, String> sortedMatches = new TreeMap<>(matches);
        try (PrintWriter out = new PrintWriter(outDir+"/"+tar+".txt")) {
            for (Map.Entry<String, String> entry : sortedMatches.entrySet()) {
                out.write(entry.getKey() + " -> " + entry.getValue() + "\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void recordTime() {
        long now = System.currentTimeMillis();
        try (FileWriter out = new FileWriter(outDir+"/recordTime.txt", true)) {
            out.write(sourceName+" VS "+targetName+": "+(now - this.time)+"\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.time = System.currentTimeMillis();
    }

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }
}
