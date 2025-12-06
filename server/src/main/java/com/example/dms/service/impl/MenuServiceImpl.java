package com.example.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dms.repository.entity.Menu;
import com.example.dms.repository.mapper.MenuMapper;
import com.example.dms.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    
    @Override
    public List<Menu> getMenuTree() {
        // 获取所有菜单
        List<Menu> allMenus = list();
        
        // 按父ID分组
        Map<Long, List<Menu>> menuMap = allMenus.stream()
            .collect(Collectors.groupingBy(menu -> menu.getParentId() == null ? 0L : menu.getParentId()));
        
        // 构建树形结构
        List<Menu> rootMenus = menuMap.getOrDefault(0L, new ArrayList<>());
        buildMenuTree(rootMenus, menuMap);
        
        return rootMenus;
    }
    
    private void buildMenuTree(List<Menu> menus, Map<Long, List<Menu>> menuMap) {
        for (Menu menu : menus) {
            List<Menu> children = menuMap.get(menu.getId());
            if (children != null && !children.isEmpty()) {
                menu.setChildren(children);
                buildMenuTree(children, menuMap);
            }
        }
    }
}
