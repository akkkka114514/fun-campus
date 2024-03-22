package com.akkkka.funcampussecurityapi.api;

import com.akkkka.funcampusmainmodel.entity.User;

import java.util.Map;

/**
 * @author akkkka
 */
public interface ILoginService {
    Map<String,String> passwordLogin(String username, String password);

    User checkPassword(String username, String password);

    void managerLoginVerify(String token , String password);
}
