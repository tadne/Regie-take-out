package com.itheima.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook>
{

}
