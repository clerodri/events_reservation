package com.clerodri.web.mapper;


import com.clerodri.core.domain.model.UserModel;
import com.clerodri.web.dto.request.RequestUserDTO;
import com.clerodri.web.dto.response.ResponseUserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserWebMapper {

    UserModel toDomain(RequestUserDTO requestUserDTO);

    ResponseUserDTO toWeb(UserModel userModel);
}
