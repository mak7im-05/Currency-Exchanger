package com.maxim.currencyexchanger.DAO;

import com.maxim.currencyexchanger.Utils.ConnectDB;
import com.maxim.currencyexchanger.model.CurrencyDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesDAO {
    private final Connection connection;
    private final ConnectDB connectDB;

    public CurrenciesDAO() throws SQLException {
        try {
            connectDB = new ConnectDB();
            connection = connectDB.getConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
    }

    public void closeConnection() {
        connectDB.closeConnection();
    }

    public List<CurrencyDTO> getCurrencies() throws SQLException {
        List<CurrencyDTO> currencies = new ArrayList<>();
        String query = "SELECT * FROM Currencies";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                CurrencyDTO currency = new CurrencyDTO(
                        rs.getInt("id"),
                        rs.getString("fullName"),
                        rs.getString("code"),
                        rs.getString("sign"));
                currencies.add(currency);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return currencies;
    }

    public CurrencyDTO getCurrencyByCode(String code) throws SQLException {
        CurrencyDTO currency = null;
        String query = "SELECT * FROM Currencies WHERE Code = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, code);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                currency = new CurrencyDTO(
                        rs.getInt("id"),
                        rs.getString("fullName"),
                        rs.getString("code"),
                        rs.getString("sign"));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return currency;
    }

    public CurrencyDTO createCurrency(CurrencyDTO currency) throws SQLException {
        String CREATE = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(CREATE);

        currency.setId(-1); // для отслеживания exceptions (если -1 -> тогда валюта уже существует, иначе база данных сломалась)

        statement.setString(1, currency.getCode());
        statement.setString(2, currency.getName());
        statement.setString(3, currency.getSign());
        statement.executeUpdate();

        ResultSet generateId = statement.getGeneratedKeys();
        currency.setId(generateId.getInt(1));

        return currency;
    }
}


//Readmefile
//Дублирование кода вместо использования filter для установки заголовков ответов во всех сервлетах - content-type и character encoding
//LomBOk DONE

//Реализовать connection pool вместо того чтобы открывать по новому соединению на каждую SQL операцию
//Использовать MapStruct или ModelMapper чтобы совершать преобразования между классами-моделями и DTO
// Задеплоить с визуалом

//бизнесс логика в DAO

