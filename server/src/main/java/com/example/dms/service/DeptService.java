package com.example.dms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dms.repository.entity.Dept;

import java.util.List;

public interface DeptService extends IService<Dept> {
    /**
     * 获取部门树形结构
     */
    List<Dept> getDeptTree();
}
