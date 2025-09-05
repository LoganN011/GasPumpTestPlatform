package Devices;

public class Gas {
    double price;
    String name;
    double availableQuantity;

    public Gas(String name, double price, double availableQuantity) {
        this.price = price;
        this.name = name;
        this.availableQuantity = availableQuantity;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public double getAvailableQuantity() {
        return getAvailableQuantity();
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

}
