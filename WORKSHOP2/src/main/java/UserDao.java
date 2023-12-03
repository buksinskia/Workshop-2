import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class UserDao {
    private static final String CREATE_USER_QUERY = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
    private static final String READ_USER_QUERY = "SELECT * FROM users where id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users where id=?";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET username=?,email=?,password=? WHERE id=?";
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";

    public User createUser(User user) {
        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement preStmt = connection.prepareStatement(CREATE_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            preStmt.setString(1, user.getUserName());
            preStmt.setString(2, user.getEmail());
            preStmt.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            preStmt.executeUpdate();
            ResultSet resultSet = preStmt.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User read(int userId) {
        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement preStmt = connection.prepareStatement(READ_USER_QUERY);
            preStmt.setInt(1, userId);
            ResultSet resultSet = preStmt.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void update(User user) {
        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement preStmt = connection.prepareStatement(UPDATE_USER_QUERY);
            preStmt.setString(1, user.getUserName());
            preStmt.setString(2, user.getEmail());
            preStmt.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            preStmt.setInt(4, user.getId());
            preStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public User[] findAll() {
        try (Connection conn = DbUtil.getConnection()) {
            User[] users = new User[0];
            PreparedStatement statement = conn.prepareStatement(FIND_ALL_USERS_QUERY);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                users = addToArray(user, users);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void delete(int userId){
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement prepStm = conn.prepareStatement(DELETE_USER_QUERY);
                prepStm.setInt(1, userId);
                prepStm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }


    private User[] addToArray(User user, User[] users) {

        User[] newArray = Arrays.copyOf(users, users.length + 1);

        newArray[newArray.length - 1] = user;

        return newArray;
    }
}
