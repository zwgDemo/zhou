package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {
    @Resource
    private LeaseAgreementMapper leaseAgreementMapper;

    @Resource
    private ApartmentInfoMapper apartmentInfoMapper;

    @Resource
    private LeaseTermMapper leaseTermMapper;

    @Resource
    private PaymentTypeMapper paymentTypeMapper;

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private GraphInfoMapper graphInfoMapper;


    @Override
    public List<AgreementItemVo> getListItem(String phone) {
        List<AgreementItemVo> result=leaseAgreementMapper.getListItem(phone);
        return result;
    }

    @Override
    public AgreementDetailVo getDetailById(Long id) {
        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);

        if(leaseAgreement==null){
            return null;
        }

        //公寓名称
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(leaseAgreement.getApartmentId());
        String apartmentName = apartmentInfo.getName();

        //租期以及租期单位
        LeaseTerm leaseTerm = leaseTermMapper.selectById(leaseAgreement.getLeaseTermId());

        //支付方式
        PaymentType paymentType = paymentTypeMapper.selectById(leaseAgreement.getPaymentTypeId());

        //房间号
        RoomInfo roomInfo = roomInfoMapper.selectById(leaseAgreement.getRoomId());

        //房间图片列表
        List<GraphVo> roomGraphVoList = graphInfoMapper.selectGraphVoById(ItemType.ROOM, leaseAgreement.getRoomId());

        //公寓图片列表
        List<GraphVo> apartmentGraphVoList = graphInfoMapper.selectGraphVoById(ItemType.APARTMENT, leaseAgreement.getApartmentId());

        AgreementDetailVo agreementDetailVo = new AgreementDetailVo();

        BeanUtils.copyProperties(leaseAgreement, agreementDetailVo);
        agreementDetailVo.setApartmentName(apartmentInfo.getName());
        agreementDetailVo.setRoomNumber(roomInfo.getRoomNumber());
        agreementDetailVo.setApartmentGraphVoList(apartmentGraphVoList);
        agreementDetailVo.setRoomGraphVoList(roomGraphVoList);
        agreementDetailVo.setPaymentTypeName(paymentType.getName());
        agreementDetailVo.setLeaseTermMonthCount(leaseTerm.getMonthCount());
        agreementDetailVo.setLeaseTermUnit(leaseTerm.getUnit());

        return agreementDetailVo;
    }
}




