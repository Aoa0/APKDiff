package edu.sjtu.gosec.apkdiff;

import org.apache.commons.cli.*;
import edu.sjtu.gosec.apkdiff.util.AnalyseOption;

public class CLIParser {
    private AnalyseOption analyseOption;
    private static Options options;
    private String sourceAPK;
    private String targetAPK;
    private String androidJAR;
    private String sourceDir;
    private String targetDir = "";

    public static class CLIArgs {
        static final String ANDROID_SDK_PATH = "s";
        static final String ANDROID_SDK_PATH_L = "android-sdk";
        static final String ANALYSE_APK_DIR = "d";
        static final String ANALYSE_APK_PAIR = "p";
        static final String ANALYSE_APK_PAIR_RESULT_DIR = "o";
    }

    public CLIParser(String[] args) {
        setupOptions();
        run(args);
    }

    private void setupOptions() {
        options = new Options();
        Option sdkPath = Option.builder(CLIArgs.ANDROID_SDK_PATH)
                .argName("directory")
                .required(true)
                .longOpt(CLIArgs.ANDROID_SDK_PATH_L)
                .hasArg()
                .desc("path to android sdk")
                .build();
        Option analyseDir = Option.builder(CLIArgs.ANALYSE_APK_DIR)
                .argName("analyse_dir")
                .required(false)
                .hasArg()
                .desc("analyse a directory of app")
                .build();
        Option analysePair = Option.builder(CLIArgs.ANALYSE_APK_PAIR)
                .argName("analyse_pair")
                .required(false)
                .numberOfArgs(2)
                .desc("analyse a pair of apks")
                .build();
        Option resultDir = Option.builder(CLIArgs.ANALYSE_APK_PAIR_RESULT_DIR)
                .argName("analyse_result_dir")
                .required(false)
                .hasArg()
                .desc("where the results are put")
                .build();
        options.addOption(sdkPath);
        options.addOption(analysePair);
        options.addOption(analyseDir);
        options.addOption(resultDir);
    }


    private void die(String message) {
        System.out.println(message);
        System.exit(0);
    }

    private void usage() {
        System.out.println("usage: java -jar apkdiff.jar -s sdk -p apk_path apk_path");
        System.out.println("                             -s sdk -d example [-o resultDir]");
        System.exit(0);
    }


    private void run(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption(CLIArgs.ANDROID_SDK_PATH)) {
                String sdkPath = cmd.getOptionValue(CLIArgs.ANDROID_SDK_PATH);
                if (!Utils.validateDirectory(sdkPath))
                    die("Android SDK path does not exist or it is not a directory: " + sdkPath);
                else
                    androidJAR = sdkPath;
            } else {
                usage();
            }

            if (cmd.hasOption(CLIArgs.ANALYSE_APK_DIR)) {
                String dir = cmd.getOptionValue(CLIArgs.ANALYSE_APK_DIR);
                if (!Utils.validateDirectory(dir))
                    die("dir path does not exist ot it is not a directory: " + dir);
                else
                    sourceDir = dir;
                analyseOption = AnalyseOption.DIRECTORY;
                if (cmd.hasOption(CLIArgs.ANALYSE_APK_PAIR_RESULT_DIR)) {
                    String outdir = cmd.getOptionValue(CLIArgs.ANALYSE_APK_PAIR_RESULT_DIR);
                    if (!Utils.validateDirectory((outdir)))
                        die("outdir path does not exist ot it is not a directory: " + outdir);
                    else
                        targetDir = outdir;
                }
            } else if (cmd.hasOption(CLIArgs.ANALYSE_APK_PAIR)) {
                analyseOption = AnalyseOption.PAIR;
                sourceAPK = cmd.getOptionValues(CLIArgs.ANALYSE_APK_PAIR)[0];
                targetAPK = cmd.getOptionValues(CLIArgs.ANALYSE_APK_PAIR)[1];
            } else {
                usage();
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSourceAPK() {
        return sourceAPK;
    }

    public String getTargetAPK() {
        return targetAPK;
    }

    public String getAndroidJAR() {
        return androidJAR;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public AnalyseOption getAnalyseOption() {
        return analyseOption;
    }

    public String getSourceDir() {
        return sourceDir;
    }
}
