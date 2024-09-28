package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.context.LoginUserContext;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.attr.AttrValueVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {
    @Resource
    private RoomInfoMapper  roomInfoMapper;

    @Resource
    private ApartmentInfoService apartmentInfoService;

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
    private FeeValueMapper feeValueMapper;

    @Resource
    private LeaseTermMapper leaseTermMapper;

    @Resource
    private LeaseAgreementMapper leaseAgreementMapper;

    @Resource
    private BrowsingHistoryService browsingHistoryService;

    @Override
    public IPage<RoomItemVo> pageRoomItem(IPage<RoomItemVo> roomItemVoPage, RoomQueryVo queryVo) {

        IPage<RoomItemVo> page=roomInfoMapper.pageRoomItem(roomItemVoPage,queryVo);
        return page;
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        //查询roomInfo
        RoomInfo roomInfo = roomInfoMapper.selectById(id);

        if(roomInfo==null){
            return null;
        }

        //"所属公寓信息"
        ApartmentItemVo apartmentItemVo=apartmentInfoService.selectApartmentVoById(roomInfo.getApartmentId());

        //"图片列表"
        List<GraphVo> graphVoList=graphInfoMapper.selectGraphVoById(ItemType.ROOM,id);

        //"属性信息列表"
        List<AttrValueVo> attrValueVoList=attrValueMapper.selectAttrValueVoById(id);

        //"配套信息列表"
        List<FacilityInfo> facilityInfoList=facilityInfoMapper.selectFacilityById(id);

        //"标签信息列表"
        List<LabelInfo> labelInfoList =labelInfoMapper.selectLabelInfoByRoomId(id);

        //"支付方式列表"
        List<PaymentType> paymentTypeList=paymentTypeMapper.selectPaymentTypeByRoomId(id);

        //"杂费列表"
        List<FeeValueVo> feeValueVoList =feeValueMapper.selectFeeValueVoByRoomId(roomInfo.getApartmentId());

        //"租期列表"
        List<LeaseTerm> leaseTermList=leaseTermMapper.selectLeaseTermListById(id);


        //"房间是否已入住"
        LambdaQueryWrapper<LeaseAgreement> leaseAgreementWrapper = new LambdaQueryWrapper<>();
        leaseAgreementWrapper.eq(LeaseAgreement::getRoomId,roomInfo.getId());
        leaseAgreementWrapper.in(LeaseAgreement::getStatus,LeaseStatus.SIGNED,LeaseStatus.WITHDRAWING);
        Long count = leaseAgreementMapper.selectCount(leaseAgreementWrapper);

        RoomDetailVo appRoomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo, appRoomDetailVo);
        appRoomDetailVo.setIsDelete(roomInfo.getIsDeleted() == 1);
        appRoomDetailVo.setIsCheckIn(count > 0);
        appRoomDetailVo.setApartmentItemVo(apartmentItemVo);
        appRoomDetailVo.setGraphVoList(graphVoList);
        appRoomDetailVo.setAttrValueVoList(attrValueVoList);
        appRoomDetailVo.setFacilityInfoList(facilityInfoList);
        appRoomDetailVo.setLabelInfoList(labelInfoList);
        appRoomDetailVo.setPaymentTypeList(paymentTypeList);
        appRoomDetailVo.setFeeValueVoList(feeValueVoList);
        appRoomDetailVo.setLeaseTermList(leaseTermList);


        browsingHistoryService.saveBrowsingHistory(LoginUserContext.getLoginUser().getUserId(),id);

        return appRoomDetailVo;
    }

    @Override
    public IPage<RoomItemVo> pageItemByApartmentId(IPage<RoomItemVo> roomItemVoIPage, Long id) {
        IPage<RoomItemVo> page=roomInfoMapper.pageItemByApartmentId(roomItemVoIPage,id);
        return page;
    }
}




