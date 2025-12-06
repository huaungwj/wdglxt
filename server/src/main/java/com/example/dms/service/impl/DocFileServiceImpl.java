package com.example.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dms.repository.entity.DocFile;
import com.example.dms.repository.mapper.DocFileMapper;
import com.example.dms.service.DocFileService;
import org.springframework.stereotype.Service;

@Service
public class DocFileServiceImpl extends ServiceImpl<DocFileMapper, DocFile> implements DocFileService {
}
