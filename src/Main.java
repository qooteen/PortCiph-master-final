import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final String COMMANDS = "Введите 1, чтобы зашифровать сообщение из файла message.txt, криптограмма запишется в файл cryptogram.txt\n" +
            "Введите 2, чтобы расшифровать криптограмму из файла cryptogram.txt, результат расшифровки будет записан в файл result.txt";
    public static void main(String[] args) {
        try {
            PortEncode portEncode = new PortEncode();
            System.out.println(COMMANDS);
            Scanner scanner = new Scanner(System.in);
            int command = scanner.nextInt();
            if (command != 1 && command != 2) {
                throw new Exception();
            }
            switch (command) {
                case 1: {
                    portEncode.encode("message.txt", "cryptogram.txt", "key.txt");
                    break;
                }
                case 2: {
                    portEncode.decode("cryptogram.txt", "result.txt", "key.txt");
                    break;
                }
            }
            //вывод таблицы Порта в консоль
            Map<Character, List<Character>> map = portEncode.getPortTable();
            for (Map.Entry<Character, List<Character>> pair : map.entrySet()) {
                System.out.println(pair.getKey() + "  --  " + pair.getValue());
            }
        }
        catch (Exception e) {
            System.out.println("Ошибка! Введите число 1 или 2");
        }

    }
}
