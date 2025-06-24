package com.github.edgeLogger.plc;

import com.github.edgeLogger.config.RegisterConfig;
import com.github.s7connector.api.DaveArea;
import com.github.s7connector.api.S7Connector;
import com.github.s7connector.api.S7Serializer;
import com.github.s7connector.api.factory.S7ConnectorFactory;
import com.github.s7connector.api.factory.S7SerializerFactory;
import com.github.s7connector.impl.serializer.converter.BitConverter;
import com.github.s7connector.impl.serializer.converter.IntegerConverter;
import com.github.s7connector.impl.serializer.converter.RealConverter;
import com.github.s7connector.impl.serializer.converter.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class S7Client {
    public static final Logger LOGGER = LoggerFactory.getLogger("S7ConnectUtil.class");

    //Int类型的封装类 用来转义
    private static final IntegerConverter intCon = new IntegerConverter();
    //String...
    private static final StringConverter strCon = new StringConverter();
    //Boolean...
    private static final BitConverter boolCon = new BitConverter();
    //real实数浮点...
    private static final RealConverter realCon = new RealConverter();

    private static S7Connector s7ConnectorSession;

    /**
     * 初始化PLC连接
     */
    public void buildS7ConnectorSession(String ip, int port, int timeout, int rack, int slot) {
        //默认端口
        s7ConnectorSession = S7ConnectorFactory
                .buildTCPConnector()
                .withHost(ip)
                .withPort(port)
                .withTimeout(timeout) //连接超时时间
                .withRack(rack)  // 架机号
                .withSlot(slot)  // 插槽号
                .build();
        S7Serializer s7Serializer2L = S7SerializerFactory.buildSerializer(s7ConnectorSession);
    }

    public void disconnect() throws IOException {
        s7ConnectorSession.close();
        LOGGER.info("s7Connector会话已安全断开");
    }

    /**
     * 批量读取PLC中的数据
     *
     * @param registers
     * @return 字节数组构成的集合
     */
    public Map<String, DataTimeEntry> readBatch(List<RegisterConfig> registers) {
//        Map<String, DataTimeEntry> registerValue = registers.stream().collect(
//                Collectors.toMap(
//                        RegisterConfig::getDeviceID,
//                        register -> {
//                            byte[] bytes = s7ConnectorSession.read(
//                                    DaveArea.DB,
//                                    register.getDeviceNumber(),
//                                    register.getEndOffset() - register.getStartOffset(),
//                                    register.getStartOffset());
//                            return new DataTimeEntry(bytes, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
//                        },
//                        (existing, replacement) -> replacement,
//                        LinkedHashMap::new
//                )
//        );

//        return registerValue;
        return null;
    }

    /**
     * 读取PLC中的数据，字符串类型
     **/
    public void readPlcDataStr(String ipAddress, int db, int dataLength, int offSet) {
        //第一个参数：DaveArea.DB 表示读取PLC的地址区域为DB
        //第二个参数：DB地址，若plc中是DB1000，则填1000
        //第三个参数：数据长度， <=plc中两个偏移量的间隔，当前偏移量为1000，下一个地址偏移量为1100，则长度可填 0-1000；
        //第三个参数：偏移量
        byte[] plcDataByte = s7ConnectorSession.read(DaveArea.DB, db, dataLength, offSet);
        String plcData = strCon.extract(String.class, plcDataByte, 0, 0);
        System.out.println(plcData);
        try {
            s7ConnectorSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取PLC中的数据，WORD，DWord，INT，DINT，整型
     **/
    public void readPlcDataInt(String ipAddress, int db, int dataLength, int offSet) {
        //第一个参数：DaveArea.DB 表示读取PLC的地址区域为DB
        //第二个参数：DB地址，若plc中是DB1000，则填1000
        //第三个参数：数据长度， <=plc中两个偏移量的间隔，当前偏移量为1000，下一个地址偏移量为1100，则长度可填 0-1000；
        //第四个参数：偏移量
        byte[] plcDataByte = s7ConnectorSession.read(DaveArea.DB, db, dataLength, offSet);
        Integer plcData = intCon.extract(Integer.class, plcDataByte, 0, 0);
        System.out.println(plcData);
        try {
            s7ConnectorSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取PLC中的数据，REAL，心跳检测
     **/
    public boolean readPlcDataReal(int db, int dataLength, int offSet) {
        //第一个参数：DaveArea.DB 表示读取PLC的地址区域为DB
        //第二个参数：DB地址，若plc中是DB1000，则填1000
        //第三个参数：数据长度， <=plc中两个偏移量的间隔，当前偏移量为1000，下一个地址偏移量为1100，则长度可填 0-1000；
        //第四个参数：偏移量
        try {
            byte[] plcDataByte = s7ConnectorSession.read(DaveArea.DB, db, dataLength, offSet);
            realCon.extract(Double.class, plcDataByte, 0, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * byte数组转hex
     */
    public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            strHex = Integer.toHexString(aByte & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }

    /**
     * 向PLC中写数据
     **/
    public void writePlcDataStr(String ipAddress, String hexString, int db, int offSet) {
        //第一个参数：DaveArea.DB 表示读取PLC的地址区域为DB
        //第二个参数：DB地址，若plc中是DB1000，则填1000
        //第三个参数：偏移量
        //第四个参数：写入的数据 二进制数组byte[],由于plc中地址的数据类型是word，所以写入的数据必须是4位的16进制数据
        s7ConnectorSession.write(DaveArea.DB, db, offSet, hexStringToBytes(hexString));

        try {
            s7ConnectorSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能： 十六进制字符串转字节数组
     *
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    public static byte[] hexStringToBytes(String hexString) {
        //判空
        if (hexString == null || hexString.isEmpty()) {
            return null;
        }

        //合法性校验
        if (!hexString.matches("[a-fA-F0-9]*") || hexString.length() % 2 != 0) {
            return null;
        }

        //计算
        int mid = hexString.length() / 2;
        byte[] bytes = new byte[mid];
        for (int i = 0; i < mid; i++) {
            bytes[i] = Integer.valueOf(hexString.substring(i * 2, i * 2 + 2), 16).byteValue();
        }

        return bytes;
    }
}
