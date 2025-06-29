**Программа представляет собой Spring Boot-приложение для работы с базами данных PostgreSQL, обеспечивающее синхронизацию и анализ данных о сотрудниках и их задачах.**  

### **Архитектура и функциональность:**  
1. **Структура приложения:**  
   - **`controller`** (`ProbsController`, `EmployeeController`, `StatisticsController`):  
     - REST-эндпоинты для управления сотрудниками, статистикой и расчетом вероятностей.  
     - Интеграция с внешней БД (tezis_db) через `JdbcTemplate` и локальной БД через Spring Data JPA.  
   - **`model`** (`Employee`, `Statistics`):  
     - Сущности для хранения данных о сотрудниках (`Employee`) и их статистике задач (`Statistics`).  
   - **`repository`** (`EmployeeRepository`, `StatisticsRepository`):  
     - Интерфейсы Spring Data JPA для CRUD-операций с сущностями.  
   - **`tezis`** (`TezisTaskCount`, `TezisTaskProbability`):  
     - Логика для подсчета задач из внешней БД (tezis_db) и расчета вероятностей их выполнения.  

2. **Конфигурации:**  
   - **`ThesisDatabaseConfig`**: Настройка подключения к внешней БД (tezis_db).  
   - **`TransactionsManagerConfig`**: Управление транзакциями.  
   - **`SecurityConfig`**: Защита API.  
   - **`ProbApplication`**: Главный класс приложения.  

### **Основной алгоритм:**  
- **Синхронизация данных:**  
  - При запросе к `/probability` или `/calculate-probability` программа проверяет наличие сотрудника в локальной БД. Если его нет — создаёт запись, используя данные из Thesis (`sec_user`).  
- **Анализ задач:**  
  - Через `TezisTaskCount` получает кол-во задач сотрудника из внешней БД.  
  - `TezisTaskProbability` рассчитывает вероятность выполнения задач, результат сохраняется в `Statistics`.  
- **Транзакционность:**  
  - Критические операции (сохранение статистики) выполняются в транзакциях (`@Transactional`).  

### **Обработка исключений:**  
- Обработка ошибок (`UserNotFoundException`) с логированием и HTTP-ответами (404, 500).

## Rest Api query

1. http://localhost:8081/api/probs/calculate-probability?user_id=9c41a1e7-bbd3-a748-d7df-b76239a66ef0
2. http://localhost:8081/api/employees/9c41a1e7-bbd3-a748-d7df-b76239a66ef0/statistics  
