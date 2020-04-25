import java.sql.*;

public class Lesson6 {
    static String url = "jdbc:mysql://localhost:3306/first_lesson?useSSL=false&serverTimezone=UTC";
    static String userName = "root";
    static String password = "root";

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
//типы чтения и уровни изоляции в транзакциях
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             Statement stat = connection.createStatement()) {
            connection.setAutoCommit(false); //отключаем автофиксацию
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED); //грязное чтение

            //грязное чтение (при грязном чтении транзакция может работать с данными, которые нет в таблицах)
            stat.executeUpdate("update books set price = 100 where bookId =1"); //обновляем данные в первой строки, не будем сразу вносить изменения в базу, а создадим вторую транзакцию, которая будет работать в другом потоке
            new otherTransaction().start(); //вызываем второй поток, который будет выводит все кники
            Thread.sleep(2000);
            connection.rollback();//делаем откат
        }
    }

    static class otherTransaction extends Thread {
        @Override
        public void run() {
            try (Connection connection = DriverManager.getConnection(url, userName, password);
                 Statement stat = connection.createStatement()) {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED); //грязное чтение
                ResultSet rs = stat.executeQuery("SELECT * from books"); //второй поток при этом получает все книги
                while (rs.next()){//потом выводим имя и цену
                    System.out.println(rs.getString("name") + " " + " price " + rs.getDouble(3));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}