import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final Object mutex = new Object();
    private static final Semaphore semaphore = new Semaphore(4);
    private static final AtomicInteger atomicVariable = new AtomicInteger(0);
    private static int runningThreads = 0;

    public static void main(String[] args) {
        printMenu();
    }

    static void printMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Меню:");
        System.out.println("1. Демонстрація mutex");
        System.out.println("2. Демонстрація semaphore");
        System.out.println("3. Демонстрація atomic");
        System.out.println("4. Демонстрація пула потоків");
        System.out.println("5. Вийти з програми");
        System.out.print("Введіть номер опції: ");

        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1:
                mutexDemo();
                break;
            case 2:
                semaphoreDemo();
                break;
            case 3:
                atomicDemo();
                break;
            case 4:
                poolDemo();
                break;
            case 5:
                System.out.println("Дякую за використання програми. До побачення!");
                return;
            default:
                System.out.println("Невірний вибір. Будь ласка, спробуйте ще раз.");
                printMenu();
        }
    }

    private static void mutexDemo() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть кількість потоків: ");
        var numOfThreads = scanner.nextInt();
        System.out.print("Введіть мінімальний час очікування (мс): ");
        var t1 = scanner.nextInt();
        System.out.print("Введіть максимальний час очікування (мс): ");
        var t2 = scanner.nextInt();

        runningThreads = numOfThreads;

        for (int i = 1; i <= numOfThreads; i++) {
            String threadName = "Потік " + i;
            new Thread(() -> {
                try {
                    synchronized (mutex) {
                        System.out.println(threadName + " захопив м'ютекс.");
                        int sleepTime = new Random().nextInt(t2 - t1) + t1;
                        Thread.sleep(sleepTime);
                    }
                    System.out.println(threadName + " звільнив м'ютекс.");
                    runningThreads--;
                    if (runningThreads == 0) {
                        printMenu();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private static void semaphoreDemo() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть кількість потоків: ");
        int numOfThreads = scanner.nextInt();
        System.out.print("Введіть мінімальний час очікування (мс): ");
        int t1 = scanner.nextInt();
        System.out.print("Введіть максимальний час очікування (мс): ");
        int t2 = scanner.nextInt();

        runningThreads = numOfThreads;

        for (int i = 1; i <= numOfThreads; i++) {
            String threadName = "Потік " + i;
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(threadName + " отримав семафор.");
                    int sleepTime = new Random().nextInt(t2 - t1) + t1;
                    Thread.sleep(sleepTime);
                    semaphore.release();
                    System.out.println(threadName + " звільнив семафор.");
                    runningThreads--;
                    if (runningThreads == 0) {
                        printMenu();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private static void atomicDemo() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Меню Atomic:");
            System.out.println("1. Збільшити змінну");
            System.out.println("2. Зменшити змінну");
            System.out.println("3. Змінити змінну");
            System.out.println("4. Повернутися до попереднього меню");
            System.out.print("Введіть номер опції: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    incrementVariable();
                    break;
                case 2:
                    decrementVariable();
                    break;
                case 3:
                    changeVariable();
                    break;
                case 4:
                    printMenu();
                    return;
                default:
                    System.out.println("Невірний вибір. Будь ласка, спробуйте ще раз.");
            }
        }
    }

    private static void poolDemo() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть кількість потоків в пулі: ");
        int k = scanner.nextInt();
        System.out.print("Введіть кількість потоків, що виконуються: ");
        int p = scanner.nextInt();

        runningThreads = p;

        ExecutorService executor = Executors.newFixedThreadPool(k);

        for (int i = 0; i < p; i++) {
            executor.execute(() -> {
                System.out.println("Виконується потік: " + Thread.currentThread().getName());
                var sleepTime = new Random().nextInt(5000) + 500;
                try {
                    Thread.sleep(sleepTime);
                    runningThreads--;
                    if (runningThreads == 0) {
                        printMenu();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
    }

    private static void incrementVariable() {
        System.out.println("Змінна збільшена. Нове значення: " + atomicVariable.incrementAndGet());
    }

    private static void decrementVariable() {
        System.out.println("Змінна зменшена. Нове значення: " + atomicVariable.decrementAndGet());
    }

    private static void changeVariable() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть нове значення змінної: ");
        int newValue = scanner.nextInt();
        atomicVariable.set(newValue);
        System.out.println("Змінна змінена на " + atomicVariable.get());
    }
}
