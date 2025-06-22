### 数据上行
topic:  device/bydpower/up/{device_id}
```json
{
  "msgType": "up",
  "msgId": "{UUID v4}",
  "source": {
    // 边缘电脑的信息
    "ip_addr": "10.79.101.101",
    // 发出消息的IP地址(边缘电脑网卡)
    "source_id": "GW-EM-01"
    // 数据来源(主PLC ID, 后面batch字段有从PLC信息)
  },
  "batch": [
    {
      "timestamp": "1710000000123",
      "device_type": "three_phase_meter",
      "device_id": "EM-2025-001",
      "metrics": {
        "voltage": {
          "data_type": "float",
          "unit": "V",
          "value": {
            "phase_a": 220.5,
            // A相电压 (V)
            "phase_b": 219.8,
            // B相电压 (V)
            "phase_c": 221.2,
            // C相电压 (V)
            "line_ab": 380.4,
            // AB线电压 (V)
            "line_bc": 381.0,
            // BC线电压 (V)
            "line_ca": 379.8
            // CA线电压 (V)
          }
        },
        "current": {
          "data_type": "float",
          "unit": "A",
          "value": {
            "phase_a": 15.3,
            // A相电流 (A)
            "phase_b": 14.9,
            // B相电流 (A)
            "phase_c": 15.1
            // C相电流 (A)
          }
        },
        "power": {
          "active": {
            "data_type": "float",
            "unit": "kW",
            "value": {
              "phase_a": 3.3,
              // A相有功功率 (kW)
              "phase_b": 3.1,
              // B相有功功率 (kW)
              "phase_c": 3.2,
              // C相有功功率 (kW)
              "total": 9.6
              // 总有功功率 (kW)
            }
          },
          "reactive": {
            "data_type": "float",
            "unit": "kvar",
            "value": {
              "phase_a": 1.2,
              // A相无功功率 (kvar)
              "phase_b": 1.1,
              // B相无功功率 (kvar)
              "phase_c": 1.3,
              // C相无功功率 (kvar)
              "total": 3.6
              // 总无功功率 (kvar)
            }
          },
          "apparent": {
            "data_type": "float",
            "unit": "kVA",
            "value": {
              "total": 10.2
              // 总视在功率 (kVA)
            }
          }
        },
        "system": [
          {
            "data_type": "float",
            "unit": "",
            "power_factor": 0.92
            // 总功率因数
          },
          {
            "data_type": "float",
            "unit": "Hz",
            "frequency": 49.98
            // 电网频率 (Hz)
          }
        ],
        "energy": {
          "active": {
            "data_type": "float",
            "unit": "kW",
            "value": {
              "forward": 1500.5,
              // 正向有功电能 (kWh)
              "reverse": 0.0
              // 反向有功电能 (kWh)
            }
          },
          "reactive": {
            "data_type": "float",
            "unit": "kvarh",
            "value": {
              "forward": 800.2,
              // 正向无功电能 (kvarh)
              "reverse": 45.3
              // 反向无功电能 (kvarh)
            }
          }
        }
      },
      "status": {
        "device_status": 0,
        // 设备状态码 (0=正常)
        "data_quality": 100
        // 数据完整度百分比
      }
    },
    {
      "timestamp": "1710000000123",
      "device_type": "three_phase_meter",
      "device_id": "EM-2025-001",
      "metrics": {
        "voltage": {
          "data_type": "float",
          "unit": "V",
          "value": {
            "phase_a": 220.5,
            // A相电压 (V)
            "phase_b": 219.8,
            // B相电压 (V)
            "phase_c": 221.2,
            // C相电压 (V)
            "line_ab": 380.4,
            // AB线电压 (V)
            "line_bc": 381.0,
            // BC线电压 (V)
            "line_ca": 379.8
            // CA线电压 (V)
          }
        },
        "current": {
          "data_type": "float",
          "unit": "A",
          "value": {
            "phase_a": 15.3,
            // A相电流 (A)
            "phase_b": 14.9,
            // B相电流 (A)
            "phase_c": 15.1
            // C相电流 (A)
          }
        },
        "power": {
          "active": {
            "data_type": "float",
            "unit": "kW",
            "value": {
              "phase_a": 3.3,
              // A相有功功率 (kW)
              "phase_b": 3.1,
              // B相有功功率 (kW)
              "phase_c": 3.2,
              // C相有功功率 (kW)
              "total": 9.6
              // 总有功功率 (kW)
            }
          },
          "reactive": {
            "data_type": "float",
            "unit": "kvar",
            "value": {
              "phase_a": 1.2,
              // A相无功功率 (kvar)
              "phase_b": 1.1,
              // B相无功功率 (kvar)
              "phase_c": 1.3,
              // C相无功功率 (kvar)
              "total": 3.6
              // 总无功功率 (kvar)
            }
          },
          "apparent": {
            "data_type": "float",
            "unit": "kVA",
            "value": {
              "total": 10.2
              // 总视在功率 (kVA)
            }
          }
        },
        "system": [
          {
            "data_type": "float",
            "unit": "",
            "power_factor": 0.92
            // 总功率因数
          },
          {
            "data_type": "float",
            "unit": "Hz",
            "frequency": 49.98
            // 电网频率 (Hz)
          }
        ],
        "energy": {
          "active": {
            "data_type": "float",
            "unit": "kW",
            "value": {
              "forward": 1500.5,
              // 正向有功电能 (kWh)
              "reverse": 0.0
              // 反向有功电能 (kWh)
            }
          },
          "reactive": {
            "data_type": "float",
            "unit": "kvarh",
            "value": {
              "forward": 800.2,
              // 正向无功电能 (kvarh)
              "reverse": 45.3
              // 反向无功电能 (kvarh)
            }
          }
        }
      },
      "status": {
        "device_status": 0,
        // 设备状态码 (0=正常)
        "data_quality": 100
        // 数据完整度百分比
      }
    }
  ]
}
```
### 数据下行
Topic: device/bydpower/down/{device_id}
```json
{
  "msgType": "down",
  "msgId": "{UUID v4}",
  "source": {
    // 指令来源
    "source_id": "GW-EM-01",
    // 发起系统
    "operator": "6789555",
    // 操作员ID
    "name": "马利凯",
    // 操作者
    "auth_token": "Bearer xyz"
    // 认证令牌
  },
  "batch": [
    {
      "transaction_id": "CMD-20240815-001",
      // 唯一指令标识
      "timestamp": "1710000000123",
      // 毫秒级时间戳

      "target": {
        // 目标设备
        "gateway_id": "GW-01",
        // 网关标识
        "device_type": "S7-1200",
        // 设备类型
        "device_id": "PLC-01"
        // 设备唯一ID
      },
      "operation": {
        // 操作定义
        "key": "A启",
        //KEY
        "value": "1",
        //value
        "type": "write_register",
        // 操作类型
        "address": "D100",
        // 寄存器地址
        "data_type": "float32",
        // 数据类型
        "verify": true
        // 要求回读验证
      },
      "control_params": {
        // 控制参数
        "priority": 1,
        // 优先级(0-9,0优先级最高，9优先级最低)
        "timeout": 5000,
        // 超时时间(ms)
        "retry_policy": {
          // 重试策略
          "max_attempts": 3,
          "interval": 1000
        }
      }
    },
    {
      "transaction_id": "CMD-20240815-001",
      // 唯一指令标识
      "timestamp": 1710000000123,
      // 毫秒级时间戳

      "target": {
        // 目标设备
        "gateway_id": "GW-01",
        // 网关标识
        "device_type": "S7-1200",
        // 设备类型
        "device_id": "PLC-01"
        // 设备唯一ID
      },
      "operation": {
        // 操作定义
        "key": "A启",
        //KEY
        "value": "1",
        //value
        "type": "write_register",
        // 操作类型
        "address": "D100",
        // 寄存器地址
        "data_type": "float32",
        // 数据类型
        "verify": true
        // 要求回读验证
      },
      "control_params": {
        // 控制参数
        "priority": 1,
        // 优先级(0-9,0优先级最高，9优先级最低)
        "timeout": 5000,
        // 超时时间(ms)
        "retry_policy": {
          // 重试策略
          "max_attempts": 3,
          "interval": 1000
        }
      }
    }
  ]
}
```

