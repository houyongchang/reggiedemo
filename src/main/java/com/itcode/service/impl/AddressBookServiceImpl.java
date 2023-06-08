package com.itcode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itcode.mapper.AddressBookMapper;
import com.itcode.pojo.AddressBook;
import com.itcode.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl  extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
