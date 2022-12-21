# <div align="center">db-migration-verify</div>

>Last update time: 21/12/2022

## 🥏 Local Setup

- **Backend**
  
  - Start SpringBoot application class[`DbMigrationApplication.java`] directly 
  - Visit `http://[your_host_name]:8080/actuator/health` to ensure application health status
    - You can modify configuration in  `application.yml` If you want to custom the default server port
- **Frontend**
  - Using `cd db-diff-frontend` command into `db-diff-frontend` directory
  - Using `npm install` command to install relevant dependencies
  - Using `npm run dev` to compile and hot-reload for development
    - Visit `http://[your_host_name]:5173` to access index page
  - Using `npm run build` to complie & build for production package
  

## 🛠️ Build Runnable Jar Package

If you want to deploy this project into your server instance, please execution the below commands to build a jar package.

```shell
$ cd db-diff-frontend
// npm run build will generate relevant static page resource into the src/main/resource directory
$ npm run build
$ cd ..
// bootJar will generate final jar package into ./build/libs directory
$ ./gradlew build bootJars
```

## 🌈 Feature Introduction

Using this tool, you can gain insight into all the difference system variables and schema fields in two databases. Currently, we only support MySQL database.

### 1. Database Variable Compare

We can use API or Web UI to process database variables compare. It will return the different global variable fields between two databases in JSON format.

---

If you want to use by Web UI directly, please execute `npm install && npm run dev` under the `db-diff-frontend` directory. 

And then visit `http://[your_host_name]:5173` to access index page like the below screenshoot.

![db-variable-compare-web-page](https://zchengb-images.oss-cn-shenzhen.aliyuncs.com/image-20221221154045561.png)

The database variable compare result is distinguish with two difference color about `orange` & `green`.

The `GREEN` rows means we have confirmed and no disagreement about it.

The `ORANGE` rows means we have not confirmed yet and it may occur issue that you should pay attention about it.

So if you have database variable fields already confirmed and want to make it into `GREEN` rows, please configure relevant field names into `application.yml` with the specific path `db-verify.target-confirmed-variables`

---

If you want to use by API, please request with the below format:

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

## 🪐 Copyright and License

This product is open source and free, and will continue to provide free community technical support. Individual or enterprise users are free to access and use.

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2022-present, Thoughtworks.

产品开源免费，并且将持续提供免费的技术支持。个人或企业内部可自由的接入和使用。如有需要可邮件联系作者免费获取项目授权

## 🫐 Contributor

- Thoughtworks - Shuai Shao
- Thoughtworks - Xupeng Ma
- Thoughtworks - Yuanxin Mao
- Thoughtworks - Longzhan Huang
- Thoughtworks - Yandi Lin
- Thoughtworks - Xiaobin Zheng