package co.com.pragma.consumer.mapper;

import co.com.pragma.consumer.UserResponse;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(response.getName() + \" \" + response.getLastName())")
    User toDomain(UserResponse response);

}
