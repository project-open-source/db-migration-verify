<template>
    <div class="tool-wrapper">
        <h1 @click="this.toggleForm">Db-Diff-Tools</h1>
        <el-form ref="form" :model="request" label-width="80px" v-if="showForm">
            <el-form-item label="请求参数">
                <el-input type="textarea" v-model="request.requestBody" height="200px"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSubmit">立即查询</el-button>
            </el-form-item>
        </el-form>
        <el-tag type="success">{{sourceDatabase}}</el-tag>
        <el-tag type="success">{{targetDatabase}}</el-tag>
        <el-table
                :data="tableData"
                style="width: 100%"
                :row-class-name="tableRowClassName"
                :default-sort = "{prop: 'confirmed', order: 'descend'}"
        >
            <el-table-column
                    type="index"
                    width="50">
            </el-table-column>
            <el-table-column
                    sortable
                    prop="variableName"
                    label="数据库变量名">
            </el-table-column>
            <el-table-column
                    prop="variableValue.sourceValue"
                    label="源数据库的值">
            </el-table-column>
            <el-table-column
                    prop="variableValue.targetValue"
                    label="目标数据库的值">
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
export default {
    name: "VerifDb",
    methods: {
        tableRowClassName({row}) {
            if (row.confirmed) {
                return 'success-row';
            }
            return 'warning-row';
        },
        onSubmit() {
            fetch(`${window.location.origin}/ftms-db-migration-verify/database-variables/verify`, {
                headers: {
                    "Content-Type": "application/json"
                },
                method: 'POST',
                body: this.request.requestBody,
            })
                    .then(response => response.json())
                    .then(res => {
                        this.tableData = res.verifyResults
                        this.source = res.sourceDatabase
                        this.target = res.targetDatabase
                    })
                    .catch(err => console.log(err));
        },
        toggleForm(){
            this.showForm = !this.showForm;
        }
    },
    data() {
        return {
            tableData: [],
            request: {
                requestBody: ""
            },
            showForm:true,
            source:"",
            target:""
        }
    },
    computed:{
        sourceDatabase(){
            return `源数据库:${this.source}`;
        },
        targetDatabase(){
            return `目标数据库:${this.target}`;
        }
    }
}
</script>

<style scoped>
  .el-table .warning-row {
      background: oldlace;
  }

  .el-table .success-row {
      background: #f0f9eb;
  }

  .tool-wrapper {
      padding: 1rem 2rem;
  }
</style>

<style>
  textarea {
      height: 200px;
  }
</style>