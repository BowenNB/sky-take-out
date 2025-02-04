package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    private LocalDate date;

    /**
     * 根据时间区间统计营业额
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnover(LocalDateTime begin, LocalDateTime end) {
        List<LocalDateTime> dateList = new ArrayList<>();
        dateList.add(begin);

        while(!begin.equals(end)){
             begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
             dateList.add(begin);
        }

        // 存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDateTime dateTime : dateList) {
            //查询date日期对应的营业额数据，营业额是指：状态为"已完成"的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.from(LocalDateTime.MIN));
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.from(LocalDateTime.MAX));

            //select sum(amount) from orders where order_time > beginTime and order_time < endTime and status = 5
            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);


        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    /**
     * 根据时间区间统计用户数量
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDateTime begin, LocalDateTime end) {
        List<LocalDateTime> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>();//新增用户数
        List<Integer> totalUserList = new ArrayList<>();//总用户数

        for (LocalDateTime date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(LocalDate.from(date), LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(LocalDate.from(date), LocalTime.MAX);
            // 新增用户数量select count(id) from user where create_time > ? and create_time < ?
            Integer newUser = getUserCounter(beginTime, endTime);
            // 总用户数量select count(id) from user where create_time < ?
            Integer totalUser = getUserCounter(null, endTime);
        }
        
        



    }

    private Integer getUserCounter(LocalDateTime beginTime, LocalDateTime endTime) {
        Map map = new HashMap<>();
        map.put("begin", beginTime);
        map.put("end", endTime);
        return userMapper.countByMap(map);
    }
}
