package org.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.sql.SQLOutput;
import java.util.*;

public class App {

    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main( String[] args ) {

        Scanner sc = new Scanner(System.in);
        try {
            // create connection
            emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();
            addRandomMenuItems(5);
            try {
                while (true) {
                    System.out.println("0: show all menu items");
                    System.out.println("1: add menu element");
                    System.out.println("2: search by price");
                    System.out.println("3: show items with discount");
                    System.out.println("4: choose 1kg menu");
                    System.out.println("5: exit");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "0":
                            showAllMenuItems();
                            break;
                        case "1":
                            addMenu(sc);
                            break;
                        case "2":
                            searchByPrice();
                            break;
                        case "3":
                            showDiscount();
                            break;
                        case "4":
                            oneKgMenu();
                            break;
                        case "5":
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
    private static Map<String, Boolean> dishNameMap = new HashMap<>();
    private static Menu generateRandomMenuItem() {
        String[] dishNames = { "Pasta", "Burger", "Salad", "Pizza", "Sushi", "Steak", "Soup", "Fried Rice" };
        Random random = new Random();

        String name;
        do {
            name = dishNames[random.nextInt(dishNames.length)];
        } while (dishNameMap.containsKey(name));

        dishNameMap.put(name, true);

        double price = random.nextDouble() * 30 + 5; // Random price between 5 and 35
        int discount = random.nextInt(10); // Random discount between 0 and 9
        double weight = random.nextDouble() * 400 + 100; // Random weight between 100 and 400

        return new Menu(name, price, discount, weight);
    }

    private static void addRandomMenuItems(int count) {
        em.getTransaction().begin();
        try {
            dishNameMap.clear(); // Clear the map before generating new items
            for (int i = 0; i < count; i++) {
                Menu randomMenu = generateRandomMenuItem();
                em.persist(randomMenu);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }
    private static void showAllMenuItems() {
        String jpql = "SELECT m FROM Menu m";
        TypedQuery<Menu> query = em.createQuery(jpql, Menu.class);

        List<Menu> menuItems = query.getResultList();

        if (menuItems.isEmpty()) {
            System.out.println("No menu items found.");
        } else {
            System.out.println("All menu items:");
            for (Menu menuItem : menuItems) {
                System.out.println(menuItem);
            }
        }
    }

    private static void addMenu(Scanner sc) {
        System.out.print("Enter dish name: ");
        String name = sc.nextLine(); // Read the dish name and consume the newline character

        System.out.print("Enter dish price: ");
        Double price = Double.parseDouble(sc.nextLine()); // Read the price and consume the newline character

        System.out.print("Enter discount %: ");
        int discount = Integer.parseInt(sc.nextLine()); // Read the discount and consume the newline character

        System.out.print("Enter dish weight: ");
        Double weight = Double.parseDouble(sc.nextLine()); // Read the weight and consume the newline character

        em.getTransaction().begin();
        try {
            Menu c = new Menu(name, price, discount, weight);
            em.persist(c);
            em.getTransaction().commit();

            System.out.println(c.getId());
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }
    private static void searchByPrice() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Enter minimum price: ");
            double minPrice = sc.nextDouble();

            System.out.print("Enter maximum price: ");
            double maxPrice = sc.nextDouble();

            String jpql = "SELECT m FROM Menu m WHERE m.price BETWEEN :minPrice AND :maxPrice";
            TypedQuery<Menu> query = em.createQuery(jpql, Menu.class);
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);

            List<Menu> menuItems = query.getResultList();

            if (menuItems.isEmpty()) {
                System.out.println("No menu items found within the specified price range.");
            } else {
                System.out.println("Menu items within the price range:");
                for (Menu menuItem : menuItems) {
                    System.out.println(menuItem);
                }
            }
        } finally {
            sc.nextLine(); // Clear the newline character from the input buffer
        }
    }
    private static void oneKgMenu() {
        double maxWeight = 1000; // Maximum weight in grams (1 kg)
        String jpql = "SELECT m FROM Menu m WHERE m.weight <= :maxWeight";
        TypedQuery<Menu> query = em.createQuery(jpql, Menu.class);
        query.setParameter("maxWeight", maxWeight);

        List<Menu> menuItems = query.getResultList();
        List<Menu> selectedMenuItems = new ArrayList<>();
        double currentWeight = 0;

        for (Menu menuItem : menuItems) {
            if (currentWeight + menuItem.getWeight() <= maxWeight) {
                selectedMenuItems.add(menuItem);
                currentWeight += menuItem.getWeight();
            }
        }

        if (selectedMenuItems.isEmpty()) {
            System.out.println("Cannot select menu items with a total weight of 1 kg or less.");
        } else {
            System.out.println("Menu items with a total weight of 1 kg or less:");
            for (Menu menuItem : selectedMenuItems) {
                System.out.println(menuItem);
            }
            System.out.println(" Total weight is " + currentWeight + " gram");
        }
    }
    private static void showDiscount() {
        String jpql = "SELECT m FROM Menu m WHERE m.discount > 0";
        TypedQuery<Menu> query = em.createQuery(jpql, Menu.class);

        List<Menu> menuItems = query.getResultList();

        if (menuItems.isEmpty()) {
            System.out.println("No menu items with a discount found.");
        } else {
            System.out.println("Menu items with a discount:");
            for (Menu menuItem : menuItems) {
                System.out.println(menuItem);
            }
        }
    }
}
