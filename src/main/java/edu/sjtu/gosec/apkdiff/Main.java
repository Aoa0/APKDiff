package edu.sjtu.gosec.apkdiff;

public class Main {
    public static void main(String[] args) {
        CLIParser cliParser = new CLIParser(args);
        System.out.println(cliParser.getSourceAPK() + " " + cliParser.getTargetAPK() + " " + cliParser.getAndroidJAR());
        Executor executor = new Executor(cliParser.getSourceAPK(), cliParser.getTargetAPK(), cliParser.getAndroidJAR());
        executor.run();
    }
}