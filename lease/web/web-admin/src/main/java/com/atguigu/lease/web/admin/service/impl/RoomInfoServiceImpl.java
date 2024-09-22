package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {
    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private ApartmentInfoMapper apartmentInfoMapper;

    @Resource
    private GraphInfoMapper graphInfoMapper;

    @Resource
    private AttrValueMapper attrValueMapper;

    @Resource
    private FacilityInfoMapper facilityInfoMapper;

    @Resource
    private LabelInfoMapper labelInfoMapper;

    @Resource
    private PaymentTypeMapper paymentTypeMapper;

    @Resource
    private LeaseTermMapper leaseTermMapper;

    @Resource
    private GraphInfoService graphInfoService;

    @Resource
    private RoomAttrValueService roomAttrValueService;

    @Resource
    private RoomFacilityService roomFacilityService;

    @Resource
    private RoomLabelService roomLabelService;

    @Resource
    private RoomPaymentTypeService roomPaymentTypeService;

    @Resource
    private RoomLeaseTermService roomLeaseTermService;


    @Override
    public IPage<RoomItemVo> pageItem(IPage<RoomItemVo> roomItemVoIPage, RoomQueryVo queryVo) {
        return roomInfoMapper.pageItem(roomItemVoIPage, queryVo);
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        RoomInfo roomInfo = roomInfoMapper.selectById(id);
        //所属公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectApartmentById(id);

        //图片列表private List<GraphVo> graphVoList;
        List<GraphVo> graphVoList=graphInfoMapper.selectGraphVoByItemTypeAndId(ItemType.ROOM,id);

        //属性信息列表private List<AttrValueVo> attrValueVoList;
        List<AttrValueVo> attrValueVoList=attrValueMapper.selectAttrValueVoById(id);

        //配套信息列表private List<FacilityInfo> facilityInfoList;
        List<FacilityInfo> facilityInfoList=facilityInfoMapper.selectFacilityInfoList(id);

        //标签信息列表private List<LabelInfo> labelInfoList;
        List<LabelInfo>  labelInfoList=labelInfoMapper.selectLabelById(id);

        //支付方式列表private List<PaymentType> paymentTypeList;
        List<PaymentType> paymentTypeList= paymentTypeMapper.selectPaymentTypeListById(id);

        //可选租期列表private List<LeaseTerm> leaseTermList;
        List<LeaseTerm> leaseTermList =leaseTermMapper.selectLeaseTermListById(id);

        RoomDetailVo roomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo,roomDetailVo);
        roomDetailVo.setGraphVoList(graphVoList);
        roomDetailVo.setApartmentInfo(apartmentInfo);
        roomDetailVo.setAttrValueVoList(attrValueVoList);
        roomDetailVo.setFacilityInfoList(facilityInfoList);
        roomDetailVo.setLabelInfoList(labelInfoList);
        roomDetailVo.setLeaseTermList(leaseTermList);
        roomDetailVo.setPaymentTypeList(paymentTypeList);
        return roomDetailVo;
    }

    @Override
    public void saveOrUpdateRoomVo(RoomSubmitVo roomSubmitVo) {
        boolean isUpdate = roomSubmitVo.getId() != null;
        super.saveOrUpdate(roomSubmitVo);
        //如果是更新操作我们就先删除列表
        if (isUpdate) {
            //图片列表的删除
            LambdaQueryWrapper<GraphInfo> graphWrapper = new LambdaQueryWrapper<>();
            graphWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphWrapper.eq(GraphInfo::getItemId, roomSubmitVo.getId());
            graphInfoService.remove(graphWrapper);

            //属性值的删除
            LambdaQueryWrapper<RoomAttrValue> attrValueWrapper = new LambdaQueryWrapper<>();
            attrValueWrapper.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
            roomAttrValueService.remove(attrValueWrapper);

            //配套信息删除
            LambdaQueryWrapper<RoomFacility> facilityWrapper = new LambdaQueryWrapper<>();
            facilityWrapper.eq(RoomFacility::getRoomId, roomSubmitVo.getId());
            roomFacilityService.remove(facilityWrapper);

            //标签信息删除
            LambdaQueryWrapper<RoomLabel> labelWrapper = new LambdaQueryWrapper<>();
            labelWrapper.eq(RoomLabel::getRoomId, roomSubmitVo.getId());
            roomLabelService.remove(labelWrapper);

            //支付方式删除
            LambdaQueryWrapper<RoomPaymentType> paymentTypeWrapper = new LambdaQueryWrapper<>();
            paymentTypeWrapper.eq(RoomPaymentType::getRoomId, roomSubmitVo.getId());
            roomPaymentTypeService.remove(paymentTypeWrapper);

            //租期关系
            LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermWrapper = new LambdaQueryWrapper<>();
            roomLeaseTermWrapper.eq(RoomLeaseTerm::getRoomId, roomSubmitVo.getId());
            roomLeaseTermService.remove(roomLeaseTermWrapper);
        }

        //图片集合
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)) {
            ArrayList<GraphInfo> graphInfoArrayList = new ArrayList<>();
            for (GraphVo item : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemId(roomSubmitVo.getId());
                graphInfo.setUrl(item.getUrl());
                graphInfo.setName(item.getName());
                graphInfo.setItemType(ItemType.ROOM);
                graphInfoArrayList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoArrayList);
        }

        //属性集合
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
        if (!CollectionUtils.isEmpty(attrValueIds)) {
            ArrayList<RoomAttrValue> roomAttrValueArrayList = new ArrayList<>();
            for (Long id : attrValueIds) {
                RoomAttrValue roomAttrValue = new RoomAttrValue();
                roomAttrValue.setRoomId(roomSubmitVo.getId());
                roomAttrValue.setAttrValueId(id);
                roomAttrValueArrayList.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(roomAttrValueArrayList);
        }

        //配套集合
        List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIds)) {
            ArrayList<RoomFacility> facilityInfoArrayList = new ArrayList<>();
            for (Long id : facilityInfoIds) {
                RoomFacility roomFacility = new RoomFacility();
                roomFacility.setRoomId(roomSubmitVo.getId());
                roomFacility.setFacilityId(id);
                facilityInfoArrayList.add(roomFacility);
            }
            roomFacilityService.saveBatch(facilityInfoArrayList);
        }

        //标签集合

        List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();
        if (!CollectionUtils.isEmpty(labelInfoIds)) {
            ArrayList<RoomLabel> roomLabelArrayList = new ArrayList<>();
            for (Long id : labelInfoIds) {
                RoomLabel roomLabel = new RoomLabel();
                roomLabel.setRoomId(roomSubmitVo.getId());
                roomLabel.setLabelId(id);
                roomLabelArrayList.add(roomLabel);
            }

            roomLabelService.saveBatch(roomLabelArrayList);
        }

        //支付方式集合
        List<Long> roomPaymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if (!CollectionUtils.isEmpty(roomPaymentTypeIds)) {
            ArrayList<RoomPaymentType> roomPaymentTypeArrayList = new ArrayList<>();
            for (Long id : roomPaymentTypeIds) {
                RoomPaymentType roomPaymentType = new RoomPaymentType();
                roomPaymentType.setRoomId(roomSubmitVo.getId());
                roomPaymentType.setPaymentTypeId(id);
                roomPaymentTypeArrayList.add(roomPaymentType);
            }
            roomPaymentTypeService.saveBatch(roomPaymentTypeArrayList);
        }


        //可选租期集合
        List<Long> leaseTermIds =roomSubmitVo.getLeaseTermIds();
        if(!CollectionUtils.isEmpty(leaseTermIds)){
            ArrayList<RoomLeaseTerm> roomLeaseTermArrayList = new ArrayList<>();
            for (Long id: leaseTermIds) {
                RoomLeaseTerm roomLeaseTerm = new RoomLeaseTerm();
                roomLeaseTerm.setRoomId(roomSubmitVo.getId());
                roomLeaseTerm.setLeaseTermId(id);
                roomLeaseTermArrayList.add(roomLeaseTerm);
            }
            roomLeaseTermService.saveBatch(roomLeaseTermArrayList);
        }

    }

    @Override
    public void removeRoomById(Long id) {
        super.removeById(id);
        //图片列表的删除
        LambdaQueryWrapper<GraphInfo> graphWrapper = new LambdaQueryWrapper<>();
        graphWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
        graphWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphWrapper);

        //属性值的删除
        LambdaQueryWrapper<RoomAttrValue> attrValueWrapper = new LambdaQueryWrapper<>();
        attrValueWrapper.eq(RoomAttrValue::getRoomId, id);
        roomAttrValueService.remove(attrValueWrapper);

        //配套信息删除
        LambdaQueryWrapper<RoomFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(RoomFacility::getRoomId, id);
        roomFacilityService.remove(facilityWrapper);

        //标签信息删除
        LambdaQueryWrapper<RoomLabel> labelWrapper = new LambdaQueryWrapper<>();
        labelWrapper.eq(RoomLabel::getRoomId, id);
        roomLabelService.remove(labelWrapper);

        //支付方式删除
        LambdaQueryWrapper<RoomPaymentType> paymentTypeWrapper = new LambdaQueryWrapper<>();
        paymentTypeWrapper.eq(RoomPaymentType::getRoomId, id);
        roomPaymentTypeService.remove(paymentTypeWrapper);

        //租期关系
        LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermWrapper = new LambdaQueryWrapper<>();
        roomLeaseTermWrapper.eq(RoomLeaseTerm::getRoomId, id);
        roomLeaseTermService.remove(roomLeaseTermWrapper);
    }
}




