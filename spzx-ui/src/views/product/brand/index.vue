<template>
    <div class="app-container">
  
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
        <el-form-item label="品牌名称" prop="name">
            <el-input
                v-model="queryParams.name"
                placeholder="请输入品牌名称"
                clearable
            />
        </el-form-item>
        <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
  
      <!-- 功能按钮栏 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus" @click="toAddPage" v-hasPermi="['product:brand:add']">
            新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="Edit"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete" @click="handleDelete" v-hasPermi="['product:brand:delete']">删除</el-button>
        </el-col>
      </el-row>
  
      <!-- 数据展示表格 -->
      <el-table :data="brandList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="品牌名称" prop="name" width="200"/>
        <el-table-column label="品牌图标" prop="logo" #default="scope">
          <img :src="scope.row.logo" width="50" />
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope"> 
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 新增或修改分类品牌对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
        <el-form ref="brandRef" :model="form" label-width="80px">
            <el-form-item label="品牌名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入品牌名称" />
            </el-form-item>
            <el-form-item label="品牌图标" prop="logo">
                <el-upload
                          class="avatar-uploader"
                          :action="imgUpload.url"
                          :headers="imgUpload.headers"
                          :show-file-list="false"
                          :on-success="handleAvatarSuccess"
                          >
                    <img v-if="form.logo" :src="form.logo" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button type="primary" @click="submitForm">确 定</el-button>
                <el-button>取 消</el-button>
            </div>
        </template>
    </el-dialog>

      <!-- 分页条组件 -->
      <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>
  </template>
  
<script setup name="Brand">
//1 引入定义接口信息js文件
import {listBrand,addBrand,getBrand,updateBrand,delBrand} from "@/api/product/brand";
import { getToken } from "@/utils/auth.js";
import {ElMessage,ElMessageBox} from "element-plus";
// import { getCurrentInstance } from "vue";

//在vue封装方法，作用：获取当前激活的vue对象，类似于java里面this
// const { proxy } = getCurrentInstance();
// proxy.$modal.msgSuccess("新增成功");


//2 定义需要使用数据模型（变量）
const brandList = ref([])
const total = ref(0)

//定义分页和条件查询相关参数
const data = reactive({
    queryParams:{
        pageNum: 1,
        pageSize: 3,
        name:null //品牌名称
    },

    form: {},
    imgUpload: {
      // 设置上传的请求头部
      headers: {
        Authorization: "Bearer " + getToken()
      },
      // 图片上传的方法地址:
      url: import.meta.env.VITE_APP_BASE_API + "/file/upload"
    }
})
//reactive == ref
const { queryParams, form, imgUpload } = toRefs(data);

//===========================删除
//定义批量操作id列表模型
const ids = ref([]);

function handleDelete(row) {
  //获取删除品牌id
  const _ids = row.id || ids.value ;
  //添加确认框
  ElMessageBox.confirm('是否确认删除分类品牌？', "系统提示", {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: "warning",
  }).then(function() {
    return delBrand(_ids);
  }).then(() => {
    getList();
    ElMessage.success("删除成功");
  }).catch(() => {});
}

//获取选择复选框的值
function handleSelectionChange(selection) {
  //selection数组
  //从selection数组获取所有id值
  ids.value = selection.map(item => item.id);
}
//========================================
//===========================修改
function handleUpdate(row) {
  //form.value = row

  //调用后端接口得到数据
  getBrand(row.id).then(response =>{
    form.value = response.data;
    //弹框
    open.value = true
    title.value = "修改品牌"
  })
  
}
//============================

//============================
//============================列表
//3 编写方法发送ajax请求得到数据
function getList() {
    listBrand(queryParams.value)
            .then(response => {  //调用成功 response后端返回数据
               // response.rows赋值给brandList
               brandList.value = response.rows
               total.value = response.total
            }) 
}

//搜索方法
function handleQuery() {
    getList()
}
//重置
function resetQuery() {
  queryParams.value.pageNum = 1
  queryParams.value.pageSize = 3
  queryParams.value.name = null
  handleQuery();
}
//列表方法，加载页面调用这个方法
// onMounted(()=>{
//     getList()
// })
//============================

//============================
//============================添加
const open = ref(false)
const title = ref("");
// 表单重置
function reset() {
  form.value = {
    id: null,
    name: null,
    logo: null
  };
}
//添加弹框方法
function toAddPage() {
  reset()
  open.value = true
  title.value = "添加品牌"
}
//上传成功之后的方法
//上传
function handleAvatarSuccess (response, uploadFile) {
  form.value.logo = response.data.url
}
//添加修改方法
function submitForm() {
  //判断对象里面是否有id，有id修改，没有id添加
  if (form.value.id != null) {
    //修改
    updateBrand(form.value).then(response=>{
      //关闭弹框
      open.value = false
      //提示信息
      ElMessage.success("修改成功")
      //刷新页面
      getList()
    })
  } else {//没有id，添加
    addBrand(form.value).then(response=>{
      //关闭弹框
      open.value = false
      //提示信息
      ElMessage.success("添加成功")      
      //刷新页面
      getList()
  })
  }
}

getList();
</script>

<style scoped>
    .avatar-uploader .avatar {
        width: 178px;
        height: 178px;
        display: block;
    }
</style>

<style>
    .avatar-uploader .el-upload {
        border: 1px dashed var(--el-border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
    }

    .avatar-uploader .el-upload:hover {
        border-color: var(--el-color-primary);
    }

    .el-icon.avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 178px;
        height: 178px;
        text-align: center;
    }
</style>