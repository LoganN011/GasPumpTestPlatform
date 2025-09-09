import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws Exception {
        runApp("Devices.Hose");
        runApp("Devices.Harness");
        runApp("Devices.Pump");

    }

    private static void runApp(String className) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "java","-cp","out\\production\\GasPumpTestPlatform", className
        );
        pb.inheritIO();



        try {
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
