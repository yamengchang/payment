package com.sup.pojo;

import java.util.*;

import static com.sup.constant.Status.*;

/**
 * Created by zwj on 2018/12/17.
 * 房屋信息
 */
public class House extends BaseEntity {

	private String sameHouseIds;
	private String instanceId;
	//小区信息
	private String name;//小区名
	private String provName;
	private String cityName;
	private String countyName;
	private String address;//详细地址

	//门牌幢信息
	private String buildingCode;//门牌幢编号
	private String floorCount;//层高
	private Boolean isElevator;//是否是电梯房

	//房源信息
	private String houseId;//房屋id
	private String unitNo;//单元号
	private String houseNo;//房间号
	private String floor;//所在楼层
	private String houseSpace;//房屋面积
	private String roomNum;//室
	private String parlourNum;//厅
	private String toiletNum;//卫
	private String houseType;//户型
	private String houseTypeName;

	private String buildType;//房屋类型 0-分散式 1-集中式
	private String buildTypeName;//名称
	private String leaseType;//出租方式  0-合租  1-整租
	private String leaseTypeName;//名称
	private String householdNumber;//户号

	//房间
	private String roomCode;//房间号
	private String alias;//房间别名（eg:主卧）
	private String roomSpace;//房间面积
	private String leaseStatus;//出租状态  0-未租  1-已租 2-退租  3-续租
	private String leaseStatusName;//名称
	private String note;//备注
	private String thirdNo;//第三方id

	//费用信息
	private String maintenanceCost;//物业费
	private String waterCost;//水费
	private String electricityCost;//电费
	private String gasCost;//燃气费
	private String airConditioningCost;//空调费

	//添加字段
	//是否为业主
	private String roomIsOwner = notRoomOwner;
	//产权类型
	private String roomType;
	//房产证编号
	private String roomCertCode;
	//房产证业主姓名
	private String roomOwnerName;
	//	产权地址
	private String propertyAddress;

	private String isUpstream;//是否是上游房源 0-否 1-是
	private String isDownStream;//是否是下游房源 0-否 1-是



	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof House)) return false;
		House house = (House)o;
		return Objects.equals(name, house.name) && Objects.equals(provName, house.provName) && Objects.equals(cityName, house.cityName) && Objects.equals(countyName, house.countyName) && Objects.equals(address, house.address);
	}

	@Override
	public int hashCode() {

		return Objects.hash(name, provName, cityName, countyName, address);
	}

	public String getHouseNo() {
		return houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	public String getSameHouseIds() {
		return sameHouseIds;
	}

	public void setSameHouseIds(String sameHouseIds) {
		this.sameHouseIds = sameHouseIds;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvName() {
		return provName;
	}

	public void setProvName(String provName) {
		this.provName = provName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBuildingCode() {
		return buildingCode;
	}

	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}

	public String getFloorCount() {
		return floorCount;
	}

	public void setFloorCount(String floorCount) {
		this.floorCount = floorCount;
	}

	public Boolean getIsElevator() {
		return isElevator;
	}

	public void setIsElevator(Boolean elevator) {
		isElevator = elevator;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getHouseSpace() {
		return houseSpace;
	}

	public void setHouseSpace(String houseSpace) {
		this.houseSpace = houseSpace;
	}

	public String getBuildType() {
		return buildType;
	}

	public void setBuildType(String buildType) {
		this.buildType = buildType;
	}

	public String getBuildTypeName() {
		return buildTypeName;
	}

	public void setBuildTypeName(String buildTypeName) {
		this.buildTypeName = buildTypeName;
	}

	public String getLeaseType() {
		return leaseType;
	}

	public void setLeaseType(String leaseType) {
		this.leaseType = leaseType;
	}

	public String getLeaseTypeName() {
		return leaseTypeName;
	}

	public void setLeaseTypeName(String leaseTypeName) {
		this.leaseTypeName = leaseTypeName;
	}

	public String getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(String roomNum) {
		this.roomNum = roomNum;
	}

	public String getParlourNum() {
		return parlourNum;
	}

	public void setParlourNum(String parlourNum) {
		this.parlourNum = parlourNum;
	}

	public String getToiletNum() {
		return toiletNum;
	}

	public void setToiletNum(String toiletNum) {
		this.toiletNum = toiletNum;
	}

	public String getHouseholdNumber() {
		return householdNumber;
	}

	public void setHouseholdNumber(String householdNumber) {
		this.householdNumber = householdNumber;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getRoomSpace() {
		return roomSpace;
	}

	public void setRoomSpace(String roomSpace) {
		this.roomSpace = roomSpace;
	}

	public String getLeaseStatus() {
		return leaseStatus;
	}

	public void setLeaseStatus(String leaseStatus) {
		this.leaseStatus = leaseStatus;
	}

	public String getLeaseStatusName() {
		return leaseStatusName;
	}

	public void setLeaseStatusName(String leaseStatusName) {
		this.leaseStatusName = leaseStatusName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getThirdNo() {
		return thirdNo;
	}

	public void setThirdNo(String thirdNo) {
		this.thirdNo = thirdNo;
	}

	public String getMaintenanceCost() {
		return maintenanceCost;
	}

	public void setMaintenanceCost(String maintenanceCost) {
		this.maintenanceCost = maintenanceCost;
	}

	public String getWaterCost() {
		return waterCost;
	}

	public void setWaterCost(String waterCost) {
		this.waterCost = waterCost;
	}

	public String getElectricityCost() {
		return electricityCost;
	}

	public void setElectricityCost(String electricityCost) {
		this.electricityCost = electricityCost;
	}

	public String getGasCost() {
		return gasCost;
	}

	public void setGasCost(String gasCost) {
		this.gasCost = gasCost;
	}

	public String getAirConditioningCost() {
		return airConditioningCost;
	}

	public void setAirConditioningCost(String airConditioningCost) {
		this.airConditioningCost = airConditioningCost;
	}

	public String getRoomIsOwner() {
		return roomIsOwner;
	}

	public void setRoomIsOwner(String roomIsOwner) {
		this.roomIsOwner = roomIsOwner;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getRoomCertCode() {
		return roomCertCode;
	}

	public void setRoomCertCode(String roomCertCode) {
		this.roomCertCode = roomCertCode;
	}

	public String getRoomOwnerName() {
		return roomOwnerName;
	}

	public void setRoomOwnerName(String roomOwnerName) {
		this.roomOwnerName = roomOwnerName;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public String getIsUpstream() {
		return isUpstream;
	}

	public void setIsUpstream(String isUpstream) {
		this.isUpstream = isUpstream;
	}

	public String getIsDownStream() {
		return isDownStream;
	}

	public void setIsDownStream(String isDownStream) {
		this.isDownStream = isDownStream;
	}


	public String getHouseId() {
		return houseId;
	}

	public void setHouseId(String houseId) {
		this.houseId = houseId;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public String getHouseTypeName() {
		return houseTypeName;
	}

	public void setHouseTypeName(String houseTypeName) {
		this.houseTypeName = houseTypeName;
	}

}
