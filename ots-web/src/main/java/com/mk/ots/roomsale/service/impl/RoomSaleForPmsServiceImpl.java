package com.mk.ots.roomsale.service.impl;

import com.mk.ots.common.utils.DateTools;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.mapper.RoomSaleForPmsMapper;
import com.mk.ots.mapper.RoomSaleMapper;
import com.mk.ots.roomsale.model.*;
import com.mk.ots.roomsale.service.RoomSaleForPmsService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;

/**
 * RoomSaleMapper.
 * 
 * @author kangxiaolong.
 */
@Service
public class RoomSaleForPmsServiceImpl implements RoomSaleForPmsService {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(RoomSaleForPmsServiceImpl.class);

	@Autowired
	private RoomSaleMapper roomSaleMapper;
	@Autowired
	private HotelService hotelService;
	@Autowired
	private RoomSaleForPmsMapper roomSaleForPmsMapper;
	public String updateTRoomSaleConfig(TRoomSaleConfigForPms bean){
		if (bean.getRoomTypeId()==null&&bean.getNewCount()==null){
			return "ERROR,提交参数不完整";
		}
		TRoomSaleConfig roomSaleConfig=roomSaleForPmsMapper.getRoomTypeByPms(bean.getRoomTypeId());
		if(roomSaleConfig==null){
			return  "ERROR,房型不存在";
		}
		TRoomSaleConfig newConfig =new TRoomSaleConfig();
		newConfig.setRoomTypeId(roomSaleConfig.getId());
		newConfig.setValid("T");
		List<TRoomSaleConfig> roomSaleConfigList=roomSaleForPmsMapper.queryRoomSaleConfigByParams(newConfig);
		if(CollectionUtils.isEmpty(roomSaleConfigList)){
			return  "ERROR,修改房型不在活动配置表中";
		}
		for (TRoomSaleConfig config:roomSaleConfigList){
			if (config.getRoomId()!=null){
				return  "ERROR,按协议该房型指定到房间号，不能变更协议量";
			}
		}
		TRoomSaleConfig configToUpdate=roomSaleConfigList.get(0);
		if (bean.getNewCount()<configToUpdate.getDealCount()){
			return  "ERROR,修改数量小于协议数量";
		}
		configToUpdate.setNum(bean.getNewCount());
		Integer result= roomSaleForPmsMapper.updateRoomSaleNum(configToUpdate);
		if (result>0){
			TRoomSaleConfig configInfo=roomSaleForPmsMapper.getConfigInfoById(configToUpdate.getSaleConfigInfoId());
			Time nowTime = Time.valueOf(DateTools.getTime("HH:mm:ss")) ;
			if (nowTime.compareTo(configInfo.getStartTime())>=0){
				return "OK,变更次日生效";
			}
			return  "OK,变更已生效";
		}else{
			return  "ERROR,更新失败";
		}
	}
	public TRoomSaleForPms getHotelRoomSale(TRoomSaleConfigForPms bean){
		if (bean.getHotelId()==null){
			return null;
		}
		List<TRoomSalePms> roomSalePmsList= roomSaleForPmsMapper.getRoomSalePms();
		String confShow ="";
		String showBegin="";
		long showContinue=0;
		String noConfShow="";
		for (TRoomSalePms roomSalePms:roomSalePmsList){
			if (roomSalePms.getType()==1){
				confShow=roomSalePms.getText();
				showBegin=DateTools.dateToString(roomSalePms.getShowBegin(), "HH:mm");
				showContinue=roomSalePms.getShowContinue();
			}else if(roomSalePms.getType()==2){
				noConfShow=roomSalePms.getText();
			}
		}
		TRoomSaleForPms roomSaleForPms=new TRoomSaleForPms();
		roomSaleForPms.setConfShow(confShow);
		roomSaleForPms.setShowBegin(showBegin);
		roomSaleForPms.setShowContinue(showContinue);
		roomSaleForPms.setNoConfShow(noConfShow);
		TRoomSaleConfig saleConfigInfoBean=new TRoomSaleConfig();
		saleConfigInfoBean.setValid("T");
		saleConfigInfoBean.setSaleTypeId(1);
		List<TRoomSaleConfig> roomSaleConfigInfoList=roomSaleForPmsMapper.getConfigInfo(saleConfigInfoBean);
		if (CollectionUtils.isEmpty(roomSaleConfigInfoList)){
			return roomSaleForPms;
		}
		TRoomSaleConfig roomSaleConfigInfo=roomSaleConfigInfoList.get(0);
		roomSaleForPms.setType(Integer.valueOf(roomSaleConfigInfo.getSaleTypeId()));
		roomSaleForPms.setBegin(DateTools.dateToString(roomSaleConfigInfo.getStartTime(), "HH:mm"));
		Long continues = getBetweenTime(roomSaleConfigInfo.getStartTime(), roomSaleConfigInfo.getEndTime());
		roomSaleForPms.setContinues(continues);
		List<TRoomTypeForPms> roomTypeForPmsList=getRoomTypeForPms(bean.getHotelId(),1);
		roomSaleForPms.setInfo(roomTypeForPmsList);
		return roomSaleForPms;
	}
	public List<TRoomTypeForPms> getRoomTypeForPms(String pmsHotelId , Integer saleType) {
		TRoomSaleConfig roomSaleConfigBean=new TRoomSaleConfig();
		roomSaleConfigBean.setPms(pmsHotelId);
		roomSaleConfigBean.setSaleType(saleType);
		List<TRoomSaleConfig> roomSaleConfigList=roomSaleForPmsMapper.getRoomSaleByPmsHotel(roomSaleConfigBean);
		List<TRoomTypeForPms> roomTypeForPmsList=new ArrayList<>();
		Map<String,TRoomTypeForPms> roomTypeForPmsMap=new HashMap<String,TRoomTypeForPms>();
		for (TRoomSaleConfig roomSaleConfig:roomSaleConfigList){
			TRoomSaleConfig roomType = roomSaleForPmsMapper.getRoomTypeById(roomSaleConfig.getRoomTypeId());
			if (roomTypeForPmsMap.get(roomType.getPms())==null) {
				TRoomTypeForPms roomTypeForPms = new TRoomTypeForPms();
				roomTypeForPms.setRoomTypeId(roomType.getPms());
				if (roomSaleConfig.getRoomId()==null){
					roomTypeForPms.setCurrCount(roomSaleConfig.getNum());
					roomTypeForPms.setMinCount(roomSaleConfig.getDealCount());
					roomTypeForPms.setIsEdit("T");
				}else {
					roomTypeForPms.setCurrCount(1);
					roomTypeForPms.setMinCount(1);
					roomTypeForPms.setIsEdit("F");
				}
				roomTypeForPmsMap.put(roomType.getPms(),roomTypeForPms);
			}else {
				TRoomTypeForPms roomTypeForPms=roomTypeForPmsMap.get(roomType.getPms());
				if (roomSaleConfig.getRoomId()==null){//如果房间号为空，则加上房型的数量
					int currCount=roomTypeForPms.getCurrCount()+roomSaleConfig.getNum();
					int dealCount=roomTypeForPms.getMinCount()+roomSaleConfig.getDealCount();
					roomTypeForPms.setCurrCount(currCount);
					roomTypeForPms.setMinCount(dealCount);
					roomTypeForPmsMap.put(roomType.getPms(),roomTypeForPms);
				}else{//如果房间号不为空，则加上活动数量1
					int currCount=roomTypeForPms.getCurrCount()+1;
					int dealCount=roomTypeForPms.getMinCount()+1;
					roomTypeForPms.setCurrCount(currCount);
					roomTypeForPms.setMinCount(dealCount);
					roomTypeForPmsMap.put(roomType.getPms(),roomTypeForPms);
				}
				if (roomSaleConfig.getRoomId()!=null&&roomTypeForPms.getIsEdit()!="F"){
					roomTypeForPms.setIsEdit("F");
				}
			}
		}
		for(String key : roomTypeForPmsMap.keySet()){
			TRoomTypeForPms roomTypeForPms =roomTypeForPmsMap.get(key);
			roomTypeForPmsList.add(roomTypeForPms);
		}
		return roomTypeForPmsList;
	}
	public long getBetweenTime(Date beginTime, Date endTime) {
		Calendar dateOne = Calendar.getInstance(), dateTwo = Calendar.getInstance();
		dateOne.setTime(beginTime);
		dateTwo.setTime(endTime);
		if (beginTime.compareTo(endTime)>=0){
			dateTwo.add(Calendar.DATE,1);
		}
		long timeOne = dateOne.getTimeInMillis();
		long timeTwo = dateTwo.getTimeInMillis();
		long minute = (timeTwo - timeOne) / (1000 * 60);//转化minute
		return minute;
	}
}

