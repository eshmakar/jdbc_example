import java.sql.*;

//Как занести несколько значении в таблицу за одну транзакцию

public class Lesson5 {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/first_lesson?useSSL=false&serverTimezone=UTC";
        String userName = "root";
        String password = "root";
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(url, userName, password);
             Statement statement = connection.createStatement()) {
            String createTable = "create table fruit (name varchar(15) not null, amount integer, price double not null, primary key(name))";
            String command1 = "insert into fruit (name, amount, price) values ('Apple', 200, 3.50)";
            String command2 = "insert into fruit (name, amount, price) values ('Orange', 50, 5.50)";
            String command3 = "insert into fruit (name, amount, price) values ('Lemon', 30, 5.50)";
            String command4 = "insert into fruit (name, amount, price) values ('Pineapple', 20, 7.50)";

            connection.setAutoCommit(false); //чтобы занести несколько значениее в одну транзакцию, необходимо отключить авто фиксирование

            //выполняем все команды (executeUpdate)
            statement.executeUpdate(createTable);
            Savepoint spt = connection.setSavepoint();//точка сохранения, после выполнения первой команды

            statement.executeUpdate(command1);
            statement.executeUpdate(command2);
            statement.executeUpdate(command3);
            statement.executeUpdate(command4);

//            connection.commit(); //фиксируем все результаты
            connection.rollback(spt); //откатить изменения до точки сохранения spt
            connection.commit();//зафиксировать выполнение одной команды
            connection.releaseSavepoint(spt); //освобождение точки сохранения
        }
    }
}
