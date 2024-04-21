import java.util.Scanner;

class VanEmdeBoas {
    int universeSize;
    int minimum;
    int maximum;
    VanEmdeBoas summary;
    VanEmdeBoas[] clusters;

    public VanEmdeBoas(int size) {
        this.universeSize = size;
        this.minimum = -1;
        this.maximum = -1;

        if (size <= 2) {
            this.summary = null;
            this.clusters = new VanEmdeBoas[0];
        } else {
            int noClusters = (size + 1) / 2;

            this.summary = new VanEmdeBoas(noClusters);
            this.clusters = new VanEmdeBoas[noClusters];

            for (int i = 0; i < noClusters; i++) {
                this.clusters[i] = new VanEmdeBoas((size + 1) / 2);
            }
        }
    }

    // Function to return cluster numbers
    // in which key is present
    int high(int x) {
        int div = (this.universeSize + 1) / 2;
        return x / div;
    }

    // Function to return position of x in cluster
    int low(int x) {
        int mod = (this.universeSize + 1) / 2;
        return x % mod;
    }

    // Function to return the index from
    // cluster number and position
    int generateIndex(int x, int y) {
        int ru = (this.universeSize + 1) / 2;
        return x * ru + y;
    }
    void printClusterInfo(int x) {
        System.out.println("Cluster number for key " + x + ": " + high(x));
        System.out.println("Position of key " + x + " in cluster: " + low(x));
        System.out.println("Index for cluster number " + high(x) + " and position " + low(x) + ": " + generateIndex(high(x), low(x)));
    }

    // Function to display menu
    void displayMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Print cluster information for a key");
        System.out.println("2. Exit");
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        VanEmdeBoas akp = new VanEmdeBoas(16);
        int choice;

        do {
            akp.displayMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter the key: ");
                    int key = scanner.nextInt();
                    akp.printClusterInfo(key);
                    break;
                case 2:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 2);

        scanner.close();
    }
}