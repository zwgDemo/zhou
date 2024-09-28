package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.FacilityInfo;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Resource
    private ApartmentInfoMapper apartmentInfoMapper;

    @Resource
    private LabelInfoMapper labelInfoMapper;

    @Resource
    private GraphInfoMapper graphInfoMapper;

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private FacilityInfoMapper facilityInfoMapper;


    @Override
    public ApartmentItemVo selectApartmentVoById(Long apartmentId) {

        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(apartmentId);
        if (apartmentInfo==null){
            return null;
        }

        //公寓下的标签信息列表
        List<LabelInfo> labelInfoList =labelInfoMapper.selectLabelInfoById(apartmentId);

        //获取公寓相关的图片
        List<GraphVo> graphVoList=graphInfoMapper.selectGraphVoById(ItemType.APARTMENT,apartmentId);

        //公寓租金最小值
        BigDecimal minRent=roomInfoMapper.selectMinRentByApartmentId(apartmentId);

        ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
        BeanUtils.copyProperties(apartmentInfo,apartmentItemVo);
        apartmentItemVo.setGraphVoList(graphVoList);
        apartmentItemVo.setLabelInfoList(labelInfoList);
        apartmentItemVo.setMinRent(minRent);
        return apartmentItemVo;
    }

    @Override
    public ApartmentDetailVo selectApartmentDetailVoById(Long apartmentId) {
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(apartmentId);
        if (apartmentInfo==null){
            return  null;
        }

        //公寓下的标签信息列表
        List<LabelInfo> labelInfoList =labelInfoMapper.selectLabelInfoById(apartmentId);

        //获取公寓相关的图片
        List<GraphVo> graphVoList=graphInfoMapper.selectGraphVoById(ItemType.APARTMENT,apartmentId);

        //获取 配套列表
        //private List<FacilityInfo> facilityInfoList;
        List<FacilityInfo> facilityInfoList=facilityInfoMapper.selectFacilityByApartmentId(apartmentId);

        //公寓租金最小值
        BigDecimal minRent=roomInfoMapper.selectMinRentByApartmentId(apartmentId);

        //公寓信息是否被删除 private Boolean isDelete;
        Boolean isDelete =apartmentInfo.getIsDeleted() !=0;

        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo,apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setMinRent(minRent);
        apartmentDetailVo.setIsDelete(isDelete);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        return apartmentDetailVo;
    }
}




