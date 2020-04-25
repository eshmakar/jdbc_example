import java.sql.*;

public class Lesson6 {
    // Задаем параметры подключения, но делаем их статическими
    static String url = "jdbc:mysql://localhost:3306/first_lesson?useSSL=false&serverTimezone=UTC";
    static String userName = "root";
    static String password = "root";

    public static void main(String[] args) throws SQLException, InterruptedException {
        // Создаем подключение и получаем объект типа Statement
        try(Connection conn = DriverManager.getConnection(url, userName, password);
            Statement stat = conn.createStatement()) {
            // Отключаем режим автоматической фиксации результатов выполнения команд SQL
            conn.setAutoCommit(false);

            // dirty read
//            conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED); // чтение разрешено
//            stat.executeUpdate("UPDATE Books SET price = 100 WHERE bookId = 1");
//            new OtherTransaction().start();
//            Thread.sleep(2000);
//            conn.rollback();

            // non-repeatable read
//            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); // чтение разрешено
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); // чтение заблокировано (это значение достаточно установить в одной транзакции)
//            ResultSet resultSet = stat.executeQuery("SELECT * FROM Books");
//            while (resultSet.next()) {
//                System.out.println(resultSet.getString("name") + " " + resultSet.getDouble(3));
//            }
//            new OtherTransaction().start();
//            Thread.sleep(2000);
//
//            ResultSet resultSet2 = stat.executeQuery("SELECT * FROM Books");
//            while (resultSet2.next()) {
//                System.out.println(resultSet2.getString("name") + " " + resultSet2.getDouble(3));
//            }

            // phantom read
//            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); // чтение разрешено
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // чтение заблокировано
            ResultSet resultSet = stat.executeQuery("SELECT * FROM Books WHERE bookId > 5");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name") + " " + resultSet.getDouble(3));
            }
            new OtherTransaction().start();
            Thread.sleep(2000);

            ResultSet resultSet2 = stat.executeQuery("SELECT * FROM Books WHERE bookId > 5");
            while (resultSet2.next()) {
                System.out.println(resultSet2.getString("name") + " " + resultSet2.getDouble(3));
            }
        }
    }

    // Класс, в котором реализуются транзакции для чтения
    static class OtherTransaction extends Thread {
        @Override
        public void run() {
            try (Connection conn = DriverManager.getConnection(url, userName, password);
                 Statement stat = conn.createStatement()) {
                // Отключаем режим автоматической фиксации результатов выполнения команд SQL
                conn.setAutoCommit(false);

                // dirty read
//                conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED); // чтение заблокировано
//                ResultSet rs = stat.executeQuery("SELECT * FROM Books");
//                while (rs.next()) {
//                    System.out.println(rs.getString("name") + " " + rs.getDouble(3));
//                }

                // non-repeatable read
//                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); // чтение разрешено
//                conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); // чтение заблокировано
//                stat.executeUpdate("UPDATE Books SET price = price + 20 WHERE name = 'Solomon key'");
//                conn.commit();

                // phantom read
//                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); // чтение разрешено
                conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); // чтение заблокировано
                stat.executeUpdate("INSERT INTO Books (name, price) VALUES ('new Book', 10)");
                conn.commit();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}
