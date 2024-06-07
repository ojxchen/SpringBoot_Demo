package com.ojxchen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ojxchen.dto.response.LoginStatisticsResponse;
import com.ojxchen.entity.LoginStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface LoginStatisticsMapper extends BaseMapper<LoginStatistics> {
    @Select("SELECT DATE_SUB(CURDATE(), INTERVAL seq.seq DAY) AS date, IFNULL(COUNT(ls.user_id), 0) AS count\n" +
            "FROM\n" +
            "  (SELECT 0 AS seq UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6) AS seq\n" +
            "LEFT JOIN login_statistics AS ls ON DATE(ls.create_at) = DATE_SUB(CURDATE(), INTERVAL seq.seq DAY) AND ls.user_id = #{userId}\n" +
            "WHERE seq.seq < 7\n" +
            "GROUP BY date\n" +
            "ORDER BY date;")
    List<LoginStatisticsResponse> countLoginStatisticsByUserIdInLast7Days(@Param("userId") String userId);
}
