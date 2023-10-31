package com.apkdiff;

import com.apkdiff.analysis.DiffAnalysis;
import com.apkdiff.profile.AppProfile;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;
import soot.Scene;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.options.Options;

import java.io.*;
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
    String outDir = "";
    long time;
    private static final Logger logger = LoggerFactory.getLogger(Executor.class);

    public Executor(String source, String target, String androidJar) {
        this.source = source;
        this.target = target;
        this.sourceName = source.split("/")[source.split("/").length - 1].replace(".apk", "");
        this.targetName = target.split("/")[source.split("/").length - 1].replace(".apk", "");
        this.androidJar = androidJar;
        this.sourceProfile = getAppProfile(source, androidJar);
        this.targetProfile = getAppProfile(target, androidJar);
    }

    public Executor(String dir, String androidJar) {
        this.dir = dir;
        this.androidJar = androidJar;
    }

    private void run() {
        logger.info("Diffing: " + sourceName + "  " + targetName);
        DiffAnalysis analysis = new DiffAnalysis(sourceProfile, targetProfile);
        analysis.diff();
        logger.info("APKDiff Analysis Done");
        writeMatchesToFile(analysis.getResult(), sourceName+"_vs_"+targetName);
        writePotentialMatchesToFile(analysis.getPotentialMatches(), sourceName+"_vs_"+targetName);
    }

    public void runPairAnalyse() {
        String apkName = this.sourceProfile.getPackageName();
        this.outDir = outDir + "/" + apkName;
        Utils.checkAndMake(outDir);
        run();
    }

    private ArrayList<String> getApkList() {
        File directory = new File(dir);
        ArrayList<String> apks = new ArrayList<>(Arrays.asList(Objects.requireNonNull(directory.list())));
        apks.sort((str1, str2) -> {
            int id1 = Integer.parseInt(str1.split("___")[1].replace(".apk", ""));
            int id2 = Integer.parseInt(str2.split("___")[1].replace(".apk", ""));
            return id1 - id2;
        });
        return apks;
    }

    public void runDirAnalyse() {
        ArrayList<String> apks = getApkList();

        if(config.specialMode) {
            this.sourceName = apks.get(0);
            this.outDir = outDir + "/" + sourceName.split("___")[0];
            Utils.checkAndMake(outDir);
            this.sourceName = sourceName.split("___")[1].replace(".apk", "");
            this.source = dir + "/" + apks.get(0);
            this.sourceProfile = getAppProfile(source, androidJar);
            this.targetName = apks.get(1);
            this.targetName = targetName.split("___")[1].replace(".apk", "");
            this.target = dir + "/" + apks.get(0);
            this.targetProfile = getAppProfile(target, androidJar);
            run();

            this.sourceName = apks.get(apks.size()-2);
            this.sourceName = sourceName.split("___")[1].replace(".apk", "");
            this.source = dir + "/" + apks.get(0);
            this.sourceProfile = getAppProfile(source, androidJar);
            this.targetName = apks.get(apks.size()-1);
            this.targetName = targetName.split("___")[1].replace(".apk", "");
            this.target = dir + "/" + apks.get(0);
            this.targetProfile = getAppProfile(target, androidJar);
            run();

            return;
        }

        logger.info(apks.size() + "apks need to be analysed");
        this.time = System.currentTimeMillis();
        this.targetName = apks.get(0);
        this.outDir = outDir + "/" + targetName.split("___")[0];
        Utils.checkAndMake(outDir);
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
            // recordTime();
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
        try (ProcessManifest manifest = new ProcessManifest(apkPath)) {
            //app.hierarchyTree.show(app.hierarchyTree.root, 0);
            return new AppProfile(apkPath, manifest);
        } catch (XmlPullParserException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeMatchesToFile(Map<String, String> matches, String tar) {
        String filename = outDir+"/"+tar+".Matches";
        TreeMap<String, String> sortedMatches = new TreeMap<>(matches);
        try (PrintWriter out = new PrintWriter(filename)) {
            System.out.println(filename);
            for (Map.Entry<String, String> entry : sortedMatches.entrySet()) {
                out.write(entry.getKey() + " -> " + entry.getValue() + "\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void writePotentialMatchesToFile(Graph<String, DefaultEdge> matches, String tar) {
        String filename = outDir+"/"+tar+".PotentialMatches";
        try (PrintWriter out = new PrintWriter(filename)) {
            System.out.println(tar);
            for(DefaultEdge edge:matches.edgeSet()) {
                String srcName = matches.getEdgeSource(edge);
                String tarName = matches.getEdgeTarget(edge);

                out.write(srcName + " -> " + tarName + "\n");
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
