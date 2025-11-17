package com.cypress.utils;

/**
 * Twitter Snowflake ID 生成器
 * 64位ID结构: 1位符号位 + 41位时间戳 + 10位机器ID + 12位序列号
 */
public class SnowflakeIdGenerator {
    // 起始时间戳 (2022-01-01)
    private final static long START_TIMESTAMP = 1640995200000L;
    
    // 各部分位数
    private final static long MACHINE_BIT = 10;   // 机器标识位数
    private final static long SEQUENCE_BIT = 12;  // 序列号位数

    // 各部分最大值
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    
    // 各部分偏移量
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIMESTAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    
    private long machineId;     // 机器标识
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L; // 上一次时间戳
    
    /**
     * 构造函数
     * @param machineId 机器ID (0-1023)
     */
    public SnowflakeIdGenerator(long machineId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId must be between 0 and " + MAX_MACHINE_NUM);
        }
        this.machineId = machineId;
    }
    
    /**
     * 生成下一个ID
     * @return 生成的雪花ID
     */
    public synchronized long nextId() {
        long currTimestamp = getNewTimestamp();
        
        if (currTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }
        
        if (currTimestamp == lastTimestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currTimestamp = getNextTimestamp();
            }
        } else {
            // 不同毫秒内，序列号置为0
            sequence = 0L;
        }
        
        lastTimestamp = currTimestamp;
        
        return (currTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT // 时间戳部分
                | machineId << MACHINE_LEFT                       // 机器标识部分
                | sequence;                                       // 序列号部分
    }
    
    /**
     * 获取下一个毫秒数
     * @return 下一个毫秒数
     */
    private long getNextTimestamp() {
        long timestamp = getNewTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getNewTimestamp();
        }
        return timestamp;
    }
    
    /**
     * 获取当前时间戳
     * @return 当前时间戳
     */
    private long getNewTimestamp() {
        return System.currentTimeMillis();
    }
    
    // 默认实例
    private static SnowflakeIdGenerator instance = new SnowflakeIdGenerator(1);
    
    /**
     * 获取默认实例生成的ID
     * @return 雪花ID
     */
    public static long generateId() {
        return instance.nextId();
    }
}