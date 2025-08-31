package Devices;

import Sockets.commPort;

public class GasServer {
    private static Gas[] fuels;

    public static void main(String[] args) {
        commPort self = new commPort("gas_server");

        //For the sake of testing, the fuels are predetermined
    }
}
