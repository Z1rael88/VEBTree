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

        if (size <= 2) {
            summary = null;
            clusters = new ArrayList<>(0);
        }
        else {
            int no_clusters = (int)Math.ceil(Math.sqrt(size));
            summary = new Van_Emde_Boas(no_clusters);

            clusters = new ArrayList<>(no_clusters);

            for (int i = 0; i < no_clusters; i++) {
                clusters.add(new Van_Emde_Boas((int)Math.ceil(Math.sqrt(size))));
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
        int index = (int)Math.ceil(Math.sqrt(universe_size));
        return x * index + y;
    }
}

class Main {

    // Funkcja zwracająca minimalną wartość z drzewa, jeśli istnieje
    public static int FindMinimum(Van_Emde_Boas helper)
    {
        return (helper.minimum == -1 ? -1 : helper.minimum);
    }

    // Funkcja zwracająca maksymalną wartość z drzewa, jeśli istnieje
    public static int FindMaximum(Van_Emde_Boas helper)
    {
        return (helper.maximum == -1 ? -1 : helper.maximum);
    }
    // Funkcja usuwająca klucz z drzewa
    public static void Delete(Van_Emde_Boas helper, int key)
    {
        // Jeśli tylko jeden klucz jest obecny, oznacza to, że jest to klucz, który chcemy usunąć
        if (helper.maximum == helper.minimum) {

            helper.minimum = -1;
            helper.maximum = -1;
        }
        // Bazowy przypadek: Jeśli powyższy warunek nie jest spełniony
        // to oznacza, że drzewo ma więcej niż dwa klucze
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
            // Ponieważ wykonujemy coś podobnego do leniwej propagacji,
            // znajdziemy kolejny większy klucz i przypiszemy go jako minimum
            if (key == helper.minimum) {

                int first_cluster = FindMinimum(helper.summary);

                key = helper.generate_index(first_cluster, FindMinimum(helper.clusters.get(first_cluster)));
                helper.minimum = key;
            }

            // Teraz usuwamy klucz
            Delete(helper.clusters.get(helper.high(key)), helper.low(key));

            // Jeśli minimum w klastrze klucza jest równe -1, to musimy go usunąć z podsumowania,
            // aby całkowicie usunąć klucz
            if (FindMinimum(
                    helper.clusters.get(helper.high(key))) == -1) {

                Delete(helper.summary, helper.high(key));

                // Po powyższym warunku, jeśli klucz jest maksymalny w drzewie
                if (key == helper.maximum) {
                    int max_insummary = FindMaximum(helper.summary);

                    if (max_insummary == -1) {
                        helper.maximum = helper.minimum;
                    }
                    else {
                        // Przypisz globalne maksimum drzewa po usunięciu klucza z zapytaniem
                        helper.maximum = helper.generate_index(max_insummary, FindMaximum(helper.clusters.get(max_insummary)));
                    }
                }
            }
            // Po prostu znajdź nowy maksymalny klucz i
            // ustaw maksimum drzewa na nowe maksimum
            else if (key == helper.maximum) {
                helper.maximum = helper.generate_index(helper.high(key), FindMaximum(helper.clusters.get(helper.high(key))));
            }
        }
    }

    // Funkcja do wstawiania klucza do drzewa
    static void Insert(Van_Emde_Boas helper, int key)
    {

        // Jeśli w drzewie nie ma żadnego klucza,
        // ustaw zarówno minimum, jak i maksimum na klucz
        if (helper.minimum == -1) {
            helper.minimum = key;
            helper.maximum = key;
        }
        else {
            // Jeśli klucz jest mniejszy niż obecne minimum,
            // zamień go z obecnym minimum,
            // ponieważ to minimum jest faktycznie
            // minimum jednego z wewnętrznych klastrów
            if (key < helper.minimum) {
                int temp = helper.minimum;
                helper.minimum = key;
                key = temp;
            }

            // Nie bazowy przypadek...
            if (helper.universe_size > 2) {

                // Jeśli w klastrze nie ma żadnego klucza, to
                // wstaw klucz zarówno do klastra, jak i do podsumowania
                if (FindMinimum(helper.clusters.get(helper.high(key))) == -1) {
                    Insert(helper.summary, helper.high(key));

                    // Ustaw minimum i maksimum klastra na klucz,
                    // ponieważ nie ma innych kluczy,
                    // na tym poziomie zatrzymamy się
                    helper.clusters.get(helper.high(key))
                            .minimum
                            = helper.low(key);
                    helper.clusters.get(helper.high(key))
                            .maximum
                            = helper.low(key);
                }
                else {
                    // Jeśli w drzewie są inne elementy,
                    // rekurencyjnie przejdź głębiej
                    // w strukturę, aby ustawić atrybuty
                    // odpowiednio
                    Insert(helper.clusters.get(helper.high(key)), helper.low(key));
                }
            }
            // Ustaw klucz jako maksymalny, jeśli jest większy niż obecne maksimum
            if (key > helper.maximum) {
                helper.maximum = key;
            }
        }
    }

    // Funkcja sprawdzająca, czy klucz istnieje w drzewie
    public static boolean IsMember(Van_Emde_Boas helper,
                                   int key)
    {
        if (helper.universe_size < key) {
            return false;
        }

        if (helper.minimum == key || helper.maximum == key) {
            return true;
        }
        else {
            // Jeśli po spełnieniu powyższego warunku,
            // a rozmiar drzewa wynosi 2, to obecny klucz
            // musi być maksimum lub minimum drzewa
            if (helper.universe_size == 2) {
                return false;
            }
            else {
                return IsMember(helper.clusters.get(helper.high(key)), helper.low(key));
            }
        }
    }

    // Funkcja do znajdowania następnika danego klucza
    public static int FindSuccessor(Van_Emde_Boas helper,
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
        // Jeśli klucz jest mniejszy niż minimum, zwróć minimum,
        // ponieważ będzie to następca klucza
        else if (helper.minimum != -1 && key < helper.minimum) {
            return helper.minimum;
        }
        else {

            // Znajdź następnika wewnątrz klastra klucza
            // Najpierw znajdź maksimum w klastrze
            int max_incluster = FindMaximum(helper.clusters.get(helper.high(key)));
            int offset;
            int succ_cluster;

            // Jeśli w klastrze jest jakiś klucz (maximum != -1), znajdź następnika wewnątrz klastra
            if (max_incluster != -1 && helper.low(key) < max_incluster)
            {
                offset = FindSuccessor(helper.clusters.get(helper.high(key)), helper.low(key));

                return helper.generate_index(helper.high(key), offset);
            }
            else {
                succ_cluster = FindSuccessor(helper.summary, helper.high(key));
                if (succ_cluster == -1) {
                    return -1;
                }
                // Znajdź minimum w następnym klastrze, które będzie następnikiem klucza
                else {
                    offset = FindMinimum(helper.clusters.get(succ_cluster));

                    return helper.generate_index(succ_cluster, offset);
                }
            }
        }
    }

    // Funkcja do znajdowania poprzednika danego klucza
    public static int FindPredecessor(Van_Emde_Boas helper, int key)
    {
        if (helper.universe_size == 2) {
            if (key == 1 && helper.minimum == 0) {
                return 0;
            }
            else {
                return -1;
            }
        }
        // Jeśli klucz jest większy niż maksimum drzewa, zwróć klucz, ponieważ będzie to poprzednik klucza
        else if (helper.maximum != -1 && key > helper.maximum) {
            return helper.maximum;
        }
        else {
            // Znajdź poprzednika wewnątrz klastra klucza
            // Najpierw znajdź minimum w klastrze, aby sprawdzić,
            // czy w klastrze jest obecny jakiś klucz
            int min_incluster =FindMinimum(helper.clusters.get(helper.high(key)));
            int offset;
            int pred_cluster;

            // Jeśli w klastrze jest obecny jakiś klucz, znajdź poprzednika wewnątrz klastra
            if (min_incluster != -1
                    && helper.low(key) > min_incluster) {

                offset = FindPredecessor(helper.clusters.get(helper.high(key)), helper.low(key));

                return helper.generate_index(helper.high(key), offset);
            }
            else {
                // Zwraca indeks poprzedniego klastra z obecnym kluczem
                // oznaczonym przez klucz
                pred_cluster = FindPredecessor(helper.summary, helper.high(key));
                // Jeśli nie ma poprzedniego klastra, to...
                if (pred_cluster == -1) {
                    if (helper.minimum != -1 && key > helper.minimum) {
                        return helper.minimum;
                    }
                    else {
                        return -1;
                    }
                } // W przeciwnym razie znajdź maksimum w poprzednim klastrze
                else {
                    offset = FindMaximum(helper.clusters.get(pred_cluster));

                    return helper.generate_index(pred_cluster, offset);
                }
            }
        }
    }

    // Funkcja wyświetlająca menu operacji na drzewie Van Emde Boasa
    public static void DisplayMenu() {
        System.out.println("Operacje na drzewie Van Emde Boasa:");
        System.out.println("1. Wstaw klucz");
        System.out.println("2. Usuń klucz");
        System.out.println("3. Sprawdź, czy klucz istnieje");
        System.out.println("4. Znajdź poprzednika klucza");
        System.out.println("5. Znajdź następnika klucza");
        System.out.println("6. Wyjdź");
        System.out.println("Wprowadź swój wybór:");
    }

    // Funkcja główna
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        Van_Emde_Boas tree = new Van_Emde_Boas(16);

        int choice;
        do {
            DisplayMenu();
            choice = scanner.nextInt();
            int key;

            switch (choice) {
                case 1:
                    System.out.println("Wprowadź klucz do wstawienia:");
                    key = scanner.nextInt();
                    Insert(tree, key);
                    break;
                case 2:
                    System.out.println("Wprowadź klucz do usunięcia:");
                    key = scanner.nextInt();
                    if (IsMember(tree, key)) {
                        Delete(tree, key);
                        System.out.println("Klucz został pomyślnie usunięty.");
                    } else {
                        System.out.println("Nie znaleziono klucza.");
                    }
                    break;
                case 3:
                    System.out.println("Wprowadź klucz do sprawdzenia:");
                    key = scanner.nextInt();
                    if (IsMember(tree, key)) {
                        System.out.println("Klucz istnieje.");
                    } else {
                        System.out.println("Klucz nie istnieje.");
                    }
                    break;
                case 4:
                    System.out.println("Wprowadź klucz, dla którego chcesz znaleźć poprzednika:");
                    key = scanner.nextInt();
                    int predecessor = FindPredecessor(tree, key);
                    if (predecessor != -1) {
                        System.out.println("Poprzednik klucza " + key + " to " + predecessor);
                    } else {
                        System.out.println("Nie znaleziono poprzednika.");
                    }
                    break;
                case 5:
                    System.out.println("Wprowadź klucz, dla którego chcesz znaleźć następnika:");
                    key = scanner.nextInt();
                    int successor = FindSuccessor(tree, key);
                    if (successor != -1) {
                        System.out.println("Następnik klucza " + key + " to " + successor);
                    } else {
                        System.out.println("Nie znaleziono następnika.");
                    }
                    break;
                case 6:
                    System.out.println("Zamykanie...");
                    break;

                default:
                    System.out.println("Nieprawidłowy wybór. Spróbuj ponownie.");
            }
        } while (choice != 6);

        scanner.close();
    }
}
