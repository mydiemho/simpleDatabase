import java.util.Scanner;

public class MyScanner {
    final Scanner scanner;
    final Database database;

    public MyScanner() {
        scanner = new Scanner(System.in);
        database = new SimpleDatabase();
    }

    public boolean processCommand() {
        String command = scanner.next();
        return Commands.valueOf(command).process(scanner, database);
    }

    public enum Commands {
        SET {
            @Override
            boolean process(Scanner scanner, Database database) {
                String key = scanner.next();
                String value = scanner.next();
                database.set(key, value);
                return true;
            }
        },
        GET {
            @Override
            boolean process(Scanner scanner, Database database) {
                String key = scanner.next();
                System.out.println(database.get(key));
                return true;
            }
        },
        UNSET {
            @Override
            boolean process(Scanner scanner, Database database) {
                String key = scanner.next();
                database.unset(key);
                return true;
            }
        },
        NUMEQUALTO {
            @Override
            boolean process(Scanner scanner, Database database) {
                String key = scanner.next();
                System.out.println(database.numEqualTo(key));
                return true;
            }
        },
        END {
            @Override
            boolean process(Scanner scanner, Database database) {
                return false;
            }
        },
        BEGIN {
            @Override
            boolean process(Scanner scanner, Database database) {
                database.beginTransaction();
                return true;
            }
        },
        COMMIT {
            @Override
            boolean process(Scanner scanner, Database database) {
                if (!database.commit()) {
                    System.out.println("NO TRANSACTION");
                }

                return true;
            }
        },
        ROLLBACK {
            @Override
            boolean process(Scanner scanner, Database database) {
                if (!database.rollBack()) {
                    System.out.println("NO TRANSACTION");
                }
                return true;
            }
        };

        abstract boolean process(Scanner scanner, Database database);
    }
}
