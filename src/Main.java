import java.util.*;

class Van_Emde_Boas {

    public int universe_size;
    public int minimum;
    public int maximum;
    public Van_Emde_Boas summary;
    public ArrayList<Van_Emde_Boas> clusters;

    public Van_Emde_Boas(int size)
    {
        universe_size = size;
        minimum = -1;
        maximum = -1;

        // Base case
        if (size <= 2) {
            summary = null;
            clusters = new ArrayList<Van_Emde_Boas>(0);
        }
        else {
            int no_clusters
                    = (int)Math.ceil(Math.sqrt(size));
            summary = new Van_Emde_Boas(no_clusters);

            clusters
                    = new ArrayList<Van_Emde_Boas>(no_clusters);

            for (int i = 0; i < no_clusters; i++) {
                clusters.add(new Van_Emde_Boas(
                        (int)Math.ceil(Math.sqrt(size))));
            }
        }
    }

    // Function to return cluster numbers
    // in which key is present
    public int high(int x)
    {
        int div = (int)Math.ceil(Math.sqrt(universe_size));
        return x / div;
    }
    // Function to return position of x in cluster
    public int low(int x)
    {
        int mod = (int)Math.ceil(Math.sqrt(universe_size));
        return x % mod;
    }

    // Function to return position of x in cluster
    public int generate_index(int x, int y)
    {
        int ru = (int)Math.ceil(Math.sqrt(universe_size));
        return x * ru + y;
    }
}

class Main {

    // Function to return the minimum value
    // from the tree if it exists
    public static int VEB_minimum(Van_Emde_Boas helper)
    {
        return (helper.minimum == -1 ? -1 : helper.minimum);
    }

    // Function to return the maximum value
    // from the tree if it exists
    public static int VEB_maximum(Van_Emde_Boas helper)
    {
        return (helper.maximum == -1 ? -1 : helper.maximum);
    }

    // Function to insert a key in the tree
    static void insert(Van_Emde_Boas helper, int key)
    {

        // If no key is present in the tree
        // then set both minimum and maximum
        // to the key (Read the previous article
        // for more understanding about it)
        if (helper.minimum == -1) {
            helper.minimum = key;
            helper.maximum = key;
        }
        else {
            // If the key is less than the current minimum
            // then swap it with the current minimum
            // because this minimum is actually
            // minimum of one of the internal cluster
            if (key < helper.minimum) {
                int temp = helper.minimum;
                helper.minimum = key;
                key = temp;
            }

            // Not base case then...
            if (helper.universe_size > 2) {

                // If no key is present in the cluster then
                // insert key into both cluster and summary
                if (VEB_minimum(helper.clusters.get(
                        helper.high(key)))
                        == -1) {
                    insert(helper.summary,
                            helper.high(key));

                    // Sets the minimum and maximum of
                    // cluster to the key as no other keys
                    // are present we will stop at this
                    // level
                    helper.clusters.get(helper.high(key))
                            .minimum
                            = helper.low(key);
                    helper.clusters.get(helper.high(key))
                            .maximum
                            = helper.low(key);
                }
                else {
                    // If there are other elements in the
                    // tree then recursively go deeper into
                    // the structure to set attributes
                    // accordingly
                    insert(helper.clusters.get(
                                    helper.high(key)),
                            helper.low(key));
                }
            }
            // Sets the key as maximum it is greater than
            // current maximum
            if (key > helper.maximum) {
                helper.maximum = key;
            }
        }
    }
    // Function that returns true if the
    // key is present in the tree
    public static boolean isMember(Van_Emde_Boas helper,
                                   int key)
    {
        if (helper.universe_size < key) {
            return false;
        }

        if (helper.minimum == key
                || helper.maximum == key) {
            return true;
        }
        else {
            // If after attending above condition,if the
            // size of the tree is 2 then the present key
            // must be maximum or minimum of the tree
            if (helper.universe_size == 2) {
                return false;
            }
            else {
                return isMember(
                        helper.clusters.get(helper.high(key)),
                        helper.low(key));
            }
        }
    }

    // Function to find the successor of the given key
    public static int VEB_successor(Van_Emde_Boas helper,
                                    int key)
    {
        if (helper.universe_size == 2) {
            if (key == 0 && helper.maximum == 1) {
                return 1;
            }
            else {
                return -1;
            }
        }
        // If key is less than minimum then return minimum
        // because it will be successor of the key
        else if (helper.minimum != -1
                && key < helper.minimum) {
            return helper.minimum;
        }
        else {

            // Find successor inside the cluster of the key
            // First find the maximum in the cluster
            int max_incluster = VEB_maximum(
                    helper.clusters.get(helper.high(key)));
            int offset = 0;
            int succ_cluster = 0;

            // If there is any key( maximum!=-1 ) present in
            // the cluster then find the successor inside of
            // the cluster
            if (max_incluster != -1
                    && helper.low(key) < max_incluster) {
                offset = VEB_successor(
                        helper.clusters.get(helper.high(key)),
                        helper.low(key));
                return helper.generate_index(
                        helper.high(key), offset);
            }
            else {
                succ_cluster = VEB_successor(
                        helper.summary, helper.high(key));
                if (succ_cluster == -1) {
                    return -1;
                }
                // Find minimum in successor cluster which
                // will be the successor of the key
                else {
                    offset = VEB_minimum(
                            helper.clusters.get(succ_cluster));
                    return helper.generate_index(
                            succ_cluster, offset);
                }
            }
        }
    }

    // Function to find the predecessor of the given key
    public static int VEB_predecessor(Van_Emde_Boas helper,
                                      int key)
    {
        if (helper.universe_size == 2) {
            if (key == 1 && helper.minimum == 0) {
                return 0;
            }
            else {
                return -1;
            }
        }
        // If the key is greater than maximum of the tree
        // then
        // return key as it will be the predecessor of the
        // key
        else if (helper.maximum != -1
                && key > helper.maximum) {
            return helper.maximum;
        }
        else {
            // Find predecessor in the cluster of the key
            // First find minimum in the key to check
            // whether any key is present in the cluster
            int min_incluster = VEB_minimum(
                    helper.clusters.get(helper.high(key)));
            int offset = 0;
            int pred_cluster = 0;

            // If any key is present in the cluster then
            // find predecessor in the cluster
            if (min_incluster != -1
                    && helper.low(key) > min_incluster) {

                offset = VEB_predecessor(
                        helper.clusters.get(helper.high(key)),
                        helper.low(key));
                return helper.generate_index(
                        helper.high(key), offset);
            }
            else {
                // returns the index of predecessor cluster
                // with any key present
                pred_cluster = VEB_predecessor(
                        helper.summary, helper.high(key));
                // If no predecessor cluster then...
                if (pred_cluster == -1) {
                    if (helper.minimum != -1
                            && key > helper.minimum) {
                        return helper.minimum;
                    }
                    else {
                        return -1;
                    }
                } // Otherwise find maximum in the
                // predecessor cluster
                else {
                    offset = VEB_maximum(
                            helper.clusters.get(pred_cluster));
                    return helper.generate_index(
                            pred_cluster, offset);
                }
            }
        }
    }
    public static void VEB_delete(Van_Emde_Boas helper,
                                  int key)
    {
        // If only one key is present, it means
        // that it is the key we want to delete
        if (helper.maximum == helper.minimum) {

            helper.minimum = -1;
            helper.maximum = -1;
        }
        // Base case: If the above condition is not true
        // i.e. the tree has more than two keys
        // and if its size is two than a tree has exactly
        // two keys.
        else if (helper.universe_size == 2) {

            if (key == 0) {
                helper.minimum = 1;
            }
            else {
                helper.minimum = 0;
            }
            helper.maximum = helper.minimum;
        }
        else {
            // As we are doing something similar to lazy
            // propagation we will basically find next
            // bigger key and assign it as minimum
            if (key == helper.minimum) {

                int first_cluster
                        = VEB_minimum(helper.summary);

                key = helper.generate_index(
                        first_cluster,
                        VEB_minimum(helper.clusters.get(
                                first_cluster)));

                helper.minimum = key;
            }

            // Now we delete the key
            VEB_delete(
                    helper.clusters.get(helper.high(key)),
                    helper.low(key));

            // After deleting the key, rest of the
            // improvements

            // If the minimum in the cluster of the key is
            // -1 then we have to delete it from the summary
            // to eliminate the key completely
            if (VEB_minimum(
                    helper.clusters.get(helper.high(key)))
                    == -1) {

                VEB_delete(helper.summary,
                        helper.high(key));

                // After the above condition, if the key
                // is maximum of the tree then.
                if (key == helper.maximum) {
                    int max_insummary
                            = VEB_maximum(helper.summary);

                    if (max_insummary == -1) {

                        helper.maximum = helper.minimum;
                    }
                    else {
                        // Assign global maximum of the
                        // tree, after deleting our
                        // query-key
                        helper.maximum
                                = helper.generate_index(
                                max_insummary,
                                VEB_maximum(
                                        helper.clusters.get(
                                                max_insummary)));
                    }
                }
            }

            // Simply find the new maximum key and
            // set the maximum of the tree
            // to the new maximum
            else if (key == helper.maximum) {
                helper.maximum = helper.generate_index(
                        helper.high(key),
                        VEB_maximum(helper.clusters.get(
                                helper.high(key))));
            }
        }
    }
    public static void displayMenu() {
        System.out.println("Van Emde Boas Tree Operations:");
        System.out.println("1. Insert a key");
        System.out.println("2. Delete a key");
        System.out.println("3. Check if a key exists");
        System.out.println("4. Find predecessor of a key");
        System.out.println("5. Find successor of a key");
        System.out.println("6. Exit");
        System.out.println("Enter your choice:");
    }

    // Driver code
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        Van_Emde_Boas end = new Van_Emde_Boas(8);

        int choice;
        do {
            displayMenu();
            choice = scanner.nextInt();
            int key;

            switch (choice) {
                case 1:
                    System.out.println("Enter key to insert:");
                    key = scanner.nextInt();
                    insert(end, key);
                    break;
                case 2:
                    System.out.println("Enter key to delete:");
                    key = scanner.nextInt();
                    if (isMember(end, key)) {
                        VEB_delete(end, key);
                        System.out.println("Key deleted successfully.");
                    } else {
                        System.out.println("Key not found.");
                    }
                    break;
                case 3:
                    System.out.println("Enter key to check:");
                    key = scanner.nextInt();
                    if (isMember(end, key)) {
                        System.out.println("Key exists.");
                    } else {
                        System.out.println("Key does not exist.");
                    }
                    break;
                case 4:
                    System.out.println("Enter key to find predecessor:");
                    key = scanner.nextInt();
                    int predecessor = VEB_predecessor(end, key);
                    if (predecessor != -1) {
                        System.out.println("Predecessor of " + key + " is " + predecessor);
                    } else {
                        System.out.println("Predecessor not found.");
                    }
                    break;
                case 5:
                    System.out.println("Enter key to find successor:");
                    key = scanner.nextInt();
                    int successor = VEB_successor(end, key);
                    if (successor != -1) {
                        System.out.println("Successor of " + key + " is " + successor);
                    } else {
                        System.out.println("Successor not found.");
                    }
                    break;
                case 6:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);

        scanner.close();
    }
}