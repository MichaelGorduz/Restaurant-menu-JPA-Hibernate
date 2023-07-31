package org.example;

import javax.persistence.*;
import java.text.DecimalFormat;

@Entity
@Table(name="menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="myid")
    private int id;
    @Column(nullable = false)
    private String name;
    private double price;
    private int discount;
    private double weight;

    public Menu() {}
    public Menu(String name, double price, int discount, double weight) {
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.weight = weight;
    }
    public String getFormattedPrice() {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(price);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", price = " + getFormattedPrice() + " USD" +
                ", discount = " + discount + "%" +
                ", weight = " + weight + " gram" +
                '}';
    }
}
