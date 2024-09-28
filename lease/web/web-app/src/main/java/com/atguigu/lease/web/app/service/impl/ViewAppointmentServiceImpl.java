package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.ViewAppointment;
import com.atguigu.lease.web.app.mapper.ViewAppointmentMapper;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.ViewAppointmentService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentItemVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ViewAppointmentServiceImpl extends ServiceImpl<ViewAppointmentMapper, ViewAppointment>
        implements ViewAppointmentService {

    @Resource
    private ViewAppointmentMapper viewAppointmentMapper;

    @Resource
    private ApartmentInfoService apartmentInfoService;


    @Override
    public List<AppointmentItemVo> getAppointmentItemVoList(Long userId) {
        List<AppointmentItemVo> result=viewAppointmentMapper.getAppointmentItemVoList(userId);
        return result;
    }

    @Override
    public AppointmentDetailVo getDetailById(Long id) {
        ViewAppointment viewAppointment = viewAppointmentMapper.selectById(id);
        if(viewAppointment==null){
            return null;
        }
        ApartmentItemVo apartmentItemVo = apartmentInfoService.selectApartmentVoById(viewAppointment.getApartmentId());
        AppointmentDetailVo appointmentDetailVo = new AppointmentDetailVo();
        BeanUtils.copyProperties(viewAppointment,appointmentDetailVo);
        appointmentDetailVo.setApartmentItemVo(apartmentItemVo);
        return appointmentDetailVo;
    }
}



