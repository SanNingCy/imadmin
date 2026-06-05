# 会议室后台管理系统

## 概述

会议室后台管理系统提供了完整的会议室管理功能，包括查询、解散等操作。该系统基于现有的声网会议室功能构建，不影响现有功能的正常使用。

## 功能特性

### 1. 会议列表查询（分页）
- **接口路径**: `POST /api/admin/room/list`
- **功能**: 分页查询会议列表，支持多种筛选条件和排序
- **支持筛选**: 房间ID、群ID、状态、时间范围、房间名称、创建者ID等
- **支持排序**: 创建时间、更新时间、最后活跃时间、房间ID

### 2. 会议聊天室查询
- **接口路径**: `POST /api/admin/room/chatroom/detail`
- **功能**: 根据会议ID获取聊天室的详细信息
- **返回信息**: 房间基本信息、配置参数、用户统计、聊天室配置等

### 3. 解散会议
- **接口路径**: `POST /api/admin/room/destroy`
- **功能**: 强制解散指定的会议，释放所有相关资源
- **操作影响**: 移除会议内所有用户、清理相关数据、释放系统资源

### 4. 批量解散会议
- **接口路径**: `POST /api/admin/room/batchDestroy`
- **功能**: 批量解散多个会议，用于批量管理操作

## API 使用示例

### 1. 查询会议列表

```bash
POST /api/admin/room/list
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 20,
  "status": ["active", "inactive"],
  "startTime": 1694073000000,
  "endTime": 1694159400000,
  "roomName": "会议室",
  "orderBy": "createTime",
  "orderDirection": "desc"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 5,
    "list": [
      {
        "roomId": "room_123456",
        "groupId": "group_12345",
        "ownerId": "user_123",
        "roomName": "会议室001",
        "status": "active",
        "isBanned": false,
        "userCount": 15,
        "maxUsers": 1000,
        "createTime": 1694073000000,
        "updateTime": 1694073500000,
        "lastActiveTime": 1694073500000
      }
    ]
  },
  "success": true
}
```

### 2. 查询会议聊天室详情

```bash
POST /api/admin/room/chatroom/detail
Content-Type: application/json

{
  "roomId": "room_123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "roomId": "room_123456",
    "chatRoomId": "chat_room_123456",
    "roomName": "会议室001",
    "status": "active",
    "userCount": 15,
    "maxUsers": 1000,
    "ownerId": "user_123",
    "groupId": "group_12345",
    "createTime": 1694073000000,
    "lastActiveTime": 1694073500000,
    "chatRoomConfig": {
      "maxUsers": 1000,
      "name": "会议室001"
    }
  },
  "success": true
}
```

### 3. 解散会议

```bash
POST /api/admin/room/destroy
Content-Type: application/json

{
  "roomId": "room_123456",
  "reason": "管理员强制解散",
  "operatorId": "admin_001"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "会议解散成功",
  "data": {
    "roomId": "room_123456",
    "destroyTime": 1694073600000,
    "operatorId": "admin_001"
  },
  "success": true
}
```

### 4. 批量解散会议

```bash
POST /api/admin/room/batchDestroy
Content-Type: application/json

{
  "roomIds": ["room_123456", "room_789012"],
  "reason": "批量清理过期会议",
  "operatorId": "admin_001"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量解散完成",
  "data": {
    "total": 2,
    "success": 2,
    "failed": 0,
    "results": [
      {"roomId": "room_123456", "success": true},
      {"roomId": "room_789012", "success": true}
    ]
  },
  "success": true
}
```

## 状态说明

### 会议室状态
- `pending_create`: 待创建状态
- `active`: 活跃状态，有用户在线
- `inactive`: 非活跃状态，无用户在线
- `destroyed`: 已销毁状态

### 排序字段
- `createTime`: 创建时间
- `updateTime`: 更新时间
- `lastActiveTime`: 最后活跃时间
- `roomId`: 房间ID

### 排序方向
- `asc`: 升序
- `desc`: 降序

## 注意事项

1. **权限要求**: 所有接口都需要管理员权限
2. **解散操作**: 解散操作是不可逆的，请谨慎使用
3. **批量操作**: 批量解散时，即使部分操作失败，也会继续执行其他操作
4. **数据一致性**: 解散操作会同时更新本地数据库和调用声网API
5. **错误处理**: 所有接口都有完善的错误处理和日志记录

## 技术实现

- **Controller**: `RoomAdminController` - 处理HTTP请求
- **Service**: `RoomAdminService` - 业务逻辑处理
- **Repository**: `RoomListV2Repository` - 数据访问
- **数据库**: MongoDB - 存储会议室信息
- **外部API**: 声网API - 解散会议室

## 扩展性

该系统设计具有良好的扩展性，可以轻松添加新功能：
- 会议室封禁/解封
- 会议室统计信息
- 会议室配置管理
- 用户权限管理
- 操作日志记录