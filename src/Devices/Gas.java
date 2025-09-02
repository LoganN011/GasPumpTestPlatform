package Devices;

public class Gas {
    double price;
    int number;
    String name;
    double availableQuantity;

    public Gas(String name, int number, double price, double availableQuantity) {
        this.price = price;
        this.number = number;
        this.name = name;
        this.availableQuantity = availableQuantity;
    }

    public double sellGas(double gallons) {
        availableQuantity -= gallons;
        return availableQuantity;
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

    public int getType() {
        return number;
    }

    public String toString() {
        return name + " " + number + " $" + displayPrice(price);
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
