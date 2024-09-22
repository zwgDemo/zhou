package com.atguigu.lease.web.admin.controller.schedule;


import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduleTasks {
    @Resource
    private LeaseAgreementService leaseAgreementService;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkLeaseStatus(){
        LambdaUpdateWrapper<LeaseAgreement> leaseTermUpdateWrapper = new LambdaUpdateWrapper<>();
        leaseTermUpdateWrapper.le(LeaseAgreement::getLeaseEndDate,new Date());
        leaseTermUpdateWrapper.in(LeaseAgreement::getStatus, LeaseStatus.SIGNED,LeaseStatus.WITHDRAWING);
        leaseTermUpdateWrapper.set(LeaseAgreement::getStatus,LeaseStatus.EXPIRED);
        leaseAgreementService.update(leaseTermUpdateWrapper);
    }


}
