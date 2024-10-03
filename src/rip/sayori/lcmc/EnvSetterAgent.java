package rip.sayori.lcmc;

import com.sun.jna.Platform;
import com.sun.jna.platform.linux.LibC;
import com.sun.jna.platform.win32.Kernel32;
import zone.rong.imaginebreaker.ImagineBreaker;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;

public class EnvSetterAgent {
    public static void premain(String agentArgs, Instrumentation inst) throws IOException {
        ImagineBreaker.openBootModules();
        ImagineBreaker.wipeFieldFilters();
        ImagineBreaker.wipeMethodFilters();
        File envfile=new File("config/env");
        try (InputStream inputStream = Files.newInputStream(envfile.toPath())) {
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bReader = new BufferedReader(reader);
            String a;
            while((a = bReader.readLine())!=null){
                String k,v;
                String[] tmp = a.split("=");
                k=tmp[0];
                v=tmp[1];
                if(Platform.isWindows()){
                    if (!Kernel32.INSTANCE.SetEnvironmentVariable(k, v)) {
                        System.err.println("Unable to set the environemnt variable: " + k);
                    }
                }else{
                    if(LibC.INSTANCE.setenv(k,v,1)!=0){
                        System.err.println("Unable to set the environemnt variable: " + k);
                    }
                }
            }
        }
    }
}
