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

    int high(int x) {
        int div = (this.universeSize + 1) / 2;
        return x / div;
    }

    int low(int x) {
        int mod = (this.universeSize + 1) / 2;
        return x % mod;
    }

    int generateIndex(int x, int y) {
        int ru = (this.universeSize + 1) / 2;
        return x * ru + y;
    }

    void insert(int x) {
        if (universeSize == 2) {
            if (x == 0) minimum = maximum = 0;
            else minimum = maximum = 1;
        } else {
            int clusterNumber = high(x);
            int position = low(x);

            if (clusters[clusterNumber].universeSize == 2) {
                if (x == 0) clusters[clusterNumber].minimum = clusters[clusterNumber].maximum = 0;
                else clusters[clusterNumber].minimum = clusters[clusterNumber].maximum = 1;
            } else {
                clusters[clusterNumber].insert(position);
            }

            if (x < minimum || minimum == -1) minimum = x;
            if (x > maximum) maximum = x;
        }
    }

    boolean isEmpty() {
        return minimum == -1;
    }

    boolean find(int x) {
        if (universeSize == 2) {
            return x == minimum || x == maximum;
        } else {
            int clusterNumber = high(x);
            int position = low(x);

            if (clusters[clusterNumber].universeSize == 2) {
                return x == clusters[clusterNumber].minimum || x == clusters[clusterNumber].maximum;
            } else {
                return clusters[clusterNumber].find(position);
            }
        }
    }

    void delete(int x) {
        if (universeSize == 2) {
            if (x == minimum && x == maximum) minimum = maximum = -1;
        } else {
            int clusterNumber = high(x);
            int position = low(x);

            if (clusters[clusterNumber].universeSize == 2) {
                if (x == clusters[clusterNumber].minimum && x == clusters[clusterNumber].maximum)
                    clusters[clusterNumber].minimum = clusters[clusterNumber].maximum = -1;
            } else {
                clusters[clusterNumber].delete(position);

                if (clusters[clusterNumber].minimum == -1) {
                    int summaryMin = summary.minimum;
                    if (summaryMin != -1 && summary.clusters[summaryMin].minimum == -1)
                        minimum = maximum = -1;
                    else
                        minimum = summaryMin * (universeSize / 2) + clusters[summaryMin].minimum;
                }
                if (clusters[clusterNumber].maximum == -1) {
                    // Initialize newMax with the current maximum value
                    int newMax = maximum;
                    for (VanEmdeBoas cluster : clusters) {
                        if (cluster != null && cluster.maximum > newMax)
                            newMax = cluster.maximum;
                    }
                    maximum = newMax;
                }
            }
        }
    }
    int max() {
        return maximum;
    }

    int min() {
        return minimum;
    }

    int successor(int x) {
        if (universeSize == 2) {
            if (x == 0 && maximum == 1) return 1;
            else return -1;
        } else if (minimum != -1 && x < minimum) {
            return minimum;
        } else {
            int clusterNumber = high(x);
            int position = low(x);
            int maxlow = clusters[clusterNumber].max();
            if (maxlow != -1 && position < maxlow) {
                int offset = clusters[clusterNumber].successor(position);
                return clusterNumber * (universeSize / 2) + offset;
            } else {
                int succCluster = summary.successor(clusterNumber);
                if (succCluster == -1) return -1;
                else {
                    int offset = clusters[succCluster].min();
                    return succCluster * (universeSize / 2) + offset;
                }
            }
        }
    }

    int predecessor(int x) {
        if (universeSize == 2) {
            if (x == 1 && minimum == 0) return 0;
            else return -1;
        } else if (maximum != -1 && x > maximum) {
            return maximum;
        } else {
            int clusterNumber = high(x);
            int position = low(x);
            int minlow = clusters[clusterNumber].min();
            if (minlow != -1 && position > minlow) {
                int offset = clusters[clusterNumber].predecessor(position);
                return clusterNumber * (universeSize / 2) + offset;
            } else {
                int predCluster = summary.predecessor(clusterNumber);
                if (predCluster == -1) {
                    if (minimum != -1 && x > minimum) return minimum;
                    else return -1;
                } else {
                    int offset = clusters[predCluster].max();
                    return predCluster * (universeSize / 2) + offset;
                }
            }
        }
    }
    void printAllValues() {
        System.out.println("Printing all values:");
        printAllValuesUtil(this);
        System.out.println();
    }
    void printAllValuesUtil(VanEmdeBoas node) {
        if (node.universeSize == 2) {
            if (node.minimum != -1) System.out.print(node.minimum + " ");
            if (node.maximum != -1 && node.maximum != node.minimum) System.out.print(node.maximum + " ");
        } else {
            for (VanEmdeBoas cluster : node.clusters) {
                if (cluster != null) {
                    // Print the minimum and maximum values of the current cluster
                    if (cluster.minimum != -1) System.out.print(cluster.minimum + " ");
                    if (cluster.maximum != -1 && cluster.maximum != cluster.minimum) System.out.print(cluster.maximum + " ");
                    cluster.printAllValuesUtil(cluster);
                }
            }
        }
    }



    void printClusterInfo(int x) {
        System.out.println("Cluster number for key " + x + ": " + high(x));
        System.out.println("Position of key " + x + " in cluster: " + low(x));
        System.out.println("Index for cluster number " + high(x) + " and position " + low(x) + ": " + generateIndex(high(x), low(x)));
    }

    void displayMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Insert an item");
        System.out.println("2. Check if the set is empty");
        System.out.println("3. Find an item");
        System.out.println("4. Delete an item");
        System.out.println("5. Get the maximum value");
        System.out.println("6. Get the minimum value");
        System.out.println("7. Find the successor of an item");
        System.out.println("8. Find the predecessor of an item");
        System.out.println("9. Print cluster information for a key");
        System.out.println("10. Print all values");
        System.out.println("11. Exit");
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
                    System.out.print("Enter the item to insert: ");
                    int item = scanner.nextInt();
                    akp.insert(item);
                    System.out.println("Item inserted.");
                    break;
                case 2:
                    System.out.println("Is the set empty? " + akp.isEmpty());
                    break;
                case 3:
                    System.out.print("Enter the item to find: ");
                    int findItem = scanner.nextInt();
                    System.out.println("Item found? " + akp.find(findItem));
                    break;
                case 4:
                    System.out.print("Enter the item to delete: ");
                    int deleteItem = scanner.nextInt();
                    akp.delete(deleteItem);
                    System.out.println("Item deleted.");
                    break;
                case 5:
                    System.out.println("Maximum value: " + akp.max());
                    break;
                case 6:
                    System.out.println("Minimum value: " + akp.min());
                    break;
                case 7:
                    System.out.print("Enter the item to find its successor: ");
                    int findSuccessorItem = scanner.nextInt();
                    System.out.println("Successor: " + akp.successor(findSuccessorItem));
                    break;
                case 8:
                    System.out.print("Enter the item to find its predecessor: ");
                    int findPredecessorItem = scanner.nextInt();
                    System.out.println("Predecessor: " + akp.predecessor(findPredecessorItem));
                    break;
                case 9:
                    System.out.print("Enter the key: ");
                    int key = scanner.nextInt();
                    akp.printClusterInfo(key);
                    break;
                case 10:
                    System.out.println("Printing all values...");
                    akp.printAllValues();
                    break;
                case 11:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 11);

        scanner.close();
    }
}
