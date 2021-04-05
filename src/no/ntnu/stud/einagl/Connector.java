package no.ntnu.stud.einagl;

import java.sql.*;

public class Connector {

    public static Connection connect() {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:library.db");
        } catch(ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }

        return connection;
    }

    public static void insertBook(int book_id, int issue_id, String title, String publisher) {
        Connection connection = connect();
        PreparedStatement preparedStatement = null;
        try {
            String sql = "INSERT INTO Books(Issue_ID, Book_ID, Title, Publisher) VALUES(?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, issue_id);
            preparedStatement.setInt(2, book_id);
            preparedStatement.setString(3, title);
            preparedStatement.setString(4, publisher);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void deleteLoanOf(int issue_id) {
        Connection connection = connect();
        PreparedStatement preparedStatement = null;
        try {
            String sql = "DELETE FROM Loans WHERE Issue_ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, issue_id);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void readBorrowersOf(String book) {
        Connection connection = connect();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = "select br.Name as Branch, c.Name as Borrower\n" +
                         "from Branch br, Books b, Clients c, Loans lo\n" +
                         "where (br.id = lo.Branch_ID and b.Issue_ID = lo.Issue_ID and c.id = lo.Client_ID)\n" +
                         "and b.Title = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, book);
            resultSet = preparedStatement.executeQuery();

            System.out.println("Borrowers of : (" +book + ")");

            while(resultSet.next()) {
                System.out.println(
                        resultSet.getString(2) + " Borrowed from " + resultSet.getString(1)
                );
            }

            System.out.println("\n");

        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                System.out.println(e);
            }
        }

    }

    public static void readBookBasedOnDate(String date) {
        Connection connection = connect();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = "select b.Title, c.Name, c.Address\n" +
                         "from Branch br, Books b, Clients c, Loans lo\n" +
                         "where (br.id = lo.Branch_ID and b.Issue_ID = lo.Issue_ID and c.id = lo.Client_ID)\n" +
                         "and lo.DueDate = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, date);
            resultSet = preparedStatement.executeQuery();

            System.out.println("Books borrowed : (" + date + ")");

            while(resultSet.next()) {
                System.out.println(
                        resultSet.getString(1) + " Borrowed by " + resultSet.getString(2) + " from " + resultSet.getString(3)
                );
            }

            System.out.println("\n");

        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                System.out.println(e);
            }
        }

    }

    public static void readLoansPerBranch() {
        Connection connection = connect();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = "select br.Name as branch_name, count(*) as loaded_out\n" +
                         "from Branch br, Books b, Clients c, Loans lo\n" +
                         "where (br.id = lo.Branch_ID and b.Issue_ID = lo.Issue_ID and c.id = lo.Client_ID)\n" +
                         "group by br.Name\n" +
                         "order by loaded_out desc";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            System.out.println("Out-Loaned books :");

            while(resultSet.next()) {
                System.out.println(
                        resultSet.getString(1) + " loaned out " + resultSet.getString(2) + " books"
                );
            }

            System.out.println("\n");

        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                System.out.println(e);
            }
        }

    }

    public static void updateClientAddressOf(String name, String address) {
        Connection connection = connect();
        PreparedStatement preparedStatement = null;
        try {
            String sql = "UPDATE Clients set Address = ? WHERE name = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, address);
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void updateClientNumberOf(String name, int number) {
        Connection connection = connect();
        PreparedStatement preparedStatement = null;
        try {
            String sql = "UPDATE Clients set Number = ? WHERE name = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, number);
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

}
