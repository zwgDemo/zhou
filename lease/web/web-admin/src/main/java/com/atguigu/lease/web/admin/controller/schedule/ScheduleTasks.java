package com.atguigu.lease.web.admin.controller.schedule;


import com.atguigu.lease.common.minio.MinioProperties;
import com.atguigu.lease.model.entity.GraphInfo;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.GraphInfoService;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

@Component
public class ScheduleTasks {
    @Resource
    private LeaseAgreementService leaseAgreementService;

    @Resource
    private GraphInfoService graphInfoService;

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioProperties minioProperties;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkLeaseStatus(){
        LambdaUpdateWrapper<LeaseAgreement> leaseTermUpdateWrapper = new LambdaUpdateWrapper<>();
        leaseTermUpdateWrapper.le(LeaseAgreement::getLeaseEndDate,new Date());
        leaseTermUpdateWrapper.in(LeaseAgreement::getStatus, LeaseStatus.SIGNED,LeaseStatus.WITHDRAWING);
        leaseTermUpdateWrapper.set(LeaseAgreement::getStatus,LeaseStatus.EXPIRED);
        leaseAgreementService.update(leaseTermUpdateWrapper);
    }

    @Scheduled(cron = " 0/2 * * * * *")
    private void deleteGraph(){
        LambdaQueryWrapper<GraphInfo> queryWrapper0 = new LambdaQueryWrapper<>();
        queryWrapper0.eq(GraphInfo::getIsDeleted,0);
        LambdaQueryWrapper<GraphInfo> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(GraphInfo::getIsDeleted,1);
        List<GraphInfo> list0 = graphInfoService.list(queryWrapper0);
        List<GraphInfo> list1 = graphInfoService.list(queryWrapper1);
        list1.removeAll(list0);

        for (GraphInfo item: list1) {
            String url =item.getUrl();
            if(url!=null){
                String fileName=url.substring(url.lastIndexOf("/")+1);
                try {
                    minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioProperties.getBucketName()).object(fileName).build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
