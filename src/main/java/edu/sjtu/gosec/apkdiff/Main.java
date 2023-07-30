package edu.sjtu.gosec.apkdiff;

import edu.sjtu.gosec.apkdiff.util.AnalyseOption;

public class Main {
    public static void main(String[] args) {
        CLIParser cliParser = new CLIParser(args);
        if(cliParser.getAnalyseOption()== AnalyseOption.PAIR) {
            Executor executor = new Executor(cliParser.getSourceAPK(), cliParser.getTargetAPK() cliParser.getAndroidJAR());
            executor.setOutDir(cliParser.getTargetDir());
            executor.runPairAnalyse();
        } else if (cliParser.getAnalyseOption()==AnalyseOption.DIRECTORY) {
            Executor executor = new Executor(cliParser.getSourceDir(), cliParser.getAndroidJAR());
            executor.setOutDir(cliParser.getTargetDir());
            executor.runDirAnalyse();
        }
    }
}