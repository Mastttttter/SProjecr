package org.master.sprojrctbackend.mapper;

import org.apache.ibatis.annotations.*;
import org.master.sprojrctbackend.entity.Account;

@Mapper
public interface UserMapper {
    @Select("select * from db_account where username = #{username} or email = #{username}")
    Account findAccountByName(String username);

    @Insert("insert into db_account(email, username, password) value (#{email},#{username},#{password})")
    int insertAccount(@Param("username") String username,@Param("password") String password,@Param("email") String email);

    @Update("update db_account set password=#{password} where email=#{email}")
    int resetPasswordByEmail(String password, String email);
}
