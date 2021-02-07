package net.iosynth;

import com.google.gson.Gson;
import net.iosynth.adapter.*;
import net.iosynth.device.Device;
import net.iosynth.device.DeviceControl;
import net.iosynth.device.DevicesFromJson;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 *
 */
public class LocalFile {
    protected List<BlockingQueue<Message>> msgQueues;
    protected AdapterLocalFile locaFile;

    protected LocalFile(Config cfg, File outputFile) {

        ConfigLocalFile cfgLocalFile = new ConfigLocalFile();
        cfgLocalFile.setFile(outputFile);
        cfgLocalFile.clients = 1;

        // Setup clients
        msgQueues = new ArrayList<BlockingQueue<Message>>(cfgLocalFile.clients);
        for(int i=0; i<cfgLocalFile.clients; i++){
            msgQueues.add(new LinkedBlockingQueue<Message>());
        }
        for (BlockingQueue<Message> msgQueue : msgQueues) {
            try {
                locaFile = new AdapterLocalFile(cfgLocalFile, msgQueue);
            } catch (URISyntaxException e) {
                return;
            }
        }
        // Create devices
        long seed = cfgLocalFile.seed;
        DevicesFromJson fromJson = new DevicesFromJson();
        List<Device> devs = fromJson.build(cfg.devJson, seed);
        DeviceControl devControl = new DeviceControl(5);
        int k=0;
        int i=0;
        int devsPerClient = (int)Math.ceil((double)devs.size() / (double)cfgLocalFile.clients);
        for (final Device dev : devs) {
            if(k>devsPerClient){
                k=0;
                i++;
            }
            devControl.addDevice(dev, msgQueues.get(i));
        }

        devControl.forever();
    }


}
