package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import io.swagger.annotations.Authorization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     */
    void insertBatch(List<DishFlavor> flavors );
}
