package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
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
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Resource
    private ApartmentInfoMapper apartmentInfoMapper;

    @Resource
    private GraphInfoMapper graphInfoMapper;

    @Resource
    private LabelInfoMapper labelInfoMapper;

    @Resource
    private FacilityInfoMapper facilityInfoMapper;

    @Resource
    private FeeValueMapper feeValueMapper;

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private ProvinceInfoMapper provinceInfoMapper;

    @Resource
    private CityInfoMapper cityInfoMapper;

    @Resource
    private DistrictInfoMapper districtInfoMapper;

    @Resource
    private GraphInfoService graphInfoService;

    @Resource
    private ApartmentFacilityService apartmentFacilityService;

    @Resource
    private ApartmentLabelService apartmentLabelService;

    @Resource
    private ApartmentFeeValueService apartmentFeeValueService;


    @Override
    public void saveOrUpdateApartmentInfo(ApartmentSubmitVo apartmentSubmitVo) {
        //此处由于前段没有将地区名称传给我们后台，所以我们需要在后端自己根据地区ID查询名称传入apartmentSubmitVo
        DistrictInfo districtInfo = districtInfoMapper.selectById(apartmentSubmitVo.getDistrictId());
        apartmentSubmitVo.setDistrictName(districtInfo.getName());

        CityInfo cityInfo = cityInfoMapper.selectById(apartmentSubmitVo.getCityId());
        apartmentSubmitVo.setCityName(cityInfo.getName());

        ProvinceInfo provinceInfo = provinceInfoMapper.selectById(apartmentSubmitVo.getProvinceId());
        apartmentSubmitVo.setProvinceName(provinceInfo.getName());


        boolean isUpdate =apartmentSubmitVo.getId()!=null;
        super.saveOrUpdate(apartmentSubmitVo);
            if(!isUpdate){
            //先删除列表信息，再去上传新的列表信息
            //图片的删除
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphQueryWrapper.eq(GraphInfo::getItemId,apartmentSubmitVo.getId());
            graphInfoService.remove(graphQueryWrapper);

            //配套信息的删除
            LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(ApartmentFacility::getApartmentId,apartmentSubmitVo.getId());
            apartmentFacilityService.remove(facilityQueryWrapper);

            //标签信息的删除
            LambdaQueryWrapper<ApartmentLabel> LabelQueryWrapper = new LambdaQueryWrapper<>();
            LabelQueryWrapper.eq(ApartmentLabel::getApartmentId,apartmentSubmitVo.getId());
            apartmentLabelService.remove(LabelQueryWrapper);

            //杂费信息的删除
            LambdaQueryWrapper<ApartmentFeeValue> feeValueQueryWrapper = new LambdaQueryWrapper<>();
            feeValueQueryWrapper.eq(ApartmentFeeValue::getApartmentId,apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(feeValueQueryWrapper);
        }


        //添加图片信息
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if(!CollectionUtils.isEmpty(graphVoList)){
            ArrayList<GraphInfo> infoArrayList = new ArrayList<>();
            for (GraphVo graphVo:graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                //为了传入GraphInfo，自定义GraphInfo并设置值，添加到新的arraylist集合里
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                infoArrayList.add(graphInfo);
            }
            graphInfoService.saveBatch(infoArrayList);
        }


        //添加配套信息
        List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
        if(!CollectionUtils.isEmpty(facilityInfoIds)){
            ArrayList<ApartmentFacility> apartmentFacilities = new ArrayList<>();
            for (Long facilityId: facilityInfoIds) {
                ApartmentFacility apartmentFacility = new ApartmentFacility();
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                apartmentFacility.setFacilityId(facilityId);
                apartmentFacilities.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(apartmentFacilities);
        }


        //添加标签信息

        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)){
            ArrayList<ApartmentLabel> apartmentLabels = new ArrayList<>();
            for (Long id: labelIds) {
                ApartmentLabel apartmentLabel = new ApartmentLabel();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(id);
                apartmentLabels.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(apartmentLabels);
        }

        //添加杂费信息
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)){
            ArrayList<ApartmentFeeValue> apartmentFeeValues = new ArrayList<>();
            for ( Long id: feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(id);
                apartmentFeeValues.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(apartmentFeeValues);
        }
    }


    @Override
    public IPage<ApartmentItemVo> ApartmentQueryPage(IPage<ApartmentItemVo> apartmentInfoPage, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.ApartmentQueryPage(apartmentInfoPage, queryVo);
    }

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        //此处先将apartInfo获取出来，后面再讲apartmentDetailVo里的各个集合拼接进来
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);

        //查询图片列表
        List<GraphVo>  graphVoList=graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT,id);

        //查询标签列表
        List<LabelInfo> labelInfoList= labelInfoMapper.selectListById(id);

        //查询配套标签
        List<FacilityInfo> facilityInfoList=facilityInfoMapper.selectListById(id);

        //查询杂费列表
        List<FeeValueVo> feeValueVoList=feeValueMapper.selectListById(id);

        //合并集合
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo,apartmentDetailVo);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setGraphVoList(graphVoList);
        return apartmentDetailVo;
    }

    @Override
    public void removeApartmentById(Long id) {
        LambdaQueryWrapper<RoomInfo> roomInfoQueryWrapper = new LambdaQueryWrapper<>();
        roomInfoQueryWrapper.eq(RoomInfo::getApartmentId,id);
        Long count = roomInfoMapper.selectCount(roomInfoQueryWrapper);

        if (count>0){
            throw new LeaseException(ResultCodeEnum.ADMIN_APARTMENT_DELETE_ERROR);
        }

        super.removeById(id);

        //图片的删除
        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphQueryWrapper.eq(GraphInfo::getItemId,id);
        graphInfoService.remove(graphQueryWrapper);

        //配套信息的删除
        LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(ApartmentFacility::getApartmentId,id);
        apartmentFacilityService.remove(facilityQueryWrapper);

        //标签信息的删除
        LambdaQueryWrapper<ApartmentLabel> LabelQueryWrapper = new LambdaQueryWrapper<>();
        LabelQueryWrapper.eq(ApartmentLabel::getApartmentId,id);
        apartmentLabelService.remove(LabelQueryWrapper);

        //杂费信息的删除
        LambdaQueryWrapper<ApartmentFeeValue> feeValueQueryWrapper = new LambdaQueryWrapper<>();
        feeValueQueryWrapper.eq(ApartmentFeeValue::getApartmentId,id);
        apartmentFeeValueService.remove(feeValueQueryWrapper);
    }
}




