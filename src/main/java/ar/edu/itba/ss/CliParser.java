package ar.edu.itba.ss;

import org.apache.commons.cli.*;

import static java.lang.System.exit;

public class CliParser {

    private double time = 20;
    private double fps = 10;
    private int pedestrians = 200;
    private double speed = 0.8; // m/s

    private static Options createOptions(){
        Options options = new Options();
        options.addOption("h", "help", false, "Shows this screen.");
        options.addOption("t", "time", true, "Total time of the simulation.");
        options.addOption("p", "pedestrians", true, "Number of pedestrians.");
        options.addOption("s", "desiredSpeed", true, "Desired desiredSpeed of the pedestrians.");
        options.addOption("fps", "fps", true, "Time step for the animation.");

        return options;
    }

    public Configuration parseOptions(String[] args){
        Options options = createOptions();
        CommandLineParser parser = new BasicParser();

        try{
            CommandLine cmd = parser.parse(options, args);

            if(cmd.hasOption("h")){
                help(options);
            }

            if (cmd.hasOption("t")) {
                time = Double.parseDouble(cmd.getOptionValue("t"));
            }

            if (cmd.hasOption("fps")) {
                fps = Double.parseDouble(cmd.getOptionValue("fps"));
            }

            if (cmd.hasOption("p")) {
                pedestrians = Integer.parseInt(cmd.getOptionValue("p"));
            }

            if (cmd.hasOption("s")) {
                speed = Double.parseDouble(cmd.getOptionValue("s"));
            }
        }catch (Exception e){
            System.out.println("Argument not recognized.");
            help(options);
        }

        return new Configuration(time, fps, pedestrians, speed);
    }

    private void help(Options options){
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("crowd-simulation", options);
        exit(0);
    }
}
