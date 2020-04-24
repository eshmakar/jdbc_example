import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public class RowSetLesson {
    // Задаем параметры подключения как статические переменные
    static String url =  "jdbc:mysql://localhost:3306/first_lesson?useSSL=false&serverTimezone=UTC";
    static String userName = "root";
    static String password = "root";
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // Получаем результрующий набор от метода getResSet()
        ResultSet resultSet = getResSet();
//        while (resultSet.next())
//            System.out.println(resultSet.getString("name"));

        // Создаем новый объект кэшированного набора строк
        CachedRowSet rowSet = (CachedRowSet) resultSet;
        // Выполняем запрос к набору строк или к базе данных
        rowSet.setCommand("SELECT * FROM books WHERE price > ?");
        rowSet.setDouble(1, 30);

        // Задаем параметры подключения к БД
        rowSet.setUrl(url);
        rowSet.setUsername(userName);
        rowSet.setPassword(password);
        rowSet.execute();

        // Вносим изменения в набор строк и базу данных
        rowSet.absolute(2);
        rowSet.deleteRow();
        rowSet.beforeFirst();
        Connection connection = DriverManager.getConnection(url, userName, password);
        connection.setAutoCommit(false);
        rowSet.acceptChanges(connection);

        // Выводим набор строк
        while (rowSet.next()) {
            String name = rowSet.getString("name");
            double price = rowSet.getDouble(3);
            System.out.println(name + " " + price);
        }
    }

    // Метод для получения результирующего набора
    static ResultSet getResSet() throws ClassNotFoundException, SQLException {
        // Регистрируем драйвер и выполняем подключение к БД
        Class.forName("com.mysql.cj.jdbc.Driver");
        try(Connection conn = DriverManager.getConnection(url, userName, password);
            Statement stat = conn.createStatement()) {
            // Получаем результирующий набор
            ResultSet rs = stat.executeQuery("SELECT * FROM books");
            // Получаем объект типа RowSetFactory
            RowSetFactory factory = RowSetProvider.newFactory();
            // Получаем объект кэшированного набора строк
            CachedRowSet crs = factory.createCachedRowSet();
            // Наполняем набор данными
            crs.populate(rs);
            return crs;
        }
    }
}
