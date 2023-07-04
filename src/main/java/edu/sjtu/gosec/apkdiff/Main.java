package edu.sjtu.gosec.apkdiff;

import edu.sjtu.gosec.apkdiff.util.AnalyseOption;

public class Main {
    public static void main(String[] args) {
        CLIParser cliParser = new CLIParser(args);
        if(cliParser.getAnalyseOption()== AnalyseOption.PAIR) {
            Executor executor = new Executor(cliParser.getTargetAPK(), cliParser.getSourceAPK(), cliParser.getAndroidJAR());
            executor.run();
        } else if (cliParser.getAnalyseOption()==AnalyseOption.DIRECTORY) {
            Executor executor = new Executor(cliParser.getTargetDir(), cliParser.getAndroidJAR());
            executor.runPairAnalyse();
        }
    }
}