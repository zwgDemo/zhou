<template>
  <div class="app-container">
    <el-form ref="queryRef" :inline="true" label-width="68px">
      <el-form-item label="分类">
        <el-cascader
                     :props="categoryProps"
                     v-model="queryCategoryIdList"
                     @change="handleCategoryChange"
                     style="width: 100%"
                     />
      </el-form-item>

      <el-form-item label="品牌">
        <el-select
           v-model="queryParams.brandId"
            class="m-2"
            placeholder="选择品牌"
            size="small"
            style="width: 100%"
        >
          <el-option
              v-for="item in brandList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" id="reset-all">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus" @click="handleAdd">新增</el-button>
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
            icon="Delete" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar></right-toolbar>
    </el-row>

    <!-- 数据展示表格 -->
    <el-table v-loading="loading" :data="categoryBrandList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="分类名称" prop="categoryName" />
      <el-table-column label="品牌名称" prop="brandName" />
      <el-table-column prop="logo" label="品牌图标" #default="scope">
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

     <!-- 添加或修改分类品牌对话框 -->
  <el-dialog :title="title" v-model="open" :rules="rules" width="500px" append-to-body>
    <el-form ref="categoryBrandRef" :model="form" label-width="80px">
      <el-form-item label="分类" prop="categoryIdList">
        <el-cascader
            :props="categoryProps"
            v-model="form.categoryIdList"
        />
      </el-form-item>
      <el-form-item label="品牌" prop="brandId">
        <el-select
            v-model="form.brandId"
            class="m-2"
            placeholder="选择品牌"
            size="small"
        >
          <el-option
              v-for="item in brandList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
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
  
<script setup name="CategoryBrand">
//分类品牌列表接口
import { listCategoryBrand,addCategoryBrand,getCategoryBrand,updateCategoryBrand,delCategoryBrand} from "@/api/product/categoryBrand";
//查询所有品牌
import { getBrandAll } from "@/api/product/brand";
//查询分类懒加载
import { listCategory } from "@/api/product/category";

const { proxy } = getCurrentInstance();

// 定义搜索表单数据模型
const brandList = ref([])

//三级分类
const props = {
  lazy: true,
  value: 'id',
  label: 'name',
  leaf: 'leaf',
  async lazyLoad(node, resolve) {
    const { level } = node
    if (typeof node.value == 'undefined') node.value = 0
    const { code, data, message } = await listCategory(
        node.value
    )
    //hasChildren判断是否有子节点
    data.forEach(function(item) {
      item.leaf = !item.hasChildren
    })
    resolve(data)
  },
}
const categoryProps = ref(props)

////////////////////////////////删除
const ids = ref([])
// es5  es6  标准
// ecmascript 6.0
function handleSelectionChange(selection) {
  //selection数组遍历，获取每个id
  ids.value = selection.map(item => item.id)
}

function handleDelete(row) {

  const _ids = row.id || ids.value

  proxy.$modal.confirm('是否确认删除分类品牌？').then(function() {
    return delCategoryBrand(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}






/////////////////////////////////

//处理分类change事件
const queryCategoryIdList = ref([])
const handleCategoryChange = () => {
  if (queryCategoryIdList.value.length == 3) {
    queryParams.value.categoryId = queryCategoryIdList.value[2]
  }
}

//搜索
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList()
}
//定义分页列表数据模型
const categoryBrandList = ref([]);
//定义列表总记录数模型
const total = ref(0);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    brandId: null,
    categoryId: null
  },
  form:{},
  rules: {
    categoryIdList: [
      { required: true, message: "分类不能为空", trigger: "blur" }
    ],
    brandId: [
      { required: true, message: "品牌不能为空", trigger: "blur" }
    ]
  }
});
const { queryParams,form,rules } = toRefs(data);

////////////////////////////////修改
//数据回显
function handleUpdate(row) {
  //form.value = row
  const _id = row.id 
  getCategoryBrand(_id).then(response =>{
    form.value = response.data;
    open.value = true
    title.value = "修改分类品牌"
  })
}

////////////////////////////////
//弹出框变量 open
const open = ref(false)
//新增与修改弹出层标题模型
const title = ref("");
//弹出框的方法
function handleAdd() {
  open.value = true
  title.value = "新增分类品牌"
}
//确定添加方法
function submitForm() {
  proxy.$refs["categoryBrandRef"].validate(valid=>{
    if(valid) { //校验通过
      //获取第三级分类id值
      form.value.categoryId = form.value.categoryIdList[2]

      if (form.value.id != null) {
        //修改
        updateCategoryBrand(form.value).then(response => {
        //关闭弹框
        open.value = false
        //提示信息
        proxy.$modal.msgSuccess("修改成功");
        //刷新页面
        getList()
      })
      } else {
        addCategoryBrand(form.value).then(response => {
        //关闭弹框
        open.value = false
        //提示信息
        proxy.$modal.msgSuccess("新增成功");
        //刷新页面
        getList()
      })
      }
      
    }
  })
}

//列表
function getList() {
  listCategoryBrand(queryParams.value).then(response =>{
    categoryBrandList.value = response.rows
    total.value = response.total
  })
}
  
//加载所有品牌
function getBrandAllList() {
  getBrandAll().then(response => {
    brandList.value = response.data
  })
}
//调用
getList()
getBrandAllList()
</script>