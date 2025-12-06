package com.example.dms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dms.repository.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    /**
     * 获取菜单树形结构
     */
    List<Menu> getMenuTree();
}
