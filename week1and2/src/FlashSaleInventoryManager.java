import java.util.*;
public class FlashSaleInventoryManager {
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Queue<Integer>> waitingList = new LinkedHashMap<>();

    public void addProduct(String productId, int stock) {
        inventory.put(productId, stock);
        waitingList.put(productId, new LinkedList<>());
    }

    public int checkStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }

    public synchronized void purchaseItem(String productId, int userId) {
        int stock = inventory.getOrDefault(productId, 0);
        if (stock > 0) {
            stock--;
            inventory.put(productId, stock);
            System.out.println("User " + userId +
                    " purchase successful. Remaining stock: " + stock);
        } else {
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            System.out.println("User " + userId +
                    " added to waiting list. Position " + queue.size());
        }
    }

    public void showWaitingList(String productId) {
        System.out.println("Waiting list for " + productId + ": " +
                waitingList.get(productId));
    }

    public static void main(String[] args) {
        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();
        manager.addProduct("IPHONE15_256GB", 5);
        System.out.println("Stock: " + manager.checkStock("IPHONE15_256GB"));
        manager.purchaseItem("IPHONE15_256GB", 101);
        manager.purchaseItem("IPHONE15_256GB", 102);
        manager.purchaseItem("IPHONE15_256GB", 103);
        manager.purchaseItem("IPHONE15_256GB", 104);
        manager.purchaseItem("IPHONE15_256GB", 105);
        manager.purchaseItem("IPHONE15_256GB", 106);
        manager.purchaseItem("IPHONE15_256GB", 107);
        manager.showWaitingList("IPHONE15_256GB");
    }
}