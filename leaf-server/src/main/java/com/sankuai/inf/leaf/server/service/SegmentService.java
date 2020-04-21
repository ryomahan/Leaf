package com.sankuai.inf.leaf.server.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.PropertyFactory;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.ZeroIDGen;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.impl.IDAllocDaoImpl;
import com.sankuai.inf.leaf.server.Constants;
import com.sankuai.inf.leaf.server.exception.InitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Properties;

@Service("SegmentService")
public class SegmentService {
    private Logger logger = LoggerFactory.getLogger(SegmentService.class);

    private IDGen idGen;
    private DruidDataSource dataSource;

    public static String getEnv(String key, String defaultValue) {
        // 改用环境变量进行系统设置
        String val = System.getenv(key);
        return val == null ? defaultValue : val;
    }

    public SegmentService() throws SQLException, InitException {
        // 默认开启 SEGMENT
        boolean flag = Boolean.parseBoolean(getEnv(Constants.LEAF_SEGMENT_ENABLE, "false"));
        if (flag) {
            // 设置数据库信息
            dataSource = new DruidDataSource();
            String mysqlHost = getEnv(Constants.LEAF_MYSQL_HOST, "127.0.0.1");
            String mysqlPort = getEnv(Constants.LEAF_MYSQL_PORT, "3306");
            String mysqlDatabase = getEnv(Constants.LEAF_MYSQL_DATABASE, "isms");
            String url = "jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlDatabase;
            dataSource.setUrl(url);
            dataSource.setUsername(getEnv(Constants.LEAF_MYSQL_USERNAME, "root"));
            dataSource.setPassword(getEnv(Constants.LEAF_MYSQL_PASSWORD, "ryomahan1996"));
            dataSource.init();

            // Config Dao
            IDAllocDao dao = new IDAllocDaoImpl(dataSource);

            // Config ID Gen
            idGen = new SegmentIDGenImpl();
            ((SegmentIDGenImpl) idGen).setDao(dao);
            if (idGen.init()) {
                logger.info("Segment Service Init Successfully");
            } else {
                throw new InitException("Segment Service Init Fail");
            }
        } else {
            idGen = new ZeroIDGen();
            logger.info("Zero ID Gen Service Init Successfully");
        }
    }

    public Result getId(String key) {
        return idGen.get(key);
    }

    public SegmentIDGenImpl getIdGen() {
        if (idGen instanceof SegmentIDGenImpl) {
            return (SegmentIDGenImpl) idGen;
        }
        return null;
    }
}
