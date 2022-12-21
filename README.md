# db-migration-verify

>Last update time: 21/12/2022

## a. Setup

- **Backend**

  - Start SpringBoot application class[`DbMigrationApplication.java`] directly 

  - Visit `http://[your_host_name]:8080/actuator/health` to ensure application health status
    - You can modify configuration in  `application.yml` If you want to custom the default server port
- **Frontend**

  - Using `npm install` command to install relevant dependencies
  - Using `npm run dev` to compile and hot-reload for development
    - Visit `http://[your_host_name]:3000` to access index page
  - Using `npm run build` to complie & build for production package

## b. Feature Introduction

Using this tool, you can gain insight into all the difference system variables and schema fields in two databases. Currently, we only support MySQL database.

### 1. Database Variable Compare

We can use API or Web UI to process database variables compare. It will return the different global variable fields between two databases in JSON format.

If you want to using by API, please request with the below format:

---

**Request Url:**

[POST] http://[your_host_name]:8080/database-variables/verify

**Request Body:**

```json
{
   "sourceDatasource":{
       "ipAddress": "input_your_source_database_ip_address",
       "port": 3306,
       "database": "input_your_database_name",
       "username": "root",
       "password": "root"
   },
   "targetDatasource":{
       "ipAddress": "input_your_target_database_ip_address",
       "port": 3306,
       "database": "input_your_database_name",
       "username": "root",
       "password": "root"
   }
}
```

**Response Body:**

```json
{
    "verifyResults": [
        {
            "variableName": "mysqlx_bind_address",
            "confirmed": false,
            "variableValue": {
                "sourceValue": "*",
                "targetValue": "VARIABLE_NOT_EXIST"
            }
        },
        {
            "variableName": "mysqlx_connect_timeout",
            "confirmed": false,
            "variableValue": {
                "sourceValue": "30",
                "targetValue": "VARIABLE_NOT_EXIST"
            }
        }
      ]
}
```

### 2. Database Content Compare

It will compare the below points between two difference database.

- Table Schema
- Table Engine
- Table Comment
- Column Name
- Column Type
- Data Row

---

**Database Compare API**

>This compare API will return a execution id. You can download a final validation CSV format report with the specific execution id.

**Request Url:**

[POST] http://[your_host_name]:8080/verify

**Request Body:**

```json
{
   "sourceDatasource":{
       "ipAddress": "input_your_source_database_ip_address",
       "port": 3306,
       "database": "input_your_database_name",
       "username": "root",
       "password": "root"
   },
   "targetDatasource":{
       "ipAddress": "input_your_target_database_ip_address",
       "port": 3306,
       "database": "input_your_database_name",
       "username": "root",
       "password": "root"
   }
}
```

**Response Body:**

```
4fb90e15-0947-4133-8d80-40e19823bdf5
```

---

**Validation Report Download API**

**Request Url:**

[GET] http://[your_host_name]:8080/verify-report/{execution-id}

**Report Example:**

![image-20221221145819708](https://zchengb-images.oss-cn-shenzhen.aliyuncs.com/image-20221221145819708.png)

## c. Contributor

- Thoughtworks - Shuai Shao
- Thoughtworks - Xupeng Ma
- Thoughtworks - Yuanxin Mao
- Thoughtworks - Longzhan Huang
- Thoughtworks - Yandi Lin
- Thoughtworks - Xiaobin Zheng