package edu.sjtu.gosec.apkdiff;

import org.apache.commons.cli.*;

public class CLIParser {
    private static Options options;
    private String sourceAPK;
    private String targetAPK;
    private String androidJAR;

    public static class CLIArgs {
        static final String ANDROID_SDK_PATH = "s";
        static final String ANDROID_SDK_PATH_L = "android-sdk";
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

        options.addOption(sdkPath);
    }


    private void die(String message) {
        System.out.println(message);
        System.exit(0);
    }

    private void usage() {
        System.out.println("usage: java -jar apkdiff.jar apk_path apk_path");
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

            String[] inputs = cmd.getArgs();
            if (inputs.length != 2) {
                usage();
            }

            for(String input: inputs) {
                if (!Utils.validateFile(input)) {
                    die("File: " + input + " is not valid");
                }
            }

            sourceAPK = inputs[0];
            targetAPK = inputs[1];

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
}
