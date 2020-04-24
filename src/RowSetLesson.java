import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public class RowSetLesson {
    static String url = "jdbc:mysql://localhost:3306/first_lesson?useSSL=false&serverTimezone=UTC";
    static String userName = "root";
    static String password = "root";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ResultSet rs = getResultSet(); //вызываем метод
        while (rs.next())//выводится имена книг
            System.out.println(rs.getString("name"));
    }

    static ResultSet getResultSet() throws ClassNotFoundException, SQLException { //возвращает результирующий набор
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(url, userName, password);
             Statement stat = conn.createStatement()) {
            ResultSet rs = stat.executeQuery("SELECT * from books");
            RowSetFactory factory = RowSetProvider.newFactory(); //получаем объект RowSetFactory (чтобы получить данные от кэша), потому что, соединение (resultSet) закрыто
            CachedRowSet crs = factory.createCachedRowSet();//кэшированный набор
            crs.populate(rs);//объект был пуст, с этим мы наполняем
            return crs;
        }
    }
}