package com.clerodri.details.mapper;


import com.clerodri.core.domain.model.UserModel;
import com.clerodri.details.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserModel userModel);


    @Mapping(source = "role", target = "role")
    UserModel toDomain(UserEntity userEntity);



}
