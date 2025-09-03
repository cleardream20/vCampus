package com.seu.vcampus.server.daotest;

import com.seu.vcampus.server.dao.UserDao;
import com.seu.vcampus.server.dao.UserDaoImpl;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) {
        UserDao userDao =  new UserDaoImpl();

        try {
            throw  new SQLException();
        } catch (SQLException ex) {
//            Logger.getLogger(UserDaoTest.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
}
