package ir.aliprogramer.localmessanger2.model;

public class Ingredient {
    public String mac;
    public String name;

    public Ingredient(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.name.toString();
    }
}
