public class Launcher {
    public static void main(String[] args) throws Exception {
        runApp("Devices.Hose");
        runApp("Devices.Display");
        runApp("Devices.Harness");
    }

    private static void runApp(String className) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "java","-cp","out/production/GasPumpTestPlatform", className
        );
        pb.inheritIO();


        pb.start();
    }
}
