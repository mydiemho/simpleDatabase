class Main {
    public static void main(String[] args) {
        MyScanner scanner = new MyScanner();

        while (true) {
            if (!scanner.processCommand()) {
                return;
            }
        }
    }
}
