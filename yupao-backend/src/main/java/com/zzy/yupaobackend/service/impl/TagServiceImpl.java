package com.zzy.yupaobackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zzy.yupaobackend.mapper.TagMapper;
import com.zzy.yupaobackend.model.domain.Tag;
import com.zzy.yupaobackend.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author zzy
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-02-29 18:47:19
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




