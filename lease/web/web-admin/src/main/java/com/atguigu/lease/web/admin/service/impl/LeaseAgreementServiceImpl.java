package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.atguigu.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.atguigu.lease.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Resource
    private LeaseAgreementMapper leaseAgreementMapper;

    @Resource
    private ApartmentInfoMapper apartmentInfoMapper;

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private PaymentTypeMapper paymentTypeMapper;

    @Resource
    private LeaseTermMapper leaseTermMapper;

    @Override
    public IPage<AgreementVo> pageLeaseAgreement(IPage<AgreementVo> agreementIPage, AgreementQueryVo queryVo) {
        return leaseAgreementMapper.pageLeaseAgreement(agreementIPage, queryVo);
    }

    @Override
    public AgreementVo getAgreementById(Long id) {

        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);

        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(leaseAgreement.getApartmentId());

        RoomInfo roomInfo = roomInfoMapper.selectById(leaseAgreement.getRoomId());

        PaymentType paymentType = paymentTypeMapper.selectById(leaseAgreement.getPaymentTypeId());

        LeaseTerm leaseTerm = leaseTermMapper.selectById(leaseAgreement.getLeaseTermId());

        AgreementVo agreementVo = new AgreementVo();

        BeanUtils.copyProperties(leaseAgreement,agreementVo);
        agreementVo.setLeaseTerm(leaseTerm);
        agreementVo.setApartmentInfo(apartmentInfo);
        agreementVo.setRoomInfo(roomInfo);
        agreementVo.setPaymentType(paymentType);
        return agreementVo;
    }
}




