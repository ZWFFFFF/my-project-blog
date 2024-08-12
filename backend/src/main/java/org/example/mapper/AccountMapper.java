package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.entity.dto.Account;

@Mapper
public interface AccountMapper {
    @Select("select * from account where email = #{email} limit 1")
    Account getAccountByEmail(@Param("email") String email);
    @Select("select * from account where username = #{username} limit 1")
    Account getAccountByName(@Param("username") String username);
    @Insert("insert into account(username, password, email, role) values(#{username}, #{password}, #{email}, #{role})")
    Integer insertAccount(Account account);
    @Update("update account set password = #{password} where email = #{email}")
    Integer updateAccountPasswordByEmail(@Param("email") String email, @Param("password") String password);
}
