package Sockets;

public class Port {

    /**
     * A method to determine the port number given a device name, for example
     * associates the hose device to port 7150.
     * The numbering scheme is as follows:
     * 714x is for commPort devices
     * 7140 for screen, 7141 for gas_server, 7142 for card_server
     * 715x is for controlPort devices
     * 7150 for hose
     * 716x is for monitor devices
     * 7160 for flow_meter
     * 717x is for statusPort devices
     * 7170 for pump
     *
     * @param deviceName the name of the device (known to the controller)
     * @return the port number associated with the given device, -1 if device name is not recognized
     */
    public static int portLookup(String deviceName) {
        return switch (deviceName.toLowerCase()) {
            //714x for commPort
            case "screen" -> 7140;
            case "gas_server" -> 7141;
            case "card_sever" -> 7142;
            //715x for controlPort
            case "hose" -> 7150;
            case "card" -> 7151;
            //716x for monitorPort
            case "flow_meter" -> 7160;
            //717x for statusPort
            case "pump" -> 7170;
            default -> -1;
        };
    }
}
