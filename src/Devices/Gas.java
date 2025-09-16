package Devices;

import Message.Message;

import java.util.ArrayList;

public class Gas {
    double price;
    String name;

    public Gas(String name, double price) {
        this.price = price;
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name + ": $" + displayPrice(price);
    }

    public static String displayPrice(double price) {
        price = (int) (Math.ceil(price * 100)) / 100.0;
        String[] curPrice = (price + "").split("\\.");
        return switch (curPrice.length) {
            case 1 -> (price + ".00");
            case 2 -> (curPrice[1].length() < 2) ? price + "0" : "" + price;
            default -> "problem in price display";
        };
    }

    public static String displayPrice(String price) {
        return displayPrice(Double.parseDouble(price));
    }

    public Message makeMessage() {
        return new Message("price:" + name + ":" + displayPrice(price));
    }

    public static ArrayList<Gas> parseGasses(Message m) {
        ArrayList<Gas> result = new ArrayList<>();
        for (String curGas : m.toString().split(",")) {
            if (curGas.isEmpty()) continue;
            String[] fields = curGas.split(":");
            result.add(new Gas(fields[1], Double.parseDouble(fields[2])));
        }
        return result;
    }

}
