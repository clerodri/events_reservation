package com.clerodri.core.domain.usecase.user;

import com.clerodri.core.domain.model.UserModel;

public interface RegisterUserUseCase {


    UserModel save(UserModel userModel);
}
