package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.entity.dto.Account;
import org.example.entity.vo.response.AccountVO;

import java.util.List;

@Mapper
public interface AccountMapper {
    @Select("select * from account where email = #{email} limit 1")
    Account getAccountByEmail(@Param("email") String email);
    @Select("select * from account where username = #{username} limit 1")
    Account getAccountByName(@Param("username") String username);
    @Select("select * from account where role like #{role}")
    List<Account> getAccountByRole(@Param("role") String role);
    @Select("select * from account where id = #{id} limit 1")
    Account getAccountById(@Param("id") Integer id);
    @Insert("insert into account(password, email) values(#{password}, #{email})")
    Integer insertAccount(Account account);
    @Update("update account set password = #{password} where email = #{email}")
    Integer updateAccountPasswordByEmail(@Param("email") String email, @Param("password") String password);
    @Update("update account set username = #{username} where username = #{oldUsername}")
    Integer updateUsername(@Param("username") String username, @Param("oldUsername") String oldUsername);
}
