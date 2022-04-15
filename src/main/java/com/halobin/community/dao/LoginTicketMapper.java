package com.halobin.community.dao;

import com.halobin.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginTicketMapper {

    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket findLoginTicketByTicket(@Param("ticket") String ticket);

    int updateStatusByTicket(@Param("ticket") String ticket, @Param("status") int status);
}
