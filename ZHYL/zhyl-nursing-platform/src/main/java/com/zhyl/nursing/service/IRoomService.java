package com.zhyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhyl.nursing.domain.Room;
import com.zhyl.nursing.vo.RoomVo;

import java.util.List;

/**
 * 房间Service接口
 *
 * @author ruoyi
 * @date 2024-04-26
 */
public interface IRoomService extends IService<Room>
{
    /**
     * 查询房间
     *
     * @param id 房间主键
     * @return 房间
     */
    public Room selectRoomById(Long id);

    /**
     * 查询房间列表
     *
     * @param room 房间
     * @return 房间集合
     */
    public List<Room> selectRoomList(Room room);

    /**
     * 新增房间
     *
     * @param room 房间
     * @return 结果
     */
    public int insertRoom(Room room);

    /**
     * 修改房间
     *
     * @param room 房间
     * @return 结果
     */
    public int updateRoom(Room room);

    /**
     * 批量删除房间
     *
     * @param ids 需要删除的房间主键集合
     * @return 结果
     */
    public int deleteRoomByIds(Long[] ids);

    /**
     * 根据楼层 id 获取房间视图对象列表
     * @param floorId
     * @return
     */
    List<RoomVo> getRoomsByFloorId(Long floorId);

    /**
     * 获取所有房间（负责老人）
     * @param floorId
     * @return
     */
    List<RoomVo> getRoomsWithNurByFloorId(Long floorId);

    /**
     * 根据房间 id 根据房间id查询房间数据(楼层、房间、价格)
     * @param id 房间id
     * @return 房间数据
     */
    RoomVo getRoomById(Long id);
}
