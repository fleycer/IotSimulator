package net.iosynth;

import com.google.gson.Gson;
import net.iosynth.adapter.Config;
import net.iosynth.adapter.ConfigLocalFile;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@CommandLine.Command(name = "checksum", mixinStandardHelpOptions = true, version = "checksum 4.0",
        description = "Prints the checksum (MD5 by default) of a file to STDOUT.")
public class IoTSimulator implements Callable<Integer> {
    Logger logger = Logger.getLogger(IoTSimulator.class.getName());

    enum Target { file, azureiothub, mqtt, coap, rabbitmq }

    @CommandLine.Option(names = {"-t", "--target"}, description = "Target")
    private Target target = Target.file;

    @CommandLine.Option(names = {"-of", "--outputFile"}, description = "Output File if target is 'file'")
    private String outputFile = null;

    @CommandLine.Option(names = {"-cf", "--configFile"}, description = "Config File")
    private String configFile = null;

    @CommandLine.Option(names = {"-df", "--deviceFile"}, description = "Device File")
    private String deviceFile = null;


    @Override
    public Integer call() throws Exception { // your business logic goes here...

        Config config = new Config(configFile, deviceFile);
        if (target.equals(Target.file)) {
            LocalFile app = new LocalFile(config, new File(outputFile));
        } else if (target.equals(Target.azureiothub)) {
            AzureIoTHub app = new AzureIoTHub(config);
        } else if (target.equals(Target.mqtt)) {
            Mqtt app = new Mqtt(config);
        } else if (target.equals(Target.coap)) {
            CoAP app = new CoAP(config);
        } else if (target.equals(Target.rabbitmq)) {
            RabbitMQ app = new RabbitMQ(config);
        }

        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new IoTSimulator()).execute(args);
        System.exit(exitCode);
    }
}
