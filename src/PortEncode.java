import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//Описываем класс Шифрования порта
public class PortEncode {
    private Map<Character, List<Character>> portTable = new LinkedHashMap<>();
    private List<Character> firstPartOfAlphabet; //Объявляем верхнюю строку шифра
    private List<Character> secondPartOfAlphabet; //Объявляем нижнюю строку шифра

    public PortEncode () {
        this.firstPartOfAlphabet = Arrays.asList('а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п'); //Инициализация верхней строки шифра
        this.secondPartOfAlphabet = Arrays.asList('р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я'); //Инициализация нижней строки шифра
        generatePortTable(); //Создаем таблицу
    }
    // Заполняем Таблицу строками
    private void generatePortTable() {
        for (Character i = 'А'; i <= 'Я'; i++) {
            if ((int) i % 2 == 0) {
                portTable.put(i, firstPartOfAlphabet); // Если код буквы кратен двум - ставим в соответствие букве верхнюю строку firstPartOfAlphabet
            } else {
                portTable.put(i, new ArrayList<>(secondPartOfAlphabet)); // Иначе - в нижнюю
                shiftCycleLeft(secondPartOfAlphabet); // Циклический сдвиг влево нижней строки, чтобы на следующем шаге ставить в соответствие сдвинутую нижнюю строку secondPartOfAlphabet
            }
        }
    }
    // Описание циклического сдвига влево
    private void shiftCycleLeft(List<Character> list) {
        Character first = list.get(0);
        for (int i = 0; i < list.size() - 1; i++) {
            swapElements(i, i + 1, list);//меняем местами элементы на итой и и + 1 позиции
        }
        list.set(list.size() - 1, first);
    }
    // Описание смены мест элементов при циклическом сдивге влево
    private void swapElements(int i, int j, List<Character> list) {
        Character temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
    // Чтение из файла, пока в нем имеются элементы
    private String readFromFile(String fileName) {
        try (Scanner textScanner = new Scanner(new FileReader(fileName))){
            String result = "";
            while (textScanner.hasNext()) {
                result += textScanner.nextLine();
            }
            return result;
        }
        catch (IOException e) {
            System.out.println("Ошибка чтения файла " + fileName); // Если файла нет - вывод сообщения об ошибке
        }
        return null;
    }
    // Вывод в файл
    private void writeInFile(String fileName, String message) {
        try (FileWriter fileWriter = new FileWriter(fileName)){
            fileWriter.write(message);
        }
        catch (IOException e) {
            System.out.println("Ошибка записи в файл " + fileName); // Если файла нет - вывод сообщения об ошибке
        }
    }

    public Map<Character, List<Character>> getPortTable() {
        return portTable;
    }
    // Шифрование с помощью таблицы Порта
    public void encode(String messageFile, String cryptogramFile, String keyFile) {
        String message = readFromFile(messageFile); // Чтение файла
        String key = readFromFile(keyFile); // Чтение ключа
        String upperKey = key.toUpperCase().replace('Ё', 'Е'); // Переводим ключ в верхний регистр, Заменяем Ё на Е
        String resultKey = upperKey.replaceAll("[^А-Я]", "");

        String lowerMessage = message.toLowerCase().replace('ё', 'е'); // Переводим сообщение в нижний регистр, Заменяем ё на е
        String messageWithoutNonAlphabetSymbols = lowerMessage.replaceAll("[^а-я]", "");
// Превращаем строку в список букв
        List<Character> messageLetters = messageWithoutNonAlphabetSymbols.chars().mapToObj(e -> (char) e).collect(Collectors.toList());// Превращаем строку в массив букв и каждому символу е сопостовляем char от e и кладем в список
        List<Character> keyLetters = resultKey.chars().mapToObj(e -> (char) e).collect(Collectors.toList()); // Тоже самое делаем с ключом
        addKeysLetters(messageLetters, keyLetters);//добиваем ключ до длины сообщения, если ключ короче сообщения

        // шифрование
        for (int i = 0; i < messageLetters.size(); i++) {
            Character character = messageLetters.get(i); // получаем итую букву сообщения
            if (firstPartOfAlphabet.contains(character) || secondPartOfAlphabet.contains(character)) {//если буква есть в верхней и нижней строчке таблицы Порта делаем следующее:
                Character firstLetter;
                Character secondLetter;
                if ((int) keyLetters.get(i) % 2 == 0) {
                    firstLetter = keyLetters.get(i);// запомним очередную букву ключа для того, чтобы сопоставить ей верхний полуалфавит
                    secondLetter = (char) ((int) keyLetters.get(i) + 1); // запомним очередную букву ключа для того, чтобы сопоставить ей нижний полуалфавит
                } else {
                    secondLetter = keyLetters.get(i); // запомним очередную букву ключа для того, чтобы сопоставить ей нижний полуалфавит
                    firstLetter = (char) ((int) keyLetters.get(i) - 1); // запомним очередную букву ключа для того, чтобы сопоставить ей верхний полуалфавит
                }

                Character resultLetter;
                if (portTable.get(firstLetter).contains(messageLetters.get(i))) {//если в полуалфавите, который соответствует очередной букве есть буква открытого текста
                    int index = portTable.get(firstLetter).indexOf(messageLetters.get(i)); //то запоминаем ее позицию
                    resultLetter = portTable.get(secondLetter).get(index);//и достаем с другого полуалфавита букву с заданной позицией index
                } else {//если в полуалфавите, который соответствует очередной букве нет буквы открытого текста
                    int index = portTable.get(secondLetter).indexOf(messageLetters.get(i));//то запоминаем ее позицию во втором полуалфавите
                    resultLetter = portTable.get(firstLetter).get(index);//и достаем с первого полуалфавита букву с заданной позицией index
                }

                messageLetters.set(i, resultLetter);//формируем получившееся зашифрованное сообщение с каждой итерацией
            }
        }

        //складываем буквы в строку
        String encodeMessage = "";
        for (Character c: messageLetters) {
            encodeMessage += c;
        }
        //записываем криптограмму в файл
        writeInFile(cryptogramFile, encodeMessage);
    }
    //Если ключ недостаточной длины - дописываем его до размера сообщения
    private void addKeysLetters(List<Character> messageLetters, List<Character> keyLetters) {
        if (messageLetters.size() > keyLetters.size()) {
            int length = messageLetters.size() - keyLetters.size();
            for (int i = 0; i < length; i++) {
                keyLetters.add(keyLetters.get(i % keyLetters.size()));
            }
        }
    }
    //Дешифрование ПРОИСХОДИТ ТАКИМ ЖЕ ОБРАЗОМ КАК И ШИФРОВАНИЕ, только вместо открытого текста криптограмма
    public void decode(String cryptogramFile, String messageFile, String keyFile) {
        String cryptogram = readFromFile(cryptogramFile); // Читаем сообщение из файла
        String key = readFromFile(keyFile); // Читаем ключ из файла

        String upperKey = key.toUpperCase().replace('Ё', 'Е'); // Переводим ключ в верхний регистр, Заменяем Ё на Е
        String resultKey = upperKey.replaceAll("[^А-Я]", "");

        String lowerMessage = cryptogram.toLowerCase().replace('ё', 'е'); // Переводим сообщение в нижний регистр, Заменяем ё на е
        String messageWithoutNonAlphabetSymbols = lowerMessage.replaceAll("[^а-я]", "");

        List<Character> cryptogramLetters = messageWithoutNonAlphabetSymbols.chars().mapToObj(e -> (char) e).collect(Collectors.toList());  // Превращаем строку в массим букв и каждому символу е сопостовляем char от e и кладем в список
        List<Character> keyLetters = resultKey.chars().mapToObj(e -> (char) e).collect(Collectors.toList()); // Тоже самое делаем с ключом
        addKeysLetters(cryptogramLetters, keyLetters);

        for (int i = 0; i < cryptogramLetters.size(); i++) {
            Character character = cryptogramLetters.get(i);
            if (firstPartOfAlphabet.contains(character) || secondPartOfAlphabet.contains(character)) {
                Character firstLetter;
                Character secondLetter;
                if ((int) keyLetters.get(i) % 2 == 0) {
                    firstLetter = keyLetters.get(i);
                    secondLetter = (char) ((int) keyLetters.get(i) + 1);
                } else {
                    secondLetter = keyLetters.get(i);
                    firstLetter = (char) ((int) keyLetters.get(i) - 1);
                }
                Character resultLetter;
                if (portTable.get(firstLetter).contains(cryptogramLetters.get(i))) {
                    int index = portTable.get(firstLetter).indexOf(cryptogramLetters.get(i));
                    resultLetter = portTable.get(secondLetter).get(index);
                } else {
                    int index = portTable.get(secondLetter).indexOf(cryptogramLetters.get(i));
                    resultLetter = portTable.get(firstLetter).get(index);
                }

                cryptogramLetters.set(i, resultLetter);
            }
        }
        String decodeMessage = "";
        for (Character c: cryptogramLetters) {
            decodeMessage += c;
        }
        writeInFile(messageFile, decodeMessage);
    }
}
