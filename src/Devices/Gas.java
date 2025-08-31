package Devices;

public class Gas {
    double price;
    int number;
    String name;
    double availableQuantity;

    public Gas(String name, int number, double price, double availableQuantity){
        this.price = (int)(price * 100) / 100.0;
        this.number = number;
        this.name = name;
        this.availableQuantity = availableQuantity;
    }

    public double getPrice() {
        return price;
    }

    public String getName(){
        return name;
    }

    public double getAvailableQuantity() {
        return getAvailableQuantity();
    }

    public String toString() {
        return name + " " + number + " $" + price;
    }
}
