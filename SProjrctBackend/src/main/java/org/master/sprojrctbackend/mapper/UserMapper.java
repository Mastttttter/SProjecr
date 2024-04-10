package org.master.sprojrctbackend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.master.sprojrctbackend.entity.Account;

@Mapper
public interface UserMapper {
    @Select("select * from db_account where username = #{username} or email = #{username}")
    Account findAccountByName(String username);

    @Insert("insert into db_account(id, email, username, password) value (#{id},#{email},#{username},#{password})")
    void insertAccount(@Param("id") int id,@Param("email") String email,@Param("username") String username,@Param("password") String password);
}
