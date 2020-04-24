import java.sql.*;

public class Lesson4 {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/first_lesson?useSSL=false&serverTimezone=UTC";
        String userName = "root";
        String password = "root";
        Class.forName("com.mysql.cj.jdbc.Driver"); //новый драйвер, сj, без этого не работает

        try (Connection conn = DriverManager.getConnection(url, userName, password);
             Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {//для получения обновляемого и прокручиваемого результирующего набора
            ResultSet rs = null;
            try {
                rs = statement.executeQuery("Select * from books"); //получаем список всех книг
            /*    while (rs.next()){
                    int id = rs.getInt(1); //получаем номер по списку
                    double price = rs.getDouble(3); //получаем цену

                    if (id == 4){ //проверка условии, 4 строка
                        rs.updateString("name", "Spartacus (discount)"); //обновляем имя книги
                        rs.updateDouble(3, price-10); //обновляем цену
                        rs.updateRow();//чтобы изменения применились!
                    }
                }*/

            if (rs.absolute(2)) //переход на конкретную строку
                System.out.println(rs.getString("name")); //выводит столбец имя (то есть имя книги)

                if (rs.previous()) //переходит на одно строку вверх
                    System.out.println(rs.getString("name"));

                if (rs.last()) //переходит на последнюю строку
                    System.out.println(rs.getString("name"));


                if (rs.relative(-3)) {//на сколько строк переместить курсор назад (показать данные)
                    ResultSetMetaData rsmd = rs.getMetaData(); //выводить все книги, которые идут после курсора
                    while (rs.next()){
                        for (int i = 1; i <= rsmd.getColumnCount() ; ++i) { //считывает имена полей
                            String field = rsmd.getColumnName(i); // получаем имя поля
                            String value = rs.getString(field); //получаем значение этого поля
                            System.out.print(field + ": " +value + " ");
                        }
                        System.out.println();
                    }
                }


            }catch (SQLException e){
                e.printStackTrace();
            }finally {
                if (rs != null){
                    rs.close();
                }else System.out.println("Ошибка чтения данных с БД");
            }
        }
    }
}