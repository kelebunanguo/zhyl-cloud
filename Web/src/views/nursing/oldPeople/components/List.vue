<template>
  <div v-if="floorData.length">
    <!-- end -->
    <!-- 房间列表 -->
    <div v-if="baseData.length" class="houseList">
      <div v-for="(roomItem, roomindex) in baseData" :key="roomindex" class="item">
        <!-- 房间标题 -->
        <div class="title">
          <div class="lText">
            <span class="name">{{ roomItem.code }}</span>
          </div>
          <div class="batchSetting" @click="handleSetting(roomItem, 'batch')">
            批量设置护理员
          </div>
        </div>
        <!--  -->
        <!-- 床位列表 -->
        <div class="bedList">
          <div v-if="roomItem.bedVoList && roomItem.bedVoList.length > 0">
            <ul>
              <li v-for="(item, index) in roomItem.bedVoList
                .filter((item) => item.ename)
                .filter((item) => item.bedStatus === 1)" :key="index" class="leisure"
                :class="routeType !== 'config' ? '' : 'bedNoHover'">
                <div class="bedText">
                  <p>床位号：{{ item.bedNumber }}</p>
                  <p>{{ item.ename ? '老人姓名：' + item.ename : '空闲' }}</p>
                  <p v-if="item.userVos && item.userVos.length" class="care">
                    护理员姓名：
                    <span v-for="(val, index) in item.userVos?.map(
                      (item) => item.nickName
                    )" :key="index">{{ val }}</span>
                  </p>
                  <p v-else class="care noCare">
                    护理员姓名：
                    <span>当前床位没有安排护理员</span>
                  </p>
                </div>
                <div class="setting" @click="handleSetting(item, 'single')"></div>
              </li>
            </ul>
          </div>
          <div v-else class="noData">当前房间没有安排床位</div>
        </div>
        <!-- end -->
      </div>
    </div>
    <!-- end -->
    <div v-else>
      <NoData></NoData>
    </div>
  </div>
  <div v-else>
    <NoData></NoData>
  </div>
</template>
<script setup>
import NoData from '@/components/noData/index.vue'; // 无数据提示组件
// ------定义变量------

// 获取父组件值、方法
defineProps({
  //  基础列表数据
  baseData: {
    type: Array,
  },
  // 楼层数据
  floorData: {
    type: Array,
  },
  routeType: {
    type: String,
    default: null,
  },
});
// 触发父级事件
const emit = defineEmits([
  'handleCheck',
  'handleSelectOlder',
  'handleSingleOlder',
]);
// ------定义方法------
const handleSetting = (roomItem, type) => {
  emit('handleCheck');
  emit(
    'handleSelectOlder',
    type === 'single' ? [roomItem] : roomItem.bedVoList
  );
  if (type === 'single') {
    emit(
      'handleSingleOlder',
      roomItem.userVos.map((item) => item.userId)
    );
  }
};
</script>
<style lang="scss" scoped>
.houseList {
  display: block;

  .item {
    display: block;
    width: auto;

    .bedList {
      .leisure {
        background-color: #f6fbfa;
        position: relative;

        .setting {
          width: 20px;
          height: 20px;
          position: absolute;
          right: 12px;
          top: 12px;
          background-image: url('@/assets/icons/icon-sz@2x.png');
          background-size: cover;
          background-repeat: no-repeat;
          background-position: center;
          cursor: pointer;
        }

        .bedText {
          width: 100%;

          .oldName {
            margin-bottom: 24px;
          }

          .olderStatus>div {
            display: flex;
          }

          .noPeople {
            text-align: center;
            color: var(--color-bk6);
            display: block;
            margin-top: 35px;
          }

          .olderStatus {
            .noPeople {
              text-align: center;
              color: var(--color-bk6);
              display: block;
              margin-top: 35px;
            }

            .left {
              margin-right: 22px;
              display: flex;
              flex-direction: column;

              img {
                width: 44px;
                height: 44px;
              }

              .bedStatus {
                font-size: 14px;
                color: #595959;
                position: relative;
                left: 3px;
              }
            }

            .right {
              display: flex;
              flex-direction: column;
              justify-content: space-between;
              position: relative;
              bottom: 7px;

              .top,
              .bottom {
                display: flex;
                align-items: center;

                img {
                  width: 40px;
                  height: 40px;
                  margin-right: 5px;
                }

                .label {
                  min-width: 56px;
                  white-space: nowrap;
                }

                .label,
                .unit {
                  font-size: 14px;
                  color: #595959;
                }

                .val {
                  font-size: 24px;
                  color: #262626;
                  font-weight: bold;
                  margin-right: 6px;
                }
              }
            }
          }

          .deviceBox {
            display: flex;
            justify-content: space-between;

            .device {
              display: flex;
              align-items: center;
              margin-left: 15px;

              .device-item {
                width: 16px;
                height: 16px;
              }
            }
          }

          p {
            margin-bottom: 4px;
          }

          .care {
            span {
              padding: 2.5px 9.5px;
              border-radius: 100px 100px 100px 100px;
              font-size: 12px;
              margin-right: 10px;
            }

            span:nth-child(1) {
              color: #008d71;
              background-color: #e5f4f2;
            }

            span:nth-child(2) {
              color: #395cff;
              background-color: #d8edfd;
            }

            span:nth-child(3) {
              color: #fb9b0d;
              background-color: #f4edd1;
            }

            span:nth-child(4) {
              color: #ff6800;
              background-color: rgba(#ffcfae, 0.37);
            }
          }

          .noCare {
            span {
              padding: 0 !important;
              background-color: transparent !important;
              color: #999 !important;
              font-size: 14px;
            }
          }
        }
      }
    }

    .title {
      .wuData {
        display: flex;

        .wuDataItem {
          margin-right: 20px;
        }
      }

      .device {
        display: flex;
        align-items: center;
        margin-left: 15px;

        .device-item {
          width: 16px;
          height: 16px;
          margin-right: 8px;
        }
      }

      .batchSetting {
        color: #008d71;
        cursor: pointer;
      }
    }
  }
}
</style>
