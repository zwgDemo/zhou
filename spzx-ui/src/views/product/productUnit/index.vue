<template>
    <div class="app-container">
  
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="名称" prop="name">
          <el-input
              v-model="queryParams.name"
              placeholder="请输入名称"
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
              icon="Plus"
              @click="handleAdd"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              type="success"
              plain
              icon="Edit"
              :disabled="single"
              @click="handleUpdate"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
          >删除</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
  
      <!-- 数据展示表格 -->
      <el-table v-loading="loading" :data="productUnitList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="名称" prop="name" width="200"/>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button link type="primary" icon="Delete"  @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 分页条组件 -->
      <pagination
          v-show="total>0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
      />
  
      <!-- 添加或修改商品单位对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
        <el-form ref="productUnitRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="单位名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入单位名称" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>
  
    </div>
  </template>
  
  <script setup name="ProductUnit">
  //引入api接口
  import {listProductUnit,addProductUnit } from "@/api/product/productUnit";
  const { proxy } = getCurrentInstance();
  
  //定义分页列表数据模型
  const productUnitList = ref([]);
  //定义列表总记录数模型
  const total = ref(0);
  //加载数据时显示的动效控制模型
//   const loading = ref(true);
  //定义隐藏搜索控制模型
  const showSearch = ref(true);
  //新增与修改弹出层标题模型
  const title = ref("");
  //新增与修改弹出层控制模型
  const open = ref(false);
  //定义批量操作id列表模型
  const ids = ref([]);

  const data = reactive({
    form: {},
    queryParams: {
      pageNum: 1,
      pageSize: 10,
      name: null
    },

    //required: true 不能为空
    //message: "单位名称不能为空"  提示信息
    // trigger: "blur"  什么时候触发校验  失去焦点
    rules: {
      name: [
        { required: true, message: "单位名称不能为空", trigger: "blur" }
      ]
    }

  });
  const { queryParams, form, rules } = toRefs(data);
  
  //列表
  function getList() {
    listProductUnit(queryParams.value).then(response =>{
        productUnitList.value = response.rows
        total.value = response.total
    })
  }

  //搜索
  function handleQuery() {
    getList()
  }

  //添加
  //弹出框
  function handleAdd() {
    open.value = true
    title.value = "添加商品单位"
  }

  function submitForm() {
    proxy.$refs["productUnitRef"].validate(valid => {
      if(valid) { //校验通过
        //添加
        if (form.value.id != null) {
            //修改

        } else {//添加
            addProductUnit(form.value).then(response => {
                open.value = false
                proxy.$modal.msgSuccess("新增成功");
                getList()
            })
        }
      }
    })


    
  }

  getList()
</script>