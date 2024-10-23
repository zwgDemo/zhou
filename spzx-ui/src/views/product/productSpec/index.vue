<template>
    <div class="app-container">
      <el-form ref="queryRef" :inline="true" label-width="68px">
        <el-form-item label="分类">
          <el-cascader
              :props="categoryProps"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="规格名称" prop="specName">
          <el-input
              placeholder="请输入规格名称"
              clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search">搜索</el-button>
          <el-button icon="Refresh">重置</el-button>
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
              icon="Delete"
          >删除</el-button>
        </el-col>
        <right-toolbar></right-toolbar>
      </el-row>
  
      <el-table :data="specList">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="分类名称" prop="categoryName" width="120"/>
        <el-table-column label="规格名称" align="left" prop="specName" width="120"/>
        <el-table-column label="规格值" #default="scope" >
          <div
              v-for="(item1, index1) in scope.row.specValueList"
              :key="index1"
              style="padding: 5px; margin: 0;width: 100%;"
          >
            {{ item1.key }}：
            <span
                v-for="(item2, index2) in item1.valueList"
                :key="index2"
                class="div-atrr"
            >
            {{ item2 }}
          </span>
          </div>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"/>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
          <template #default="scope">
            <el-button link type="primary" icon="Edit">修改</el-button>
            <el-button link type="primary" icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 添加或修改商品规格对话框 -->
<el-dialog :title="title" v-model="open" width="500px" append-to-body>
  <el-form ref="specRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="分类" prop="categoryIdList">
      <el-cascader
          :props="categoryProps"
          v-model="form.categoryIdList"
      />
    </el-form-item>
    <el-form-item label="规格名称" prop="specName">
      <el-input v-model="form.specName" placeholder="请输入规格名称" />
    </el-form-item>
    <el-form-item>
      <div
          v-for="(item1, index1) in specValueList"
          :key="index1"
          style="padding: 10px; margin: 0;"
      >
        {{ item1.key }}：
        <span
            v-for="(item2, index2) in item1.valueList"
            :key="index2"
            class="div-atrr"
        >
        {{ item2 }}
      </span>
        <el-button size="small" @click="removeAttr(index1)">删除</el-button>
        <br />
      </div>
    </el-form-item>
    <el-form-item label="">
      <el-row v-if="isAdd">
        <el-col :span="10">
          <el-input
              v-model="specValue.key"
              placeholder="规格"
              style="width: 90%;"
          />
        </el-col>
        <el-col :span="10">
          <el-input
              v-model="specValue.values"
              placeholder="规格值(如:白色,红色)"
              style="width: 90%;"
          />
        </el-col>
        <el-col :span="4">
          <el-button size="default" @click="addSpecValue">添加</el-button>
        </el-col>
      </el-row>
      <el-row v-if="!isAdd">
        <el-col :span="4" align="left">
          <el-button size="default" @click="isAdd = true">
            添加新规格
          </el-button>
        </el-col>
      </el-row>
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
          v-show="total > 0"
          :total="total"
      />
  
    </div>
  </template>
  
  <script setup>
  import { listSpec,addSpec } from "@/api/product/productSpec";
  
  // 表格数据模型
  const specList = ref([])
  
  const props = {
    lazy: true,
    value: 'id',
    label: 'name',
    leaf: 'leaf',
    lazyLoad(node, resolve) {   // 加载数据的方法
      const data = [
        {
          "id": 643,
          "createTime": "2023-05-22 15:31:18",
          "name": "玩具乐器",
          "imageUrl": "https://lilishop-oss.oss-cn-beijing.aliyuncs.com/0f423fb60f084b2caade164fae25a9a0.png",
          "parentId": 0,
          "status": 1,
          "orderNum": 10,
          "hasChildren": true,
          "children": null
        },
        {
          "id": 576,
          "createTime": "2023-05-22 15:31:13",
          "name": "汽车用品",
          "imageUrl": "https://lilishop-oss.oss-cn-beijing.aliyuncs.com/665dd952b54e4911b99b5e1eba4b164f.png",
          "parentId": 0,
          "status": 1,
          "orderNum": 10,
          "hasChildren": true,
          "children": null
        },
      ]
      resolve(data)  // 返回数据
    }
  };
  const categoryProps = ref(props)
  
  // 分页条数据模型
  const total = ref(0)
  
//新增与修改弹出层控制模型
const open = ref(false);
//新增与修改弹出层标题模型
const title = ref("");

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10
  },
  form: {},
  rules: {
    categoryIdList: [
      { required: true, message: "分类不能为空", trigger: "blur" }
    ],
    specName: [
      { required: true, message: "规则名称不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams,form,rules } = toRefs(data);

//编辑规格属性
const isAdd = ref(false)
const specValue = ref({ key: '', values: '' })
const specValueList = ref([])

function addSpecValue() {
  //push方法向数组添加数据
  specValueList.value.push({
    key: specValue.value.key,
    valueList: specValue.value.values.split(','),
  })
  specValue.value = {}
  isAdd.value = false
}
function removeAttr(index) {
  specValueList.value.splice(index, 1)
}
//弹框
function handleAdd() {
    //debugger
    open.value = true
    title.value = "添加商品规格";

    specValueList.value = []
}
//分页列表的方法
function getList() {
    listSpec(queryParams.value).then(response => {
        specList.value = response.rows

        //specList规格数据有specValue
        // "[{...},{...}]" ==> [{...},{...}]
        // json:对象{} 数组[]
        // 使用js里面JSON,在JSON里面有方法parse ,去掉引号，转换json标准格式
        // specList.value.forEach(item => {
        //     item.specValueList = JSON.parse(item.specValue)
        // })

        specList.value.forEach(function(item) {
            item.specValueList = JSON.parse(item.specValue)
        })

        total.value = response.total
    })
}

function submitForm() {
    proxy.$refs["specRef"].validate(valid => {
    if (valid) {
      // [{'颜色':'绿色,蓝色'}]  ==> "[{'颜色':'绿色,蓝色'}]"
      form.value.specValue = JSON.stringify(specValueList.value)

      //系统只需要三级分类id
      form.value.categoryId = form.value.categoryIdList[2]

      addSpec(form.value).then(response => {
        proxy.$modal.msgSuccess("新增成功");
        open.value = false;
        getList();
      });
    }
  });
}

//调用
getList()
</script>
  <style scoped>
  .div-atrr {
    padding: 5px 10px;
    margin: 0 10px;
    background-color: powderblue;
    border-radius: 10px;
  }
  </style>