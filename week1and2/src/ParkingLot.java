import java.util.*;
import java.time.*;
enum SpotStatus {
    EMPTY, OCCUPIED, DELETED
}

class ParkingSpot {
    String licensePlate;
    SpotStatus status;
    LocalDateTime entryTime;
    public ParkingSpot() {
        this.status = SpotStatus.EMPTY;
        this.licensePlate = null;
        this.entryTime = null;
    }
}

public class ParkingLot {
    private final int capacity = 500;
    private ParkingSpot[] spots = new ParkingSpot[capacity];
    private int totalProbes = 0;
    private int vehiclesParked = 0;
    public ParkingLot() {
        for (int i = 0; i < capacity; i++) {
            spots[i] = new ParkingSpot();
        }
    }
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }
    public void parkVehicle(String licensePlate) {
        int preferred = hash(licensePlate);
        int probes = 0;
        for (int i = 0; i < capacity; i++) {
            int idx = (preferred + i) % capacity;
            probes++;
            if (spots[idx].status == SpotStatus.EMPTY || spots[idx].status == SpotStatus.DELETED) {
                spots[idx].licensePlate = licensePlate;
                spots[idx].status = SpotStatus.OCCUPIED;
                spots[idx].entryTime = LocalDateTime.now();
                vehiclesParked++;
                totalProbes += probes;
                System.out.println("Vehicle " + licensePlate + " assigned spot " + idx + " (" + (probes - 1) + " probes)");
                return;
            }
        }
        System.out.println("Parking lot full! Cannot park vehicle " + licensePlate);
    }
    public void exitVehicle(String licensePlate) {
        int preferred = hash(licensePlate);
        for (int i = 0; i < capacity; i++) {
            int idx = (preferred + i) % capacity;
            if (spots[idx].status == SpotStatus.OCCUPIED && licensePlate.equals(spots[idx].licensePlate)) {
                LocalDateTime entry = spots[idx].entryTime;
                LocalDateTime exit = LocalDateTime.now();
                Duration duration = Duration.between(entry, exit);
                double hours = duration.toMinutes() / 60.0;
                double fee = hours * 5.0; // $5 per hour
                spots[idx].status = SpotStatus.DELETED;
                spots[idx].licensePlate = null;
                spots[idx].entryTime = null;
                vehiclesParked--;
                System.out.printf("Vehicle %s exited from spot #%d, Duration: %.2f hours, Fee: $%.2f%n",
                        licensePlate, idx, hours, fee);
                return;
            }
        }
        System.out.println("Vehicle " + licensePlate + " not found in parking lot!");
    }

    public void getStatistics() {
        double occupancy = (vehiclesParked * 100.0) / capacity;
        double avgProbes = vehiclesParked == 0 ? 0 : totalProbes * 1.0 / vehiclesParked;
        System.out.printf("Occupancy: %.2f%%, Avg Probes: %.2f%n", occupancy, avgProbes);
    }

    public static void main(String[] args) throws InterruptedException {
        ParkingLot lot = new ParkingLot();
        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");
        Thread.sleep(2000); // simulate parking duration
        lot.exitVehicle("ABC-1234");
        lot.getStatistics();
    }
}