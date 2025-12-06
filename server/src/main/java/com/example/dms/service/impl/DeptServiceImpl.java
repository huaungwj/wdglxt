package com.example.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dms.repository.entity.Dept;
import com.example.dms.repository.mapper.DeptMapper;
import com.example.dms.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {
    
    @Override
    public List<Dept> getDeptTree() {
        // 获取所有部门
        List<Dept> allDepts = list();
        
        // 按父ID分组
        Map<Long, List<Dept>> deptMap = allDepts.stream()
            .collect(Collectors.groupingBy(dept -> dept.getParentId() == null ? 0L : dept.getParentId()));
        
        // 构建树形结构
        List<Dept> rootDepts = deptMap.getOrDefault(0L, new ArrayList<>());
        buildDeptTree(rootDepts, deptMap);
        
        return rootDepts;
    }
    
    private void buildDeptTree(List<Dept> depts, Map<Long, List<Dept>> deptMap) {
        for (Dept dept : depts) {
            List<Dept> children = deptMap.get(dept.getId());
            if (children != null && !children.isEmpty()) {
                dept.setChildren(children);
                buildDeptTree(children, deptMap);
            }
        }
    }
}
