Задание 1. bencode parser (10 баллов)

Написать bencode парсер, который будет принимать файл в bencode формате, переводить его в json-представление и выводить в консоль или файл.

Запуск программы:

java Main.java <filename> [output-file]

filename - файл с bencode
output-file - файл с читаемым выводом (опциональный аргумент)

Требования к программе:

Программа должна содержать юнит-тесты для парсера и лексера
Ошибки парсера / лексера должны быть понятны пользователю. Пример понятной ошибки: Expected token: CURLY_BRACE, got COMMA at line 5.
json должны содержать отступы при переходе на следующий уровень вложенности

Полезные ссылки

https://maven.apache.org/ - система сборки
https://jdk.java.net/17/ - openjdk 17 (рекомендуется исп. java 17)
https://craftinginterpreters.com/scanning.html - как писать лексер
https://craftinginterpreters.com/parsing-expressions.html - как писать парсер
https://openjdk.java.net/jeps/361 - switch expression
https://openjdk.java.net/jeps/359 - records
https://docs.oracle.com/javase/tutorial/essential/exceptions/index.html - исключения в Java
https://www.youtube.com/watch?v=ygEo5LHHXSI&list=PLlb7e2G7aSpRZSRZxANkvpYC82BXUzCTY - лекции Тагира
https://www.jetbrains.com/idea/download/ - идейка
https://resources.jetbrains.com/storage/products/intellij-idea/docs/IntelliJIDEA_ReferenceCard.pdf - шорткаты в идее
https://plugins.jetbrains.com/plugin/8554-ide-features-trainer - IDE Features Trainer
https://junit.org/junit4/ - фреймворк для написания юнит тестов
